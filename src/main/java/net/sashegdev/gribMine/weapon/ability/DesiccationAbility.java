package net.sashegdev.gribMine.weapon.ability;

import net.sashegdev.gribMine.weapon.*;
import org.bukkit.entity.*;
import org.bukkit.potion.*;
import org.bukkit.*;
import org.bukkit.scheduler.BukkitRunnable;

public class DesiccationAbility extends WeaponAbility {
    public DesiccationAbility() { super("desiccation", "Яд разложения", 0.6); }

    @Override
    public void activate(Player player, Entity entity) {

        Location location = entity.getLocation();
        LivingEntity le = (LivingEntity) entity;
        le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20*3, 2));

        new BukkitRunnable() {
            int step = 0;
            @Override
            public void run() {
                for (Entity e : player.getNearbyEntities(3, 2, 3)) {
                    if (e.getLocation().distance(location) < 3) {
                        if (e instanceof LivingEntity) {
                            LivingEntity le = (LivingEntity) e;
                            le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 2, 1));
                        }
                    }
                }

                for (int i = -1; i <= 1; i++)
                    for (int j = -1; j <= 1; j++) {
                        Location block = location.clone().add(i, 0, j);
                        player.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, block.clone().add(0, 1, 0), 5, 0.5, 0.5, 0.5, 0.1);
                    }

                step += 1;
                if (step >= 30) {
                    cancel();
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("GribMine"), 0, 5);
    }
}
