package net.sashegdev.gribMine.weapon.ability;

import net.sashegdev.gribMine.GribMine;
import net.sashegdev.gribMine.weapon.WeaponAbility;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class FreezeAbility extends WeaponAbility {



    public FreezeAbility() {
        super("freeze", "Ледяной удар", GribMine.getMineConfig().getDouble("ability_chance.freeze"));
    }

    @Override
    public void activate(Player player, Entity entity) {
        entity.setFreezeTicks(7 * 20);
        entity.getWorld().spawnParticle(Particle.SNOWFLAKE, entity.getLocation().add(0,1,0), 120, 0.3);
        entity.getWorld().spawnParticle(Particle.DRAGON_BREATH, entity.getLocation().add(0,1,0), 130, 0, 0, 0, 0.4);
    }
}