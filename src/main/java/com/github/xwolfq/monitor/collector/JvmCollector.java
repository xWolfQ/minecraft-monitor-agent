package com.github.xwolfq.monitor.collector;

import java.lang.management.ManagementFactory;


  /** Zbiera ogólne metryki JVM: liczbę aktywnych wątków i załadowanych klas. */
public class JvmCollector implements MetricCollector<JvmCollector.Result> {

    /**
     * Migawka metryk JVM z jednego cyklu zbierania.
     *
     * @param threadCount    liczba aktywnych wątków JVM w chwili pomiaru
     * @param loadedClasses  liczba klas aktualnie załadowanych przez ClassLoader
     */
    public record Result(int threadCount, int loadedClasses) {}

    /**
     * Zwraca aktualną liczbę wątków i załadowanych klas JVM.
     *
     * @return migawka metryk JVM
     */
    @Override
    public Result collect() {
        int threads = ManagementFactory.getThreadMXBean().getThreadCount();
        int classes = ManagementFactory.getClassLoadingMXBean().getLoadedClassCount();
        return new Result(threads, classes);
    }
}
