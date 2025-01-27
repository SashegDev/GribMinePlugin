package net.sashegdev.gribMine.tool;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class ToolAbility {

    private final String name; // Название способности (английское)
    private final String russianName; // Русское название способности
    private final double chance; // Шанс срабатывания

    public ToolAbility(String name, String russianName, double chance) {
        this.name = name;
        this.russianName = russianName;
        this.chance = chance;
    }

    public String getName() {
        return name;
    }

    public String getRussianName() {
        return russianName;
    }

    public double getChance() {
        return chance;
    }

    public abstract void activate(Player player, ItemStack tool);
}