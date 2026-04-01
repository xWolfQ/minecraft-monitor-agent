package com.github.xwolfq.monitor.collector;

import org.bukkit.Bukkit;

/**
 * Zbiera TPS i MSPT z serwera Paper.
 *
 */
public class TpsCollector implements MetricCollector<TpsCollector.Result> {

    /**
     * Migawka metryk TPS/MSPT z jednego cyklu zbierania.
     *
     * @param tps  TPS z ostatniej minuty
     * @param mspt średni czas wykonania jednego ticka w milisekundach
     */
    public record Result(double tps, double mspt) {}

    /**
     * Zwraca aktualny TPS (średnia z ostatniej minuty) i MSPT serwera.
     *
     * @return migawka TPS i MSPT; {@code tps} pochodzi z indeksu 0 tablicy
     *         zwracanej przez {@code Server#getTPS()} (średnia 1-minutowa)
     */
    @Override
    public Result collect() {
        double[] tpsArray = Bukkit.getServer().getTPS();
        double mspt = Bukkit.getServer().getAverageTickTime();
        return new Result(tpsArray[0], mspt);
    }
}
