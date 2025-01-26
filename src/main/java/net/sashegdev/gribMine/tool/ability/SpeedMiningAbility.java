package net.sashegdev.gribMine.tool.ability;

import net.md_5.bungee.api.ChatColor;
import net.sashegdev.gribMine.tool.ToolAbility;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpeedMiningAbility extends ToolAbility {

    public SpeedMiningAbility() {
        super("speed_mining", ChatColor.GREEN + "Ускорение добычи", 0.2); // Шанс срабатывания 20%
    }

    @Override
    public void activate(Player player, ItemStack tool) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 100, 1)); // Эффект ускорения добычи
    }
}