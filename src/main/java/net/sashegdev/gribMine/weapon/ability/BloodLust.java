package net.sashegdev.gribMine.weapon.ability;

import net.sashegdev.gribMine.GribMine;
import net.sashegdev.gribMine.weapon.WeaponAbility;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BloodLust extends WeaponAbility {

    public BloodLust() {
        super("bloodlust", "Жажда Крови", GribMine.getMineConfig().getDouble("ability_chance.bloodlust"));
    }

    @Override
    public void activate(Player player, Entity entity) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 2, 0, true, false, false));
        player.getWorld().spawnParticle(Particle.LANDING_LAVA, player.getLocation().add(0,1,0), 60,0.25 ,0.5, 0.25,0.6);
        player.getWorld().spawnParticle(Particle.TRIAL_SPAWNER_DETECTION, player.getLocation().add(0,1,0), 60,0.25 ,0.5, 0.25,0.013);
        player.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, entity.getLocation().add(0,1,0), 30,0.25 ,0.5, 0.25, 0.3);
        player.getWorld().spawnParticle(Particle.TRIAL_SPAWNER_DETECTION_OMINOUS, entity.getLocation().add(0,1,0), 60,0.25 ,0.5, 0.25,0.013);

        if (entity instanceof Player) {
            Player targetPlayer = (Player) entity;
            targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 4, 1, true, false, false));
        } else {
            if (entity instanceof LivingEntity) {
                LivingEntity targetEntity = (LivingEntity) entity;
                targetEntity.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 4, 1, true, false, false));
            }
        }
    }
}