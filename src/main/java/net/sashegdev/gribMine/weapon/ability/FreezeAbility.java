package net.sashegdev.gribMine.weapon.ability;

import net.sashegdev.gribMine.GribMine;
import net.sashegdev.gribMine.weapon.WeaponAbility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class FreezeAbility extends WeaponAbility {

    public FreezeAbility() {
        super("freeze", ChatColor.AQUA+""+ChatColor.BOLD+"Ледяной удар", GribMine.getMineConfig().getDouble("ability_chance.freeze"));
    }

    @Override
    public void activate(Player player, Entity entity) {
        if (player.getCooldown(player.getInventory().getItemInMainHand()) <= 1) {
            // Проверяем, является ли entity экземпляром LivingEntity
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.setFreezeTicks(50 * 20); // Устанавливаем заморозку

                // Добавляем эффект замедления
                livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 9, 2, true, false, false));
                livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 20 * 4, 1, true, false, false));
                player.setCooldown(player.getInventory().getItemInMainHand(), 5 * 20);
                Location loc = livingEntity.getLocation();
                entity.getWorld().spawnParticle(Particle.SNOWFLAKE, loc.add(0,2,0), 40, 0.6, 0.6, 0.6, 0.1);
            }
        }
    }
}