package com.github.xwolfq.monitor.collector;

import java.lang.management.ManagementFactory;

/**
 * Zbiera obciążenie CPU przez proces JVM.
 *
 * <p>Korzysta z {@code com.sun.management.OperatingSystemMXBean} dostępnego
 * na wszystkich JDK opartych na OpenJDK (w tym Temurin, GraalVM używanych przez Paper).
 * Jeśli platforma nie udostępnia tego MXBean, kolektor zwraca {@code 0.0}.</p>
 *
 * <p>Bezpieczny do wywołania z dowolnego wątku.</p>
 */
public class CpuCollector implements MetricCollector<CpuCollector.Result> {

    /**
     * Migawka obciążenia CPU z jednego cyklu zbierania.
     *
     * @param cpuPercent obciążenie CPU procesu JVM w procentach (0.0–100.0);
     *                   {@code 0.0} jeśli platforma nie udostępnia tej metryki
     */
    public record Result(double cpuPercent) {}

    /**
     * Zwraca aktualne obciążenie CPU procesu JVM w procentach.
     *
     * <p>{@code getProcessCpuLoad()} zwraca wartość z przedziału [0.0, 1.0]
     * lub {@code -1.0} gdy pomiar jest niedostępny (np. za krótki czas od startu JVM).
     * W obu przypadkach niedostępnych zwracane jest {@code 0.0}.</p>
     *
     * @return migawka CPU; wartość {@code 0.0} oznacza brak danych, nie zerowe obciążenie
     */
    @Override
    public Result collect() {
        var osBean = ManagementFactory.getOperatingSystemMXBean();
        if (osBean instanceof com.sun.management.OperatingSystemMXBean sunBean) {
            double load = sunBean.getProcessCpuLoad();
            return new Result(load >= 0.0 ? load * 100.0 : 0.0);
        }
        return new Result(0.0);
    }
}
