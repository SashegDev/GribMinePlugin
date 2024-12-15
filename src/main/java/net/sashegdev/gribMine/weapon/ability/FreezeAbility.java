package net.sashegdev.gribMine.weapon.ability;

import net.sashegdev.gribMine.GribMine;
import net.sashegdev.gribMine.weapon.WeaponAbility;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class FreezeAbility extends WeaponAbility {



    public FreezeAbility() {
        super("freeze", "Ледяной Мост", GribMine.getMineConfig().getDouble("ability_chance.freeze"));
    }

    @Override
    public void activate(Player player, Entity entity) {
        entity.setFreezeTicks(7 * 20);
        entity.getWorld().spawnParticle(Particle.SNOWFLAKE, entity.getLocation().add(0,1,0), 120, 0.3);
        spawnBridgeParticles(player, entity);
    }

    private void spawnBridgeParticles(Player player, Entity entity) {
        new BukkitRunnable() {
            int duration = 100; // Длительность эффекта в тиках (например, 100 тиков = 5 секунд)
            int particleCount = 10;

            @Override
            public void run() {
                if (duration <= 0) {
                    cancel(); // Останавливаем задачу, если время истекло
                    return;
                }

                // Получаем координаты игрока и цели
                double startX = player.getLocation().getX();
                double startY = player.getLocation().getY() + 1;
                double startZ = player.getLocation().getZ();
                double endX = entity.getLocation().getX();
                double endY = entity.getLocation().getY() + 1;
                double endZ = entity.getLocation().getZ();

                // Вычисляем вектор между двумя точками
                double deltaX = endX - startX;
                double deltaY = endY - startY;
                double deltaZ = endZ - startZ;

                // Спавним частицы вдоль линии
                for (int i = 0; i < particleCount; i++) {
                    double ratio = (double) i / particleCount;
                    double particleX = startX + deltaX * ratio;
                    double particleY = startY + deltaY * ratio;
                    double particleZ = startZ + deltaZ * ratio;

                    // Исправленный вызов spawnParticle
                    entity.getWorld().spawnParticle(Particle.SNOWFLAKE, particleX, particleY, particleZ, 1, 0, 0, 0, 0.1);
                    entity.getWorld().spawnParticle(Particle.DRAGON_BREATH, particleX, particleY, particleZ, 3, 0, 0, 0, 0.1);
                }

                duration--; // Уменьшаем оставшееся время
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("GribMine"), 0, 1);
    }
}