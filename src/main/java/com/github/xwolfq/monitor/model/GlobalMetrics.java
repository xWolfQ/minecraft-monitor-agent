package com.github.xwolfq.monitor.model;

import com.google.gson.annotations.SerializedName;

/**
 * Metryki globalne serwera zbierane przy każdym cyklu,
 * niezależnie od tego czy gracze są online.
 */
public class GlobalMetrics {

    /** Ticki na sekundę (średnia z ostatniej minuty). Wartość nominalna: 20.0. */
    private final double tps;

    /** Średni czas wykonania jednego ticka w milisekundach (mean server process time). */
    private final double mspt;

    /** Zużyta pamięć sterty JVM w bajtach. */
    @SerializedName("ram_used")
    private final long ramUsed;

    /** Maksymalna dostępna pamięć sterty JVM w bajtach. */
    @SerializedName("ram_max")
    private final long ramMax;

    /** Obciążenie CPU procesu JVM w procentach (0.0–100.0). */
    private final double cpu;

    /** Liczba wykonanych cykli GC młodej generacji od startu JVM. */
    @SerializedName("gc_young_count")
    private final long gcYoungCount;

    /** Łączny czas spędzony w GC młodej generacji od startu JVM, w milisekundach. */
    @SerializedName("gc_young_time")
    private final long gcYoungTime;

    /** Liczba wykonanych cykli GC starej generacji od startu JVM. */
    @SerializedName("gc_old_count")
    private final long gcOldCount;

    /** Łączny czas spędzony w GC starej generacji od startu JVM, w milisekundach. */
    @SerializedName("gc_old_time")
    private final long gcOldTime;

    /** Liczba aktywnych wątków JVM. */
    @SerializedName("thread_count")
    private final int threadCount;

    /** Liczba aktualnie załadowanych klas JVM. */
    @SerializedName("loaded_classes")
    private final int loadedClasses;

    /** Bajty odebrane przez interfejs sieciowy od startu JVM. */
    @SerializedName("network_in")
    private final long networkIn;

    /** Bajty wysłane przez interfejs sieciowy od startu JVM. */
    @SerializedName("network_out")
    private final long networkOut;

    public GlobalMetrics(double tps, double mspt, long ramUsed, long ramMax, double cpu,
                         long gcYoungCount, long gcYoungTime, long gcOldCount, long gcOldTime,
                         int threadCount, int loadedClasses, long networkIn, long networkOut) {
        this.tps           = tps;
        this.mspt          = mspt;
        this.ramUsed       = ramUsed;
        this.ramMax        = ramMax;
        this.cpu           = cpu;
        this.gcYoungCount  = gcYoungCount;
        this.gcYoungTime   = gcYoungTime;
        this.gcOldCount    = gcOldCount;
        this.gcOldTime     = gcOldTime;
        this.threadCount   = threadCount;
        this.loadedClasses = loadedClasses;
        this.networkIn     = networkIn;
        this.networkOut    = networkOut;
    }

    public double getTps()           { return tps; }
    public double getMspt()          { return mspt; }
    public long getRamUsed()         { return ramUsed; }
    public long getRamMax()          { return ramMax; }
    public double getCpu()           { return cpu; }
    public long getGcYoungCount()    { return gcYoungCount; }
    public long getGcYoungTime()     { return gcYoungTime; }
    public long getGcOldCount()      { return gcOldCount; }
    public long getGcOldTime()       { return gcOldTime; }
    public int getThreadCount()      { return threadCount; }
    public int getLoadedClasses()    { return loadedClasses; }
    public long getNetworkIn()       { return networkIn; }
    public long getNetworkOut()      { return networkOut; }
}
