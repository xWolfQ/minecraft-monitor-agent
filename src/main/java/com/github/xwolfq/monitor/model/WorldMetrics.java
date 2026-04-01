package com.github.xwolfq.monitor.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Metryki jednego świata Minecraft zbierane wyłącznie wtedy,
 * gdy w danym świecie przebywa co najmniej jeden gracz.
 */
public class WorldMetrics {

    /** Nazwa świata zgodna z {@code World.getName()}. */
    private final String world;

    /** Liczba aktualnie załadowanych chunków w świecie. */
    @SerializedName("loaded_chunks")
    private final int loadedChunks;

    /** Łączna liczba encji w świecie (wszystkich typów). */
    @SerializedName("entity_count")
    private final int entityCount;

    /** Liczba tile entity (bloków z logiką, np. skrzynie, piece) w załadowanych chunkach. */
    @SerializedName("tile_entity_count")
    private final int tileEntityCount;

    /** Liczba graczy aktualnie przebywających w tym świecie. */
    @SerializedName("players_online")
    private final int playersOnline;

    /** Minimalny ping gracza w świecie, w milisekundach. */
    @SerializedName("ping_min")
    private final int pingMin;

    /** Średni ping graczy w świecie, w milisekundach. */
    @SerializedName("ping_avg")
    private final double pingAvg;

    /** Maksymalny ping gracza w świecie, w milisekundach. */
    @SerializedName("ping_max")
    private final int pingMax;

    /** Rozkład encji w świecie pogrupowany według typu. */
    private final List<EntityCount> entities;

    public WorldMetrics(String world, int loadedChunks, int entityCount, int tileEntityCount,
                        int playersOnline, int pingMin, double pingAvg, int pingMax,
                        List<EntityCount> entities) {
        this.world           = world;
        this.loadedChunks    = loadedChunks;
        this.entityCount     = entityCount;
        this.tileEntityCount = tileEntityCount;
        this.playersOnline   = playersOnline;
        this.pingMin         = pingMin;
        this.pingAvg         = pingAvg;
        this.pingMax         = pingMax;
        this.entities        = entities;
    }

    public String getWorld()           { return world; }
    public int getLoadedChunks()       { return loadedChunks; }
    public int getEntityCount()        { return entityCount; }
    public int getTileEntityCount()    { return tileEntityCount; }
    public int getPlayersOnline()      { return playersOnline; }
    public int getPingMin()            { return pingMin; }
    public double getPingAvg()         { return pingAvg; }
    public int getPingMax()            { return pingMax; }
    public List<EntityCount> getEntities() { return entities; }
}
