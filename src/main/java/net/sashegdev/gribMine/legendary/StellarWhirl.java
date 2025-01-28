package net.sashegdev.gribMine.legendary;

import net.sashegdev.gribMine.GribMine;
import net.sashegdev.gribMine.core.LegendaryItem;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Objects;

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
            player.setCooldown(Objects.requireNonNull(player.getItemInUse()).getType(), 20 * 3);
            return;
        }
        player.setLevel(currentLevel - 3);

        player.setCooldown(Objects.requireNonNull(player.getItemInUse()).getType(), 20 * 5);

        // Получаем направление, в котором смотрит игрок
        Vector direction = player.getLocation().getDirection().normalize();
        Location startLocation = player.getLocation().add(0, 1.5, 0); // Начальная позиция немного выше игрока

        new BukkitRunnable() {
            private int particleCount = 0;
            private final Location currentLocation = startLocation.clone();

            @Override
            public void run() {
                // Проверяем столкновение с блоками или сущностями
                RayTraceResult rayTraceResult = player.getWorld().rayTrace(currentLocation, direction, 60, FluidCollisionMode.NEVER, true, 0.1, null);

                if (rayTraceResult != null) {
                    // Если столкновение с блоком
                    if (rayTraceResult.getHitBlock() != null) {
                        player.getWorld().spawnParticle(Particle.PORTAL, rayTraceResult.getHitPosition().toLocation(player.getWorld()), 10, 0, 0, 0, 0.05, null, true);
                        this.cancel(); // Останавливаем выполнение
                    } else if (rayTraceResult.getHitEntity() != null) {
                        Entity hitEntity = rayTraceResult.getHitEntity();
                        if (hitEntity instanceof Player targetPlayer) {
                            // Наложение эффектов на игрока
                            targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 240, 0, true, false, true));
                            targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 240, 3, true, false, true));
                        }
                        this.cancel(); // Останавливаем выполнение
                    }
                }

                // Обновляем текущую позицию заряда
                currentLocation.add(direction.clone().multiply(0.1)); // Двигаем заряд вперед

                // Создаем партиклы PORTAL
                player.getWorld().spawnParticle(Particle.PORTAL, currentLocation, 1, 0, 0, 0, 0, null, true);

                // Создаем след партиклов END_ROD
                if (particleCount > 0) {
                    player.getWorld().spawnParticle(Particle.END_ROD, currentLocation.subtract(direction.clone().multiply(0.05)), 1, 0, 0, 0, 0, null, true);
                }

                particleCount++;
            }
        }.runTaskTimer(GribMine.getPlugin(GribMine.class), 0, 1);
    }
}