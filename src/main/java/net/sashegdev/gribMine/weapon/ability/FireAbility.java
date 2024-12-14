package net.sashegdev.gribMine.weapon.ability;

import net.sashegdev.gribMine.weapon.WeaponAbility;
import org.bukkit.entity.Player;

public class FireAbility extends WeaponAbility {
    public FireAbility() {
        super("fire", 0.2); // Название и шанс срабатывания 20%
    }

    @Override
    public void activate(Player player) {
        int ticks = 20;
        // Логика активации способности, например, поджечь игрока
        player.setFireTicks(ticks*5);
    }
}