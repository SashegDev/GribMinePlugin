package net.sashegdev.gribMine.legendary;

import net.sashegdev.gribMine.GribMine;
import net.sashegdev.gribMine.core.LegendaryItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

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
        ItemStack sandals = player.getInventory().getBoots();
        if (sandals == null || !sandals.isSimilar(getItemStack())) return;

        // Установка кулдауна на 2 минуты (синхронизировано с эффектом)
        player.setCooldown(sandals.getType(), 20 * 60 * 2);

        // Активация полёта
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setFlySpeed(0.2f);

        // Эффекты
        player.getWorld().spawnParticle(
                Particle.CLOUD,
                player.getLocation(),
                100,
                0.5, 0.5, 0.5,
                0.2
        );

        // Деактивация через 2 минуты
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    player.setFlying(false);
                    player.setAllowFlight(false);
                }
            }
        }.runTaskLater(GribMine.getPlugin(GribMine.class), 20 * 60 * 2);
    }
}