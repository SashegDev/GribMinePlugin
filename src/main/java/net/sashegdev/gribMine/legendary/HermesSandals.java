package net.sashegdev.gribMine.legendary;

import net.sashegdev.gribMine.GribMine;
import net.sashegdev.gribMine.core.LegendaryItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Objects;

public class HermesSandals extends LegendaryItem {
    public HermesSandals() {
        super("hermes_sandals",
                Material.LEATHER_BOOTS,
                ChatColor.GOLD + "Сандалии Гермеса",
                Arrays.asList(
                        ChatColor.GRAY + "Позволяют летать",
                        ChatColor.BOLD + "" + ChatColor.YELLOW + "Легендарный артефакт"
                ),
                0.03,
                true
        );
    }

    @Override
    public void onUse(Player player) {
        player.setCooldown(Objects.requireNonNull(player.getItemInUse()).getType(), 30 * 60 * 20);

        // Активация творческого полёта
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setFlySpeed(0.2f); // Скорость полёта (можно настроить)

        // Эффекты при активации
        player.getWorld().spawnParticle(
                Particle.CLOUD,
                player.getLocation(),
                100,
                0.5, 0.5, 0.5,
                0.2
        );

        // Деактивация полёта через 2 минуты
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    player.setFlying(false);
                    player.setAllowFlight(false);
                }
            }
        }.runTaskLater(GribMine.getPlugin(GribMine.class), 30 * 60 * 2);
    }
}