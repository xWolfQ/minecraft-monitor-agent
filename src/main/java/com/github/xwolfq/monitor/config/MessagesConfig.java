package com.github.xwolfq.monitor.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class MessagesConfig {

    private YamlConfiguration config;
    private final JavaPlugin plugin;

    private MessagesConfig(JavaPlugin plugin, YamlConfiguration config) {
        this.plugin = plugin;
        this.config = config;
    }

    /**
     * Wczytuje wiadomości z {@code messages.yml} w folderze pluginu.
     * Jeśli plik nie istnieje, kopiuje go z zasobów jara.
     * Brakujące klucze są uzupełniane wartościami domyślnymi z jara.
     *
     * @return gotowa do użycia instancja konfiguracji wiadomości
     */
    public static MessagesConfig load(JavaPlugin plugin) {
        var file = new File(plugin.getDataFolder(), "messages.yml");

        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        var config = YamlConfiguration.loadConfiguration(file);

        // Uzupełnij brakujące klucze wartościami domyślnymi z jara
        try (InputStream defaults = plugin.getResource("messages.yml")) {
            if (defaults != null) {
                var defaultConfig = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(defaults, StandardCharsets.UTF_8));
                config.setDefaults(defaultConfig);
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Nie można wczytać domyślnych wiadomości: " + e.getMessage());
        }

        return new MessagesConfig(plugin, config);
    }

    /**
     * Ponownie wczytuje {@code messages.yml} z dysku bez restartu serwera.
     * Wywołać po {@code /monitor reload}.
     */
    public void reload() {
        var file = new File(plugin.getDataFolder(), "messages.yml");
        config = YamlConfiguration.loadConfiguration(file);

        try (InputStream defaults = plugin.getResource("messages.yml")) {
            if (defaults != null) {
                var defaultConfig = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(defaults, StandardCharsets.UTF_8));
                config.setDefaults(defaultConfig);
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Nie można wczytać domyślnych wiadomości: " + e.getMessage());
        }
    }

    /**
     * Zwraca wiadomość przypisaną do podanego klucza z przetłumaczonymi kodami kolorów.
     * Jeśli klucz nie istnieje, zwraca komunikat o brakującej wiadomości.
     *
     * @param key klucz wiadomości z {@code messages.yml}
     * @return gotowy do wysłania graczowi tekst
     */
    public String get(String key) {
        var raw = config.getString(key, "&cBrak wiadomości: " + key);
        return ChatColor.translateAlternateColorCodes('&', raw);
    }

    /**
     * Zwraca wiadomość z podstawionymi placeholderami.
     * Placeholdery podaje się w parach: nazwa, wartość.
     * Przykład: {@code format("key-reset", "key", "abc-123")} zastępuje {@code {key}} wartością {@code abc-123}.
     *
     * @param key   klucz wiadomości z {@code messages.yml}
     * @param pairs pary (nazwa placeholdera, wartość) — musi być parzysta liczba argumentów
     * @return wiadomość z podstawionymi wartościami, gotowa do wysłania
     */
    public String format(String key, String... pairs) {
        var msg = get(key);
        for (int i = 0; i + 1 < pairs.length; i += 2) {
            msg = msg.replace("{" + pairs[i] + "}", pairs[i + 1]);
        }
        return msg;
    }
}
