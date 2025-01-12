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

public class Sacrifice extends WeaponAbility {

    public Sacrifice() {
        super("sacrifice", ChatColor.RED + "" + ChatColor.BOLD + "Жертвоприношение", GribMine.getMineConfig().getDouble("ability_chance.sacrifice"));
    }

    @Override
    public void activate(Player player, Entity entity) {
        player.setHealth(Math.max(player.getHealth() - 6, 0)); // Убираем 3 сердца
        int strengthLevel = Math.random() < 0.5 ? 1 : 2; // Случайный уровень силы
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 500, strengthLevel)); // Сила на 25 секунд

        // Спавн частиц
        player.getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 30, 0.5, 0.5, 0.5, 0.1);
    }
}