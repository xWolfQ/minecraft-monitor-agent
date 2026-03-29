package com.github.xwolfq.monitor;

import com.github.xwolfq.monitor.command.MonitorCommand;
import com.github.xwolfq.monitor.config.MessagesConfig;
import com.github.xwolfq.monitor.config.PluginConfig;
import org.bukkit.plugin.java.JavaPlugin;

public final class MonitorPlugin extends JavaPlugin {

    private PluginConfig config;
    private MessagesConfig messages;

    @Override
    public void onEnable() {
        config = PluginConfig.load(this);
        messages = MessagesConfig.load(this);

        var cmd = getCommand("monitor");
        if (cmd != null) {
            cmd.setExecutor(new MonitorCommand(this));
        }

        getLogger().info("Agent monitorujący uruchomiony.");
        getLogger().info("UUID serwera : " + config.serverUuid());
        getLogger().info("URL backendu : " + config.backendUrl());
        getLogger().info("Interwał     : " + config.intervalSeconds() + "s");
    }

    @Override
    public void onDisable() {
        getLogger().info("Agent monitorujący zatrzymany.");
    }

    /** Zwraca aktualną migawkę konfiguracji pluginu. */
    public PluginConfig getMonitorConfig() {
        return config;
    }

    /**
     * Wczytuje {@code config.yml} z dysku i aktualizuje aktywną konfigurację.
     * Wywołać po każdej zewnętrznej zmianie pliku lub komendzie {@code /monitor reload}.
     */
    public void reloadMonitorConfig() {
        reloadConfig();
        config = PluginConfig.load(this);
    }

    /** Zwraca aktywną konfigurację wiadomości pluginu. */
    public MessagesConfig getMessages() {
        return messages;
    }
}
