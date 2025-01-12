package net.sashegdev.gribMine.weapon.ability;

import net.sashegdev.gribMine.GribMine;
import net.sashegdev.gribMine.weapon.WeaponAbility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class Suffocation extends WeaponAbility {

    public Suffocation() {
        super("suffocation", ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "Задушье", GribMine.getMineConfig().getDouble("ability_chance.suffocation"));
    }

    @Override
    public void activate(Player player, Entity entity) {
        if (entity instanceof LivingEntity) {
            LivingEntity target = (LivingEntity) entity;

            new BukkitRunnable() {
                int duration = 100; // 5 секунд

                @Override
                public void run() {
                    if (duration <= 0 || target.isDead()) {
                        cancel();
                        return;
                    }

                    target.damage(1); // Наносим урон каждую секунду
                    target.getWorld().spawnParticle(Particle.BUBBLE_POP, target.getLocation(), 10, 0.5, 0.5, 0.5, 0.1);

                    duration -= 20; // Уменьшаем длительность на 1 секунду
                }
            }.runTaskTimer(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("GribMine")), 0, 20);
        }
    }
}