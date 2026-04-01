package com.github.xwolfq.monitor.collector;

/**
 * <p>Interfejs dla kolektorów metryk JVM i serwera Minecraft.</p>
 *
 * <p>Każda implementacja zbiera dokładnie jeden rodzaj danych i zwraca
 * niemutowalną migawkę wyniku. </p>
 *
 * @param <T> typ wyniku zwracanego przez kolektor
 */
@FunctionalInterface
public interface MetricCollector<T> {

    /**
     * Zbiera metryki i zwraca ich aktualną migawkę.
     *
     * @return niemutowalna migawka zebranych danych
     */
    T collect();
}
