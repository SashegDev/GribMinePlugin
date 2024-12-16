package net.sashegdev.gribMine.weapon.ability;

import net.sashegdev.gribMine.GribMine;
import net.sashegdev.gribMine.weapon.WeaponAbility;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class BloodLust extends WeaponAbility {

    public BloodLust() {
        super("bloodlust", "Жажда Крови", GribMine.getMineConfig().getDouble("ability_chance.bloodlust"));
    }

    @Override
    public void activate(Player player, Entity entity) {
        if (player.getCooldown(player.getInventory().getItemInMainHand()) <= 1) {
            player.setCooldown(player.getInventory().getItemInMainHand(), 5 * 20);

            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 2, 0, true, false, true));
            player.getWorld().spawnParticle(Particle.LANDING_LAVA, player.getLocation().add(0, 1, 0), 60, 0.25, 0.5, 0.25, 0.6);
            player.getWorld().spawnParticle(Particle.TRIAL_SPAWNER_DETECTION, player.getLocation().add(0, 1, 0), 60, 0.25, 0.5, 0.25, 0.013);
            player.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, entity.getLocation().add(0, 1, 0), 30, 0.25, 0.5, 0.25, 0.3);
            player.getWorld().spawnParticle(Particle.TRIAL_SPAWNER_DETECTION_OMINOUS, entity.getLocation().add(0, 1, 0), 60, 0.25, 0.5, 0.25, 0.013);

            babibabu(player, entity);

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

    private void babibabu(Player player, Entity entity) {
        new BukkitRunnable() {
            int duration = 4; // Количество тиков (4 тика = 4 секунды)
            int regenerationDuration = 40; // Длительность эффекта регенерации в тиках (2 секунды)

            @Override
            public void run() {
                if (duration <= 0) {
                    cancel();
                    return;
                }
                if (!entity.isDead()) {

                    // Спавн партиклов
                    player.getWorld().spawnParticle(Particle.TRIAL_SPAWNER_DETECTION_OMINOUS, entity.getLocation().add(0, 1, 0), 60, 0.25, 0.5, 0.25, 0.013);
                    player.getWorld().spawnParticle(Particle.FLAME, entity.getLocation().add(0, 1, 0), 60, 0.25, 0.5, 0.25, 0.013);

                    // Добавление 2 секунд к эффекту регенерации
                    if (player.hasPotionEffect(PotionEffectType.REGENERATION)) {
                        PotionEffect currentEffect = player.getPotionEffect(PotionEffectType.REGENERATION);
                        int newDuration = currentEffect.getDuration() + regenerationDuration; // Увеличиваем длительность
                        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, newDuration, currentEffect.getAmplifier(), true, false, true), true);
                    } else {
                        // Если эффекта нет, добавляем его
                        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, regenerationDuration, 0, true, false, true), true);
                    }
                } else {
                    player.getWorld().spawnParticle(Particle.TRIAL_SPAWNER_DETECTION_OMINOUS, entity.getLocation().add(0, 1, 0), 140, 0.5, 0.5, 0.5, 0.025);
                    player.getWorld().spawnParticle(Particle.FLAME, entity.getLocation().add(0, 1, 0), 70, 0.5, 0.5, 0.5, 0.013);
                }
                duration--;
            }
        }.runTaskTimer(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("GribMine")), 0, 20); // Задержка 20 тиков
    }
}