package net.sashegdev.gribMine.weapon.ability;

import net.sashegdev.gribMine.GribMine;
import net.sashegdev.gribMine.weapon.WeaponAbility;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ShadowCloak extends WeaponAbility {

    public ShadowCloak() {
        super("shadowCloak", ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Плащ теней", GribMine.getMineConfig().getDouble("ability_chance.shadowCloak"));
    }

    @Override
    public void activate(Player player, Entity entity) {
        if (player.getCooldown(player.getInventory().getItemInMainHand()) <= 1) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 0)); // Невидимость на 10 секунд

            // Спавн частиц
            player.getWorld().spawnParticle(Particle.LARGE_SMOKE, player.getLocation(), 30, 0.5, 0.5, 0.5, 0.1);

            // Можно добавить эффект замедления или что-то подобное для баланса
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 1));

            // Устанавливаем кулдаун на 12 секунд (12 * 20)
            player.setCooldown(player.getInventory().getItemInMainHand(), 12 * 20);
        }
    }
}