package net.sashegdev.gribMine.legendary;

import net.sashegdev.gribMine.core.LegendaryItem;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Objects;

public class ThorHammer extends LegendaryItem {
    public ThorHammer() {
        super(
                "thor_hammer",
                Material.IRON_AXE,
                ChatColor.BLUE + "Мьёльнир",
                Arrays.asList(
                        ChatColor.GRAY + "Призывает молнии",
                        ChatColor.BOLD + "" + ChatColor.YELLOW + "Легендарный артефакт"
                ),
                0.02,
                true
        );
    }

    @Override
    public void onUse(Player player) {
        // Устанавливаем кулдаун
        player.setCooldown(Objects.requireNonNull(player.getItemInUse()).getType(), 30 * 60 * 20);

        // Получаем цель игрока с помощью RayTrace
        Entity target = null;
        Location eyeLoc = player.getEyeLocation();
        Vector direction = eyeLoc.getDirection().normalize();
        double maxDistance = 50;

        for (double dist = 0; dist <= maxDistance; dist += 0.5) {
            Location checkLoc = eyeLoc.clone().add(direction.clone().multiply(dist));
            for (Entity entity : player.getWorld().getEntities()) {
                if (entity.equals(player)) continue; // Игнорируем самого игрока
                if (entity.getBoundingBox().contains(checkLoc.getX(), checkLoc.getY(), checkLoc.getZ())) {
                    target = entity;
                    break;
                }
            }
            if (target != null) break;
        }

        // Если цель найдена...
        if (target != null) {
            target.getWorld().strikeLightning(target.getLocation());
            target.getWorld().strikeLightning(target.getLocation());

            target.getWorld().spawnParticle(
                    Particle.ELECTRIC_SPARK,
                    target.getLocation(),
                    50,
                    0.5, 0.5, 0.5,
                    0.3
            );

            target.getWorld().playSound(
                    target.getLocation(),
                    Sound.ENTITY_LIGHTNING_BOLT_THUNDER,
                    1.0f,
                    0.5f
            );
        }

        // ... остальная логика с полётом ...
    }
}