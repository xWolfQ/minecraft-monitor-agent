package com.github.xwolfq.monitor.collector;

import java.lang.management.ManagementFactory;

/**
 * Zbiera zużycie pamięci sterty JVM (heap used i heap max).
 *
 * <p>Bezpieczny do wywołania z dowolnego wątku — {@code MemoryMXBean} jest thread-safe.</p>
 */
public class MemoryCollector implements MetricCollector<MemoryCollector.Result> {

    /**
     * Migawka zużycia pamięci sterty z jednego cyklu zbierania.
     *
     * @param ramUsed aktualnie zajęta pamięć sterty w bajtach
     * @param ramMax  maksymalna dostępna pamięć sterty w bajtach;
     *                {@code -1} jeśli JVM nie raportuje wartości maksymalnej
     */
    public record Result(long ramUsed, long ramMax) {}

    /**
     * Zwraca aktualny stan pamięci sterty JVM.
     *
     * @return migawka {@code used} i {@code max} w bajtach
     */
    @Override
    public Result collect() {
        var usage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        return new Result(usage.getUsed(), usage.getMax());
    }
}
