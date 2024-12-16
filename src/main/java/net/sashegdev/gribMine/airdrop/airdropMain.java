package net.sashegdev.gribMine.airdrop;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class airdropMain {

    private Location location;
    private final static List<airdropMain> airdropList = new ArrayList<>();

    private final LivingEntity armor;
    public airdropMain(@NotNull Player p) {
        this.location = p.getLocation().add(new Random().nextInt(-10000, 10000), 0, new Random().nextInt(-10000, 10000));
        this.location.setY(p.getLocation().getY()+100);

        armor = p.getWorld().spawn(location, ArmorStand.class);
        armor.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, PotionEffect.INFINITE_DURATION, 1));

        airdropList.add(this);

        activation();
    }

    public airdropMain(@NotNull Player p, int w, int h) {
        this.location = p.getLocation().add(new Random().nextInt(-w, w), 0, new Random().nextInt(-h, h));
        this.location.setY(p.getLocation().getY()+100);
        this.location.setX(this.location.getBlockX());
        this.location.setZ(this.location.getBlockZ());
        System.out.println("Airdrop! X:"+location.getX()+" Z:"+location.getZ());

        armor = p.getWorld().spawn(location, ArmorStand.class);
        armor.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, PotionEffect.INFINITE_DURATION, 12));

        airdropList.add(this);

        activation();
    }

    public void activation() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (armor.getLocation().add(0, -1, 0).getBlock().getType() != Material.AIR ||
                        armor.getLocation().add(1, -1, 0).getBlock().getType() != Material.AIR ||
                        armor.getLocation().add(0, -1, 1).getBlock().getType() != Material.AIR ||
                        armor.getLocation().add(-1, -1, 0).getBlock().getType() != Material.AIR ||
                        armor.getLocation().add(0, -1, -1).getBlock().getType() != Material.AIR ||
                        armor.getLocation().add(1, -1, 1).getBlock().getType() != Material.AIR ||
                        armor.getLocation().add(-1, -1, -1).getBlock().getType() != Material.AIR ||
                        armor.getLocation().add(1, -1, 1).getBlock().getType() != Material.AIR) {

                    location = armor.getLocation();
                    World w = armor.getWorld();
                    armor.remove();

                    // Устанавливаем блок на место
                    location.getBlock().setType(Material.BARREL);

                    // Получаем BlockData для бочки и устанавливаем направление
                    BlockData blockData = location.getBlock().getBlockData();
                    if (blockData instanceof Directional) {
                        Directional directional = (Directional) blockData;
                        directional.setFacing(BlockFace.WEST); // Устанавливаем направление на запад
                        location.getBlock().setBlockData(directional);
                    }

                    cancel();
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("GribMine"), 0, 5);
    }
    public Location getLocation() { return location; }
    public static List<airdropMain> getAirdropList() { return airdropList; }
}
