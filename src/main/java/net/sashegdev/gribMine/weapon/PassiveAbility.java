package net.sashegdev.gribMine.weapon;

import org.bukkit.entity.Player;

public abstract class PassiveAbility extends WeaponAbility {

    public PassiveAbility(String name, String ru_name, double chance) {
        super(name, ru_name, chance);
    }

    // Метод, который будет вызываться при активации пассивки
    public abstract void onTick(Player player);
}