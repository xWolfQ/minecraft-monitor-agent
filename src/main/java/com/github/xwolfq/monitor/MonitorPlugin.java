package com.github.xwolfq.monitor;

import com.github.xwolfq.monitor.command.MonitorCommand;
import com.github.xwolfq.monitor.config.MessagesConfig;
import com.github.xwolfq.monitor.config.PluginConfig;
import com.github.xwolfq.monitor.model.MetricsPayload;
import com.github.xwolfq.monitor.service.HttpMetricsSender;
import com.github.xwolfq.monitor.service.MetricsService;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public final class MonitorPlugin extends JavaPlugin {

    private PluginConfig config;
    private MessagesConfig messages;
    private MetricsService metricsService;
    private HttpMetricsSender httpMetricsSender;
    private BukkitTask metricsTask;

    @Override
    public void onEnable() {
        config = PluginConfig.load(this);
        messages = MessagesConfig.load(this);

        metricsService = new MetricsService(getLogger());
        httpMetricsSender = new HttpMetricsSender(getLogger());

        var cmd = getCommand("monitor");
        if (cmd != null) {
            cmd.setExecutor(new MonitorCommand(this));
        }

        startCollectingTask();

        getLogger().info("Agent monitorujący uruchomiony.");
        getLogger().info("UUID serwera : " + config.serverUuid());
        getLogger().info("URL backendu : " + config.backendUrl());
        getLogger().info("Interwał     : " + config.intervalSeconds() + "s");
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        getLogger().info("Agent monitorujący zatrzymany.");
    }

    /**
     * Zwraca aktualną migawkę konfiguracji pluginu.
     */
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
        startCollectingTask();
    }

    /**
     * Zwraca aktywną konfigurację wiadomości pluginu.
     */
    public MessagesConfig getMessages() {
        return messages;
    }

    /**
     * Zbiera metryki na głównym wątku serwera i wysyła je do backendu asynchronicznie.
     *
     * <p>Wspólna ścieżka dla cyklicznego zadania agenta oraz komendy {@code /monitor send}.
     * Rezultat przekazywany jest do {@code onResult} ponownie na głównym wątku —
     * bezpiecznie można w nim wysyłać wiadomości do graczy.</p>
     *
     * @param onResult opcjonalny callback z wynikiem wysyłki ({@code null} = brak)
     */
    public void collectAndSendAsync(Consumer<HttpMetricsSender.Result> onResult) {
        var cfg = getMonitorConfig();
        getServer().getScheduler().runTask(this, () -> {
            MetricsPayload payload = metricsService.collect();
            getServer().getScheduler().runTaskAsynchronously(this, () -> {
                var result = httpMetricsSender.send(cfg, payload);
                if (onResult != null) {
                    getServer().getScheduler().runTask(this, () -> onResult.accept(result));
                } else if (!result.success()) {
                    getLogger().warning("Cykliczna wysyłka metryk nie powiodła się: " + result.description());
                }
            });
        });
    }

    /**
     * Uruchamia (lub ponawia z nowym interwałem) cykliczne zbieranie i wysyłanie metryk.
     * Interwał pobierany jest z {@code interval-seconds} w {@code config.yml}.
     */
    private void startCollectingTask() {
        if (metricsTask != null) {
            metricsTask.cancel();
        }
        long periodTicks = Math.max(1L, (long) config.intervalSeconds() * 20L);
        metricsTask = getServer().getScheduler()
                .runTaskTimer(this, () -> collectAndSendAsync(null), periodTicks, periodTicks);
    }
}
