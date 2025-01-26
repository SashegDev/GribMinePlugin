package net.sashegdev.gribMine.legendary;

import net.sashegdev.gribMine.core.LegendaryItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.World;
import java.util.Arrays;
import java.util.Objects;

public class NyktaClock extends LegendaryItem {
    public NyktaClock() {
        super(
                "nykta_clock",
                Material.CLOCK,
                ChatColor.DARK_PURPLE + "Часы Нюкта",
                Arrays.asList(
                        ChatColor.GRAY + "Манипулирует временем",
                        ChatColor.BOLD+""+ChatColor.YELLOW + "Легендарный артефакт"
                ),
                0.05,
                true
        );
    }

    @Override
    public void onUse(Player player) {
        player.setCooldown(Objects.requireNonNull(player.getItemInUse()).getType(),20*60*20);
        World world = player.getWorld();
        world.setFullTime(world.getTime() < 13000 ? 14000 : 0);
        world.spawnParticle(Particle.REVERSE_PORTAL, player.getLocation(),160,0.1,0.1,0.1,0.2,0,true);
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Время изменено!");
    }
}