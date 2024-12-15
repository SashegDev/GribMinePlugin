package net.sashegdev.gribMine.weapon.ability;

import net.sashegdev.gribMine.weapon.WeaponAbility;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BloodLust extends WeaponAbility {

    public BloodLust() {
        super("bloodlust", "Жажда Крови", 0.6);
    }

    @Override
    public void activate(Player player, Entity entity) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 2, 0, true, false, false));

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