package com.github.xwolfq.monitor.command;

import com.github.xwolfq.monitor.MonitorPlugin;
import com.github.xwolfq.monitor.config.PluginConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class MonitorCommand implements CommandExecutor {

    private final MonitorPlugin plugin;

    public MonitorCommand(MonitorPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Obsługuje komendę {@code /monitor}. Wymaga uprawnienia {@code monitor.admin}.
     * Dostępne subkomendy: {@code reload}, {@code resetkey}.
     *
     * @return zawsze {@code true} — komunikat o błędnym użyciu wysyłany jest ręcznie
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("monitor.admin")) {
            sender.sendMessage(plugin.getMessages().get("no-permission"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(plugin.getMessages().get("usage"));
            return true;
        }

        return switch (args[0].toLowerCase()) {
            case "reload" -> handleReload(sender);
            case "resetkey" -> handleResetKey(sender);
            default -> {
                sender.sendMessage(plugin.getMessages().get("unknown-subcommand"));
                yield true;
            }
        };
    }

    /**
     * Przeładowuje {@code config.yml} i {@code messages.yml} bez restartu serwera.
     */
    private boolean handleReload(CommandSender sender) {
        plugin.reloadMonitorConfig();
        plugin.getMessages().reload();
        sender.sendMessage(plugin.getMessages().get("config-reloaded"));
        return true;
    }

    /**
     * Generuje nowy klucz API, zapisuje go do {@code config.yml} i wyświetla nadawcy.
     * Pozostałe pola konfiguracji pozostają bez zmian.
     */
    private boolean handleResetKey(CommandSender sender) {
        var old = plugin.getMonitorConfig();
        var updated = new PluginConfig(
                old.backendUrl(),
                UUID.randomUUID().toString(),
                old.serverUuid(),
                old.intervalSeconds()
        );
        PluginConfig.save(plugin, updated);
        plugin.reloadMonitorConfig();
        sender.sendMessage(plugin.getMessages().format("key-reset", "key", updated.apiKey()));
        return true;
    }
}
