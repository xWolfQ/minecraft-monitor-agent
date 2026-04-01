package com.github.xwolfq.monitor.model;

/**
 * Liczba encji danego typu w jednym świecie.
 * Element listy {@link WorldMetrics#entities}.
 */
public class EntityCount {

    /** Nazwa typu encji zgodna z {@code EntityType.name()}, np. {@code "CREEPER"}. */
    private final String type;

    /** Łączna liczba żywych encji tego typu w świecie. */
    private final int count;

    public EntityCount(String type, int count) {
        this.type = type;
        this.count = count;
    }

    public String getType() { return type; }
    public int getCount()   { return count; }
}
