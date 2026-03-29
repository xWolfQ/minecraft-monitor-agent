package com.github.xwolfq.monitor.config;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public record PluginConfig(
        String backendUrl,
        String apiKey,
        String serverUuid,
        int intervalSeconds
) {

    /**
     * Wczytuje konfigurację pluginu z {@code config.yml}.
     * Jeśli plik nie istnieje, tworzy go z wartościami domyślnymi.
     * Pola {@code api-key} i {@code server-uuid} są generowane automatycznie
     * przy pierwszym uruchomieniu i od razu zapisywane na dysk.
     *
     * @return migawka konfiguracji gotowa do użycia
     */
    public static PluginConfig load(JavaPlugin plugin) {
        plugin.saveDefaultConfig();
        var config = plugin.getConfig();

        String apiKey = config.getString("api-key", "");
        if (apiKey.isBlank()) {
            apiKey = UUID.randomUUID().toString();
            config.set("api-key", apiKey);
        }

        String serverUuid = config.getString("server-uuid", "");
        if (serverUuid.isBlank()) {
            serverUuid = UUID.randomUUID().toString();
            config.set("server-uuid", serverUuid);
        }

        plugin.saveConfig();

        return new PluginConfig(
                config.getString("backend-url", "http://localhost:8080"),
                apiKey,
                serverUuid,
                config.getInt("interval-seconds", 10)
        );
    }

    /**
     * Zapisuje wszystkie pola podanej konfiguracji do {@code config.yml} na dysk.
     * Nadpisuje wcześniej zapisane wartości.
     */
    public static void save(JavaPlugin plugin, PluginConfig cfg) {
        var config = plugin.getConfig();
        config.set("backend-url", cfg.backendUrl());
        config.set("api-key", cfg.apiKey());
        config.set("server-uuid", cfg.serverUuid());
        config.set("interval-seconds", cfg.intervalSeconds());
        plugin.saveConfig();
    }
}
