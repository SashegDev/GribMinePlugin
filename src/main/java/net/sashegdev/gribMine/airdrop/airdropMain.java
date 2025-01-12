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

                armor = p.getWorld().spawn(location, ArmorStand.class);
                armor.setInvisible(true);
                armor.setInvulnerable(true);
                armor.setCustomName(ChatColor.RED + "Воздушное Снабжение");
                armor.setCustomNameVisible(true);
                armor.setCanPickupItems(false);
                Objects.requireNonNull(armor.getEquipment()).setHelmet(new ItemStack(Material.BARREL));

                // Add slow falling effect
                armor.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, Integer.MAX_VALUE, 999)); // Уменьшите уровень эффекта

                // Notify players
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(ChatColor.GRAY +
                            "Я увидел самолет...\n" +
                            "И он что-то выбросил...\n" +
                            "Где то на:\n" +
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
                        Objects.requireNonNull(playerLocation.getWorld()).spawnParticle(Particle.WITCH, particleLocation, 1, 0.2, 0, 0.2, 0.001);
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
                if (armor.isOnGround()) {
                    Block BARREL = armor.getLocation().getBlock();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            BARREL.setType(Material.AIR);
                            //Objects.requireNonNull(BARREL.getLocation().getWorld()).playSound(BARREL.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_7,100,0); //ЭТО ДЛЯ ДЕБАГА ЧТО БЫ ПОНЯТ РАБОТАЕТ ЛИ
                        }
                    }.runTaskLater(GribMine.getPlugin(GribMine.class),20*10*60);
                    activate(); // Вызываем активацию
                    cancel();
                }
            }
        }.runTaskTimer(GribMine.getPlugin(GribMine.class),0,1);

    }


    public void activate() {
        // Добавляем лут в бочку
        barrelBlock = addLootToBarrel();
        armor.remove();
    }

    private Block addLootToBarrel() {
        barrelBlock = armor.getLocation().getBlock(); // Блок, на котором находится ArmorStand
        barrelBlock.setType(Material.BARREL); // Устанавливаем тип блока на бочку

        // Устанавливаем направление бочки
        BlockData blockData = barrelBlock.getBlockData();
        if (blockData instanceof Directional directional) {
            directional.setFacing(BlockFace.WEST); // Направление бочки
            barrelBlock.setBlockData(directional);
        }

        // Добавляем лут в бочку
        airdropLoot.addLoot(barrelBlock);

        return barrelBlock;
    }

    public Location getLocation() {
        return location;
    }

    public static List<airdropMain> getAirdropList() {
        return airdropList;
    }
}