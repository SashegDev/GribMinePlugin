package net.sashegdev.gribMine.weapon.ability;

import net.sashegdev.gribMine.GribMine;
import net.sashegdev.gribMine.weapon.WeaponAbility;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LightStrike extends WeaponAbility {
    //TODO: реализовать прикол когда партиклы будут видны с людой дистанции

    public LightStrike() {
        super("lightStrike", ChatColor.LIGHT_PURPLE+""+ChatColor.BOLD+"Удар"+ChatColor.YELLOW+" Зевса", GribMine.getMineConfig().getDouble("ability_chance.lightStrike"));
    }

    @Override
    public void activate(Player player, Entity entity) {
        if (player.getCooldown(player.getInventory().getItemInMainHand()) <= 1) {
            player.setCooldown(player.getInventory().getItemInMainHand(), 40 * 20);

            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 40, 255, true, false, false));

            // Создаем удар молнии
            entity.getWorld().spawn(entity.getLocation(), LightningStrike.class);

            // Создаем взрыв
            entity.getWorld().createExplosion(entity.getLocation(), 7);

            // Спавним частицы
            entity.getWorld().spawnParticle(Particle.END_ROD, entity.getLocation(), 650, 0.5, 0.5, 0.5, 0.8);
            entity.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, entity.getLocation(), 500, 0.5, 0.5, 0.5, 0.7);

            // Получаем координаты удара молнии
            Location strikeLocation = entity.getLocation();

            // Проходим по всем игрокам в мире
            for (Player nearbyPlayer : Bukkit.getOnlinePlayers()) {
                // Проверяем расстояние до игрока
                if (nearbyPlayer.getLocation().distance(strikeLocation) <= 300) {
                    // Воспроизводим звук
                    nearbyPlayer.playSound(strikeLocation, Sound.ENTITY_WITHER_SPAWN, 0.0f, 1.0f); // Питч 1.0, громкость 1.0
                }
            }
        }
    }
}