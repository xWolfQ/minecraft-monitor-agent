package com.github.xwolfq.monitor.collector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

/**
 * Zbiera skumulowany ruch sieciowy serwera (bajty odebrane i wysłane) ze wszystkich
 * interfejsów sieciowych z wyjątkiem loopback.
 *
 * <p>Źródłem danych jest {@code /proc/net/dev}, dostępny wyłącznie na Linuxie.
 * Na innych platformach kolektor zwraca {@code 0} dla obu kierunków
 * i loguje jednorazowe ostrzeżenie przy pierwszym wywołaniu.</p>
 *
 * <p>Zwracane wartości są skumulowane od startu systemu, nie od startu JVM.
 * Backend powinien obliczać delty między kolejnymi migawkami.</p>
 *
 */
public class NetworkCollector implements MetricCollector<NetworkCollector.Result> {

    private static final Path PROC_NET_DEV = Path.of("/proc/net/dev");

    private final Logger logger;

    /** Flaga zapobiegająca wielokrotnemu logowaniu ostrzeżenia o braku wsparcia platformy. */
    private boolean unsupportedWarningLogged = false;

    public NetworkCollector(Logger logger) {
        this.logger = logger;
    }

    /**
     * Migawka ruchu sieciowego z jednego cyklu zbierania.
     *
     * @param bytesIn  łączna liczba bajtów odebranych przez wszystkie interfejsy (bez loopback)
     * @param bytesOut łączna liczba bajtów wysłanych przez wszystkie interfejsy (bez loopback)
     */
    public record Result(long bytesIn, long bytesOut) {

        /** Pusty wynik zwracany gdy platforma nie udostępnia danych sieciowych. */
        static final Result UNAVAILABLE = new Result(0L, 0L);
    }

    /**
     * Zwraca skumulowany ruch sieciowy ze wszystkich interfejsów (bez loopback).
     *
     * <p>Na platformach innych niż Linux zwraca {@link Result#UNAVAILABLE}.</p>
     *
     * @return migawka bajtów in/out; {@code 0} gdy platforma nie jest obsługiwana
     *         lub odczyt {@code /proc/net/dev} się nie powiódł
     */
    @Override
    public Result collect() {
        if (!Files.exists(PROC_NET_DEV)) {
            logUnsupportedOnce();
            return Result.UNAVAILABLE;
        }

        try {
            return parseNetDev(Files.readAllLines(PROC_NET_DEV));
        } catch (IOException | NumberFormatException e) {
            logger.warning("Nie można odczytać /proc/net/dev: " + e.getMessage());
            return Result.UNAVAILABLE;
        }
    }

    /**
     * Parsuje zawartość {@code /proc/net/dev} i sumuje ruch ze wszystkich
     * interfejsów z wyjątkiem {@code lo}.
     *
     * @param lines linie pliku {@code /proc/net/dev}
     * @return zsumowany ruch in/out w bajtach
     */
    private Result parseNetDev(List<String> lines) {
        long totalIn  = 0;
        long totalOut = 0;

        for (var line : lines) {
            int colon = line.indexOf(':');
            if (colon < 0) continue;

            var iface = line.substring(0, colon).trim();
            if ("lo".equals(iface)) continue;

            var parts = line.substring(colon + 1).trim().split("\\s+");
            if (parts.length < 9) continue;

            totalIn  += Long.parseLong(parts[0]); // rx bytes
            totalOut += Long.parseLong(parts[8]); // tx bytes
        }

        return new Result(totalIn, totalOut);
    }

    private void logUnsupportedOnce() {
        if (!unsupportedWarningLogged) {
            logger.warning("Metryki sieciowe niedostępne — /proc/net/dev nie istnieje (non-Linux).");
            unsupportedWarningLogged = true;
        }
    }
}
