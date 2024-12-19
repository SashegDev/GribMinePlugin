package net.sashegdev.gribMine.airdrop;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class airdropLoot {
    //TODO: доделай плез

    public static void addLoot(Block block) {
        block.getDrops().add(new ItemStack(Material.ACACIA_PLANKS, 1));
    }
}
