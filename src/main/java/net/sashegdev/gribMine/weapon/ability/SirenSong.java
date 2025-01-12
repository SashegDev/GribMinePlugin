package net.sashegdev.gribMine.weapon.ability;

import net.sashegdev.gribMine.GribMine;
import net.sashegdev.gribMine.weapon.WeaponAbility;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SirenSong extends WeaponAbility {

    public SirenSong() {
        super("sirenSong", ChatColor.AQUA + "" + ChatColor.BOLD + "Песнь Сирены", GribMine.getMineConfig().getDouble("ability_chance.sirenSong"));
    }

    @Override
    public void activate(Player player, Entity entity) {
        if (entity instanceof LivingEntity) {
            LivingEntity target = (LivingEntity) entity;
            target.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 200, 0)); // Сопротивление на 10 секунд
            player.setHealth(Math.min(player.getHealth() + 4, player.getMaxHealth())); // Добавляем 2 сердца

            // Спавн частиц
            player.getWorld().spawnParticle(Particle.HEART, target.getLocation(), 10, 0.5, 0.5, 0.5, 0.1);
        }
    }
}