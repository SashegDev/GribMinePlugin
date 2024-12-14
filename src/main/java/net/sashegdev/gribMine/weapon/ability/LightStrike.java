package net.sashegdev.gribMine.weapon.ability;

import net.sashegdev.gribMine.weapon.WeaponAbility;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.LightningStrike;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LightStrike extends WeaponAbility {

    public LightStrike() {
        super("lightStrike", "Удар Зевса", 0.05);
    }

    @Override
    public void activate(Player player, Entity entity) {
        // Создаем удар молнии
        entity.getWorld().spawn(entity.getLocation(), LightningStrike.class);

        // Создаем взрыв
        entity.getWorld().createExplosion(entity.getLocation(), 5);

        // Спавним частицы
        entity.getWorld().spawnParticle(Particle.END_ROD, entity.getLocation(), 340, 1);
        entity.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, entity.getLocation(), 240, 0.4);

        // Получаем координаты удара молнии
        Location strikeLocation = entity.getLocation();

        // Проходим по всем игрокам в мире
        for (Player nearbyPlayer : Bukkit.getOnlinePlayers()) {
            // Проверяем расстояние до игрока
            if (nearbyPlayer.getLocation().distance(strikeLocation) <= 300) {
                // Воспроизводим звук
                nearbyPlayer.playSound(strikeLocation, Sound.ENTITY_WITHER_SPAWN, 0, 0); // Питч 0, громкость 100, минимальная громкость 0
            }
        }
    }
}