package com.github.xwolfq.monitor.model;

import java.util.List;

/**
 * Główny obiekt wysyłany do backendu w każdym cyklu zbierania metryk.
 * Serializowany przez Gson do JSON i przesyłany HTTP POST z nagłówkiem {@code X-API-Key}.
 */
public class MetricsPayload {

    /** Moment zebrania metryk w formacie ISO-8601 UTC, np. {@code 2026-03-29T10:00:00Z}. */
    private final String timestamp;

    /** Metryki globalne serwera (JVM, GC, CPU, sieć). Zawsze obecne. */
    private final GlobalMetrics global;

    /**
     * Metryki poszczególnych światów. Pusta lista gdy żaden gracz nie jest online —
     * w takim trybie backend otrzymuje wyłącznie {@link #global}.
     */
    private final List<WorldMetrics> worlds;

    public MetricsPayload(String timestamp, GlobalMetrics global, List<WorldMetrics> worlds) {
        this.timestamp = timestamp;
        this.global    = global;
        this.worlds    = worlds;
    }

    public String getTimestamp()          { return timestamp; }
    public GlobalMetrics getGlobal()      { return global; }
    public List<WorldMetrics> getWorlds() { return worlds; }
}
