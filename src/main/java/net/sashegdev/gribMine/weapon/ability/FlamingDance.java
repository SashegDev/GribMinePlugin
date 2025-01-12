package net.sashegdev.gribMine.weapon.ability;

import net.sashegdev.gribMine.GribMine;
import net.sashegdev.gribMine.weapon.WeaponAbility;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FlamingDance extends WeaponAbility {

    public FlamingDance() {
        super("flamingDance", ChatColor.GOLD + "" + ChatColor.BOLD + "Пылающий танец", GribMine.getMineConfig().getDouble("ability_chance.flamingDance"));
    }

    @Override
    public void activate(Player player, Entity entity) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1)); // Скорость на 10 секунд

        // Спавн частиц
        player.getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 30, 0.5, 0.5, 0.5, 0.1);
    }
}