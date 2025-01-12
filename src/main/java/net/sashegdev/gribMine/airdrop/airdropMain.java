package net.sashegdev.gribMine.airdrop;

import net.sashegdev.gribMine.GribMine;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class airdropMain implements Listener {

    private Block barrelBlock;
    private final Location location;
    private static final List<airdropMain> airdropList = new ArrayList<>();
    private LivingEntity armor;

    public airdropMain(@NotNull Player p) {
        this(p, 350, 350);
    }

    public airdropMain(@NotNull Player p, int w, int h) {
        this.location = p.getLocation().add(new Random().nextInt(-w, w), 0, new Random().nextInt(-h, h));
        this.location.setY(p.getLocation().getY() + 100); // Set height

        System.out.println("Airdrop! X:" + location.getX() + " Z:" + location.getZ());
        airdropList.add(this);

        // Spawn particle column at player's location
        spawnParticleColumn(p.getLocation());

        new BukkitRunnable() {
            @Override
            public void run() {
                // Get the highest block Y at the X and Z coordinates
                int highestY = p.getWorld().getHighestBlockYAt(location.getBlockX(), location.getBlockZ());
                // Set the Y coordinate to be above the highest block
                location.setY(highestY + 1); // Spawn 1 block above the highest block

                // Create ArmorStand
                armor = p.getWorld().spawn(location.clone().add(0, 1, 0), ArmorStand.class); // Adjust Y position
                armor.setInvisible(true);
                armor.setInvulnerable(true);
                armor.setCustomName(ChatColor.RED + "Воздушное Снабжение");
                armor.setCustomNameVisible(true);
                armor.setCanPickupItems(false);
                Objects.requireNonNull(armor.getEquipment()).setHelmet(new ItemStack(Material.BARREL));

                // Add slow falling effect
                armor.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, Integer.MAX_VALUE, 1)); // Уменьшите уровень эффекта

                // Start slow fall
                startSlowFall();

                // Notify players
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(ChatColor.GRAY +
                            "Я увидел самолет...\n" +
                            "И он что-то выбросил...\n" +
                            "Это примерно на:\n" +
                            "X:" + ChatColor.GOLD + location.getBlockX() + ChatColor.GRAY + " Z:" + ChatColor.GOLD + location.getBlockZ()
                    );
                }
                startCheckingForGround();
            }
        }.runTaskLater(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("GribMine")), 20 * 25);
    }

    private void spawnParticleColumn(Location playerLocation) {
        // Start a new BukkitRunnable to spawn particles for 20 seconds
        new BukkitRunnable() {
            int ticks = 0; // Counter for ticks

            @Override
            public void run() {
                // Spawn particles every tick for 20 seconds (20 seconds = 400 ticks)
                if (ticks < 400) {
                    // Get the starting Y coordinate
                    double startY = playerLocation.getY(); // Use double for more precision
                    // Loop to spawn particles from startY to startY + 18 with a step of 0.2
                    for (double y = startY; y <= startY + 18; y += 0.2) {
                        Location particleLocation = new Location(playerLocation.getWorld(), playerLocation.getX(), y, playerLocation.getZ());
                        Objects.requireNonNull(playerLocation.getWorld()).spawnParticle(Particle.LARGE_SMOKE, particleLocation, 1, 0, 0, 0, 0.001);
                    }
                    ticks++;
                } else {
                    cancel(); // Stop the task after 20 seconds
                }
            }
        }.runTaskTimer(GribMine.getPlugin(GribMine.class), 0, 1); // Start immediately and run every tick
    }

    public void startCheckingForGround() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if ((armor != null && armor.isOnGround())) {
                    activate(); // Call the activate method when armor is on the ground
                    cancel(); // Stop this task
                }
            }
        }.runTaskTimer(GribMine.getPlugin(GribMine.class), 0, 1); // Check every tick (1 tick = 1/20 second)
    }

    public void startSlowFall() {
        new BukkitRunnable() {
            double fallSpeed = 0.1; // Скорость падения
            double currentY = armor.getLocation().getY(); // Текущая высота

            @Override
            public void run() {
                if (armor != null && !armor.isOnGround()) {
                    currentY -= fallSpeed; // Уменьшаем Y координату
                    armor.teleport(new Location(armor.getWorld(), armor.getLocation().getX(), currentY, armor.getLocation().getZ()));
                } else {
                    cancel(); // Останавливаем задачу, если арморстенд на земле
                }
            }
        }.runTaskTimer(GribMine.getPlugin(GribMine.class), 0, 1); // Запускаем каждую тика
    }

    public void activate() {
        // Добавляем лут в бочку
        barrelBlock = addLootToBarrel();
        armor.remove();
    }

    private Block addLootToBarrel() {
        // Set the block type to barrel
        barrelBlock = armor.getLocation().getBlock(); // Store the reference to the barrel block
        barrelBlock.setType(Material.BARREL);

        // Set the block data for the barrel
        BlockData blockData = barrelBlock.getBlockData();
        if (blockData instanceof Directional directional) {
            directional.setFacing(BlockFace.WEST); // Set direction to west
            barrelBlock.setBlockData(directional);
        }

        // Add loot to the barrel
        airdropLoot.addLoot(barrelBlock);

        return barrelBlock; // Return the barrel block
    }

    public Location getLocation() {
        return location;
    }

    public static List<airdropMain> getAirdropList() {
        return airdropList;
    }
}