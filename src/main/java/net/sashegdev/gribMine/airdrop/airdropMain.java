package net.sashegdev.gribMine.airdrop;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.potion.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.*;

import java.util.*;
public class airdropMain {

    private Location location;
    private final static List<airdropMain> airdropList = new ArrayList<>();

    private final LivingEntity armor;
    public airdropMain(@NotNull Player p) {
        this.location = p.getLocation().add(new Random().nextInt(-10000, 10000), 0, new Random().nextInt(-10000, 10000));
        this.location.setY(10000000);

        armor = p.getWorld().spawn(location, ArmorStand.class);
        armor.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 1000*20, 1));

        airdropList.add(this);
    }

    public void activation() {
        new BukkitRunnable() {
            @Override
            public void run() {

                if (armor.getLocation().add(0, -1, 0).getBlock().getType() != Material.AIR) {

                    location = armor.getLocation();
                    World w = armor.getWorld();
                    armor.remove();

                    location.getBlock().setType(Material.BARREL);

                    cancel();
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("GribMine"), 0, 5);
    }
    public Location getLocation() { return location; }
    public static List<airdropMain> getAirdropList() { return airdropList; }
}
