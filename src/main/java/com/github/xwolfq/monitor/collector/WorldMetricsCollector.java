package com.github.xwolfq.monitor.collector;

import com.github.xwolfq.monitor.model.EntityCount;
import com.github.xwolfq.monitor.model.WorldMetrics;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Zbiera metryki światów Minecraft, w których przebywa co najmniej jeden gracz.
 *
 * <p>Światy bez graczy są pomijane — zgodnie z dokumentacją {@link WorldMetrics}
 * ich metryki nie są zbierane w cyklu, by nie obciążać serwera na pustych światach.</p>
 *
 * <p>Wymaga wywołania z głównego wątku serwera — iteruje po encjach światów.</p>
 */
public class WorldMetricsCollector implements MetricCollector<List<WorldMetrics>> {

    /**
     * Zwraca metryki wszystkich światów, w których przebywa co najmniej jeden gracz.
     *
     * @return lista metryk światów (pusta, gdy żaden gracz nie jest online)
     */
    @Override
    public List<WorldMetrics> collect() {
        return Bukkit.getWorlds().stream()
                .filter(world -> !world.getPlayers().isEmpty())
                .map(this::collectWorld)
                .toList();
    }

    private WorldMetrics collectWorld(World world) {
        List<Player> players = world.getPlayers();

        int minPing = players.stream().mapToInt(Player::getPing).min().orElse(0);
        int maxPing = players.stream().mapToInt(Player::getPing).max().orElse(0);
        double avgPing = players.stream().mapToInt(Player::getPing).average().orElse(0.0);

        return new WorldMetrics(
                world.getName(),
                world.getChunkCount(),
                world.getEntityCount(),
                world.getTileEntityCount(),
                players.size(),
                minPing,
                avgPing,
                maxPing,
                collectEntities(world)
        );
    }

    /**
     * Zlicza encje świata pogrupowane według typu i zwraca je posortowane malejąco.
     */
    private List<EntityCount> collectEntities(World world) {
        Map<String, Integer> counts = new HashMap<>();
        world.getEntities().forEach(entity ->
                counts.merge(entity.getType().name(), 1, Integer::sum));

        return counts.entrySet().stream()
                .map(entry -> new EntityCount(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingInt(EntityCount::getCount).reversed())
                .toList();
    }
}