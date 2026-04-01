package com.github.xwolfq.monitor.collector;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Set;

/**
 * Zbiera statystyki Garbage Collector z podziałem na młodą (young) i starą (old) generację.
 *
 * <p>Rozróżnienie generacji odbywa się na podstawie nazw znanych kolektorów JVM.
 * Kolektor nierozpoznany po nazwie jest traktowany jako old-gen, co jest
 * bezpiecznym fallbackiem — nie powoduje utraty danych, tylko błędne przypisanie.</p>
 *
 * <p>Zwracane wartości są skumulowane od startu JVM, tak jak raportuje je JMX. </p>
 *
 * <p>Bezpieczny do wywołania z dowolnego wątku.</p>
 */
public class GarbageCollectorCollector implements MetricCollector<GarbageCollectorCollector.Result> {

    /**
     * Migawka statystyk GC z jednego cyklu zbierania.
     *
     * @param youngCount łączna liczba cykli GC młodej generacji od startu JVM
     * @param youngTime  łączny czas GC młodej generacji od startu JVM, w milisekundach
     * @param oldCount   łączna liczba cykli GC starej generacji od startu JVM
     * @param oldTime    łączny czas GC starej generacji od startu JVM, w milisekundach
     */
    public record Result(long youngCount, long youngTime, long oldCount, long oldTime) {}

    /**
     * Nazwy kolektorów klasyfikowanych jako young-gen.
     * Wszystkie pozostałe traktowane są jako old-gen.
     */
    private static final Set<String> YOUNG_GC_NAMES = Set.of(
            "G1 Young Generation",
            "PS Scavenge",
            "ParNew",
            "Copy",
            "ZGC Minor Cycles",
            "Shenandoah Cycles",
            "GPGC New"
    );

    /**
     * Zwraca skumulowane liczniki cykli i czasu GC z podziałem na young/old generację.
     *
     * @return migawka statystyk GC; wartości {@code -1} z {@code GarbageCollectorMXBean}
     *         (kolektor nieaktywny) są traktowane jako {@code 0}
     */
    @Override
    public Result collect() {
        long youngCount = 0, youngTime = 0;
        long oldCount   = 0, oldTime   = 0;

        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            long count = Math.max(gc.getCollectionCount(), 0);
            long time  = Math.max(gc.getCollectionTime(),  0);

            if (YOUNG_GC_NAMES.contains(gc.getName())) {
                youngCount += count;
                youngTime  += time;
            } else {
                oldCount += count;
                oldTime  += time;
            }
        }

        return new Result(youngCount, youngTime, oldCount, oldTime);
    }
}
