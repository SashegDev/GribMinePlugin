package net.sashegdev.gribMine.weapon.ability;

import net.sashegdev.gribMine.weapon.WeaponAbility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class FireAbility extends WeaponAbility {
    public FireAbility() {
        super("fire", "Пламенный удар", 0.7); // Название и шанс срабатывания 70%
    }

    @Override
    public void activate(Player player) {
        // Получаем направление взгляда игрока
        Vector direction = player.getLocation().getDirection();
        Location startLocation = player.getEyeLocation(); // Начальная позиция - позиция глаз игрока

        // Определяем конечную позицию
        Location endLocation = startLocation.clone().add(direction.clone().multiply(3)); // 3 блока вперед
        endLocation.setY(endLocation.getWorld().getHighestBlockYAt(endLocation) + 1); // Устанавливаем Y на верхнюю часть блока

        // Запускаем задачу для создания эффекта
        new BukkitRunnable() {
            int step = 0; // Счетчик шагов

            @Override
            public void run() {
                // Поджигаем сущности в области
                for (Entity entity : player.getNearbyEntities(3, 2, 3)) { // 3 блока в радиусе
                    if (entity.getLocation().distance(startLocation) <= 3) {
                        entity.setFireTicks(100); // Поджигаем сущность на 5 секунд

                        // Запускаем задачу для испускания частиц из подожженной сущности
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (entity.isValid() && entity.isVisualFire()) {
                                    entity.getWorld().spawnParticle(org.bukkit.Particle.FLAME, entity.getLocation(), 30, 0.1, 0.1, 0.1, 0.1);
                                } else {
                                    cancel(); // Останавливаем задачу, если сущность больше не подожжена
                                }
                            }
                        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("GribMine"), 0, 5); // Запускаем задачу с задержкой 0 и периодом 5 тиков
                    }
                }

                // Устанавливаем огонь только в направлении взгляда игрока
                for (int i = 1; i <= 3; i++) { // Устанавливаем огонь на 3 блока вперед
                    Location blockLocation = startLocation.clone().add(direction.clone().multiply(i));
                    if (blockLocation.getBlock().getType() == Material.AIR) {
                        blockLocation.getBlock().setType(Material.FIRE); // Устанавливаем огонь на блок
                        // Спавним частицы над блоком
                        player.getWorld().spawnParticle(org.bukkit.Particle.LAVA, blockLocation.clone().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.1);
                    }
                }

                step++;
                if (step >= 5) { // Количество шагов, чтобы завершить эффект
                    cancel(); // Останавливаем задачу после завершения
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("GribMine"), 0, 5); // Запускаем задачу с задержкой 0 и периодом 5 тиков
    }
}