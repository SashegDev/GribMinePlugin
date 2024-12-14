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
        super("fire","Пламенный удар", 0.7); // Название и шанс срабатывания 70%
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
            @Override
            public void run() {
                // Создаем частицы лавы от позиции глаз до конечной позиции
                player.getWorld().spawnParticle(org.bukkit.Particle.LAVA, endLocation, 10, 0.5, 0.5, 0.5, 0.1);

                // Поджигаем сущности в области
                for (Entity entity : player.getNearbyEntities(3, 2, 3)) { // 3 блока вперед, 2 блока влево и вправо
                    if (entity.getLocation().distance(startLocation) <= 3) {
                        entity.setFireTicks(100); // Поджигаем сущность на 5 секунд
                    }
                }

                // Поджигаем блоки в области
                for (int x = -2; x <= 2; x++) {
                    for (int z = -2; z <= 2; z++) {
                        Location blockLocation = endLocation.clone().add(x, player.getLocation().getY(), z);
                        if (blockLocation.getBlock().getType() != Material.AIR) {
                            blockLocation.getBlock().setType(Material.FIRE); // Устанавливаем огонь на блок
                        }
                    }
                }

                // Останавливаем задачу после одного выполнения
                cancel();
            }
        }.runTaskLater(Bukkit.getPluginManager().getPlugin("GribMine"), 5); // Задержка в 5 тиков (примерно 0.25 секунды)
    }
}