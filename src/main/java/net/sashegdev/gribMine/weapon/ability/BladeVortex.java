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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class BladeVortex extends WeaponAbility {

    public BladeVortex() {
        super("bladeVortex", ChatColor.BLUE + "" + ChatColor.BOLD + "Вихрь клинка", GribMine.getMineConfig().getDouble("ability_chance.bladeVortex"));
    }

    @Override
    public void activate(Player player, Entity entity) {
        if (entity instanceof LivingEntity) {
            LivingEntity target = (LivingEntity) entity;
            target.setVelocity(new Vector(0, 1.2, 0)); // Подбрасываем вверх
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20*18, 1)); // Эффект медлительности на 5 секунд

            // Спавн частиц
            player.getWorld().spawnParticle(Particle.CLOUD, target.getLocation(), 30, 0.5, 0.5, 0.5, 0.1);
        }
    }
}