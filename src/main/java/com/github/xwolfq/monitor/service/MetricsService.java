package com.github.xwolfq.monitor.service;

import com.github.xwolfq.monitor.collector.CpuCollector;
import com.github.xwolfq.monitor.collector.GarbageCollectorCollector;
import com.github.xwolfq.monitor.collector.JvmCollector;
import com.github.xwolfq.monitor.collector.MemoryCollector;
import com.github.xwolfq.monitor.collector.NetworkCollector;
import com.github.xwolfq.monitor.collector.TpsCollector;
import com.github.xwolfq.monitor.collector.WorldMetricsCollector;
import com.github.xwolfq.monitor.model.GlobalMetrics;
import com.github.xwolfq.monitor.model.MetricsPayload;

import java.time.Instant;
import java.util.logging.Logger;

/**
 * Składa kompletny {@link MetricsPayload} z pojedynczych kolektorów metryk.
 *
 * <p>Zbiera metryki globalne (TPS/MSPT, pamięć, CPU, GC, JVM, sieć) zawsze,
 * a metryki światów wyłącznie dla światów z graczami — patrz {@link WorldMetricsCollector}.</p>
 *
 * <p>Klasa jest współdzielona przez zadanie cykliczne agenta oraz komendę
 * {@code /monitor send}, która wymusza natychmiastowe wysłanie statystyk.</p>
 */
public class MetricsService {

    private final CpuCollector cpuCollector = new CpuCollector();
    private final MemoryCollector memoryCollector = new MemoryCollector();
    private final GarbageCollectorCollector gcCollector = new GarbageCollectorCollector();
    private final JvmCollector jvmCollector = new JvmCollector();
    private final TpsCollector tpsCollector = new TpsCollector();
    private final NetworkCollector networkCollector;
    private final WorldMetricsCollector worldMetricsCollector = new WorldMetricsCollector();

    public MetricsService(Logger logger) {
        this.networkCollector = new NetworkCollector(logger);
    }

    /**
     * Zbiera aktualne metryki ze wszystkich kolektorów i zwraca kompletny payload.
     *
     * <p>Wymaga wywołania z głównego wątku serwera — część kolektorów korzysta z API
     * Bukkita (światy, encje).</p>
     *
     * @return kompletna migawka metryk gotowa do wysyłki
     */
    public MetricsPayload collect() {
        var cpu = cpuCollector.collect();
        var memory = memoryCollector.collect();
        var gc = gcCollector.collect();
        var jvm = jvmCollector.collect();
        var tps = tpsCollector.collect();
        var network = networkCollector.collect();

        var global = new GlobalMetrics(
                tps.tps(),
                tps.mspt(),
                memory.ramUsed(),
                memory.ramMax(),
                cpu.cpuPercent(),
                gc.youngCount(),
                gc.youngTime(),
                gc.oldCount(),
                gc.oldTime(),
                jvm.threadCount(),
                jvm.loadedClasses(),
                network.bytesIn(),
                network.bytesOut()
        );

        return new MetricsPayload(Instant.now().toString(), global, worldMetricsCollector.collect());
    }
}