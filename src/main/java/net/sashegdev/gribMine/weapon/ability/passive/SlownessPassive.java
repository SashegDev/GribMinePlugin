package net.sashegdev.gribMine.weapon.ability.passive;

import net.sashegdev.gribMine.weapon.PassiveAbility;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SlownessPassive extends PassiveAbility {

    public SlownessPassive() {
        super("slowness", "Замедление", 0.1);
    }

    @Override
    public void onTick(Player player) {
        if (Math.random() < getChance()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20, 1,false,false,true));
        }
    }

    @Override
    public void activate(Player player, Entity entity) {
        // Пассивная способность не требует активации, поэтому метод можно оставить пустым
    }
}