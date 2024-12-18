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

public class DesiccationAbility extends WeaponAbility {
    public DesiccationAbility() { super("desiccation", ChatColor.GREEN+""+ChatColor.BOLD+"Яд разложения", GribMine.getMineConfig().getDouble("ability_chance.desiccation")); }

    @Override
    public void activate(Player player, Entity entity) {
        if (player.getCooldown(player.getInventory().getItemInMainHand()) <= 1) {
            player.setCooldown(player.getInventory().getItemInMainHand(), 5 * 20);

            Location location = entity.getLocation();
            LivingEntity le = (LivingEntity) entity;
            le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 3, 2));

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
            }.runTaskTimer(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("GribMine")), 0, 5);
        }
    }
}
