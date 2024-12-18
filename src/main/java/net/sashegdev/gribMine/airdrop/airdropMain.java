package net.sashegdev.gribMine.airdrop;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
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

    private Location location;
    private final static List<airdropMain> airdropList = new ArrayList<>();

    private LivingEntity armor;

    //TODO: допилить метод рандом дропа базируюясь на atme
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
        this.location.setY(p.getLocation().getY() + 1000);
        System.out.println("Airdrop! X:" + location.getX() + " Z:" + location.getZ());

        new BukkitRunnable() {
            @Override
            public void run() {
                // Создаем ArmorStand
                armor = p.getWorld().spawn(location, ArmorStand.class);
                armor.setInvisible(true); // ArmorStand невидим
                armor.setInvulnerable(true); // ArmorStand не получает урона
                armor.setCustomName(ChatColor.RED+"AirDrop"); // Устанавливаем имя
                armor.setCustomNameVisible(true); // Имя будет видно игрокам

                // Устанавливаем бочку на голову ArmorStand
                armor.getEquipment().setHelmet(new org.bukkit.inventory.ItemStack(Material.BARREL));

                // Добавляем эффект медленного падения
                armor.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, PotionEffect.INFINITE_DURATION, 28));

                // Отправляем сообщение всем игрокам
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(ChatColor.GRAY +
                            "Я увидел самолет...\n" +
                            "И он что-то выбросил...\n" +
                            "Это примерно на:\n" +
                            "X:" + ChatColor.GOLD + location.getX() + ChatColor.RESET + " Z:" + ChatColor.GOLD + location.getZ()
                    );
                }
            }
        }.runTaskLater(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("GribMine")), 20 * 25);
    }

    @EventHandler
    public void NononoMisterFishYouDontWantBreakTheAirDrop(BlockBreakEvent event) {
        Player pl = event.getPlayer();
        Block block = event.getBlock();

        // Проверяем, совпадает ли локация и является ли блок бочкой
        if (block.getLocation().getX() == this.location.getX() &&
                block.getLocation().getZ() == this.location.getZ() &&
                block.getType() == Material.BARREL) {
            event.setCancelled(true);
            pl.sendMessage(ChatColor.RED+"AirDrop cannot be broken by player");
        }
    }

    public void activation() {
        //Молись богу что бы этот метод работал так, как я его задумывал
        //TODO: починить рофлы с травой/цветами   так же добавить таймер 5 минут по истечению которого будет открыт дроп
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
                    if (blockData instanceof Directional directional) {
                        directional.setFacing(BlockFace.WEST); // Устанавливаем направление на запад
                        location.getBlock().setBlockData(directional);
                        airdropLoot.addLoot(location.getBlock());
                    }

                    cancel();
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("GribMine"), 0, 5);
    }
    public Location getLocation() { return location; }
    public static List<airdropMain> getAirdropList() { return airdropList; }
}

