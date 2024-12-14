package net.sashegdev.gribMine.weapon;

import org.bukkit.entity.Player;

public abstract class WeaponAbility {
    private final String name; // Название способности
    private final double chance; // Шанс срабатывания

    public WeaponAbility(String name, double chance) {
        this.name = name;
        this.chance = chance;
    }

    public String getName() {
        return name;
    }

    public double getChance() {
        return chance;
    }

    // Метод, который будет вызываться при активации способности
    public abstract void activate(Player player);
}