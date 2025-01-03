package net.sashegdev.gribMine.weapon.ability;

import net.sashegdev.gribMine.GribMine;
import net.sashegdev.gribMine.weapon.WeaponAbility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class FreezeAbility extends WeaponAbility {

    public FreezeAbility() {
        super("freeze", ChatColor.AQUA+""+ChatColor.BOLD+"Ледяной удар", GribMine.getMineConfig().getDouble("ability_chance.freeze"));
    }

    @Override
    public void activate(Player player, Entity entity) {
        if (player.getCooldown(player.getInventory().getItemInMainHand()) <= 1) {
            entity.setFreezeTicks(20 * 20);
            spawnBridgeParticles(player, entity);
            player.setCooldown(player.getInventory().getItemInMainHand(), 5 * 20);
        }
    }

    private void spawnBridgeParticles(Player player, Entity entity) {
        new BukkitRunnable() {
            int duration = 10*20;
            final int particleCount = 100;

            @Override
            public void run() {
                if (duration <= 0) {
                    cancel(); // Останавливаем задачу, если время истекло
                    return;
                }
                if (!entity.isDead()) {

                    // Получаем координаты игрока и цели
                    double startX = player.getLocation().getX();
                    double startY = player.getLocation().getY();
                    double startZ = player.getLocation().getZ();
                    double endY = entity.getLocation().getY();
                    double endX = entity.getLocation().getX();
                    double endZ = entity.getLocation().getZ();


                    // Вычисляем вектор между двумя точками
                    double deltaX = endX - startX;
                    double deltaY = endY - startY;
                    double deltaZ = endZ - startZ;

                    for (int i = 0; i < particleCount; i++) {
                        double ratio = (double) i / particleCount;
                        double particleX = startX + deltaX * ratio;
                        double particleY = startY + deltaY * ratio;
                        double particleZ = startZ + deltaZ * ratio;

                        //TODO: поставить на этот вывод партиклов другой тип, так как текущий хуйня
                        player.getWorld().spawnParticle(Particle.PORTAL, particleX, particleY, particleZ, 1, 0, 0, 0, 0);
                    }
                } else {
                    player.getWorld().spawnParticle(Particle.SNOWFLAKE, entity.getLocation(), 460, 0, 0, 0, 0.24);
                    player.getWorld().spawnParticle(Particle.REVERSE_PORTAL, entity.getLocation(), 460, 0, 0, 0, 0.25);
                    cancel();
                }

                duration--; // Уменьшаем оставшееся время
            }
        }.runTaskTimer(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("GribMine")), 0, 1);
    }
}