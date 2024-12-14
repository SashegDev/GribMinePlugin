package net.sashegdev.gribMine.weapon;

import org.bukkit.entity.Player;

public abstract class WeaponAbility {
    private final String name; // Название способности
    private final double chance; // Шанс срабатывания
    private final String ru_name;

    public WeaponAbility(String name,String ru_name, double chance) {
        this.name = name;
        this.ru_name = ru_name;
        this.chance = chance;
    }

    public String getName() {
        return name;
    }

    public double getChance() {
        return chance;
    }

    public String getRussianName() { return ru_name; }

    // Метод, который будет вызываться при активации способности
    public abstract void activate(Player player);
}