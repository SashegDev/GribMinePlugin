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
import java.util.function.Predicate;

public class StellarWhirl extends LegendaryItem {
    public StellarWhirl() {
        super(
                "staff_stellar_whirl",
                Material.STICK,
                ChatColor.AQUA + "Посох Звездного Вихря",
                Arrays.asList(
                        ChatColor.GOLD + "" + ChatColor.BOLD + "*" + ChatColor.RESET + " " + ChatColor.GOLD + "Легендарный артефакт",
                        ChatColor.DARK_PURPLE + "Данный посох, позволяет владельцу",
                        ChatColor.DARK_PURPLE + "Использовать одно из мощнейших заклинаний в этом мире",
                        ChatColor.DARK_PURPLE + "Но будьте осторожны, ведь он питается вашим" + ChatColor.DARK_AQUA + " опытом."
                ),
                0.02,
                true
        );
    }

    @Override
    public void onUse(Player player) {

        int currentLevel = player.getLevel();
        if (currentLevel < 3) {
            player.sendMessage("Хмм... не получается.");
            player.setCooldown(Material.STICK, 20 * 3);
            return;
        }
        player.setLevel(currentLevel - 3);
        player.setCooldown(Material.STICK, 20 * 5);

        Vector direction = player.getLocation().getDirection().normalize();
        Location startLocation = player.getLocation().add(0, 1.5, 0);

        Predicate<Entity> filter = entity -> !(entity instanceof Player targetPlayer && targetPlayer == player);
        RayTraceResult rayTraceResult = player.getWorld().rayTrace(startLocation, direction, 60, FluidCollisionMode.NEVER, true, 0.1, filter);

        if (rayTraceResult != null) {
            if (rayTraceResult.getHitBlock() != null) {
                player.getWorld().spawnParticle(Particle.WITCH, rayTraceResult.getHitPosition().toLocation(player.getWorld()), 450, 0.5, 0.5, 0.5, 0.4, null, true);
            } else if (rayTraceResult.getHitEntity() != null) {
                Entity hitEntity = rayTraceResult.getHitEntity();
                if (hitEntity instanceof Player targetPlayer && targetPlayer != player) {
                    targetPlayer.setVelocity(direction.multiply(1.5).setY(1.15)); // Устанавливаем скорость с учетом направления и вертикали
                    targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 20, 0, true, false, true));
                    targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 35, 3, true, false, true));
                    player.getWorld().spawnParticle(Particle.WITCH, hitEntity.getLocation(), 350, 0.2, 0.2, 0.2, 0.3, null, true);
                } else if (hitEntity instanceof Entity target && target != player) {
                    target.setFireTicks(20 * 24);
                    target.setVelocity(direction.multiply(2.4).setY(1)); // Устанавливаем скорость с учетом направления и вертикали
                    target.getWorld().spawnParticle(Particle.WITCH, hitEntity.getLocation(), 350, 0.2, 0.2, 0.2, 0.3, null, true);
                }
            }
        }
    }
}