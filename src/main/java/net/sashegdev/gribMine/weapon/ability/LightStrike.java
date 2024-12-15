package net.sashegdev.gribMine.weapon.ability;

import net.sashegdev.gribMine.weapon.WeaponAbility;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.LightningStrike;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

public class LightStrike extends WeaponAbility {
    //TODO: реализовать прикол когда партиклы будут видны с людой дистанции

    public LightStrike() {
        super("lightStrike", "Удар Зевса", 0.5);
    }

    @Override
    public void activate(Player player, Entity entity) {

        // Создаем удар молнии
        entity.getWorld().spawn(entity.getLocation(), LightningStrike.class);

        // Создаем взрыв
        entity.getWorld().createExplosion(entity.getLocation(), 7);

        // Спавним частицы
        entity.getWorld().spawnParticle(Particle.END_ROD, entity.getLocation(), 650, 0.5, 0.5, 0.5, 0.8);
        entity.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, entity.getLocation(), 500, 0.5, 0.5, 0.5, 0.7);
        entity.getWorld().spawnParticle(Particle.DRAGON_BREATH, entity.getLocation(), 300, 0.5, 0.5, 0.5, 0.45);

        // Получаем координаты удара молнии
        Location strikeLocation = entity.getLocation();

        // Проходим по всем игрокам в мире
        for (Player nearbyPlayer : Bukkit.getOnlinePlayers()) {
            // Проверяем расстояние до игрока
            if (nearbyPlayer.getLocation().distance(strikeLocation) <= 800) {
                // Воспроизводим звук
                nearbyPlayer.playSound(strikeLocation, Sound.ENTITY_WITHER_SPAWN, 0.0f, 100.0f);
            }
        }
    }
}