package net.sashegdev.gribMine.airdrop;

import org.bukkit.*;
import org.bukkit.block.Barrel;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
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

    public airdropMain(@NotNull Player p) {
        this(p, 100, 100);
    }

    public airdropMain(@NotNull Player p, int w, int h) {
        this.location = p.getLocation().add(new Random().nextInt(-w, w), 0, new Random().nextInt(-h, h));
        this.location.setY(p.getLocation().getY() + 100); // Устанавливаем высоту

        System.out.println("Airdrop! X:" + location.getX() + " Z:" + location.getZ());
        new BukkitRunnable() {
            @Override
            public void run() {
                armorstandcheck();
            }
        }.runTaskTimer(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("GribMine")),0,1);

        new BukkitRunnable() {
            @Override
            public void run() {
                // Создаем ArmorStand
                armor = p.getWorld().spawn(location, ArmorStand.class);
                armor.setInvisible(true); // ArmorStand невидим
                armor.setInvulnerable(true); // ArmorStand не получает урона
                armor.setCustomName(ChatColor.RED + "AirDrop"); // Устанавливаем имя
                armor.setCustomNameVisible(true); // Имя будет видно игрокам

                // Устанавливаем бочку на голову ArmorStand
                Objects.requireNonNull(armor.getEquipment()).setHelmet(new org.bukkit.inventory.ItemStack(Material.BARREL));

                // Добавляем эффект медленного падения
                armor.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, PotionEffect.INFINITE_DURATION, 87));

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

    public void armorstandcheck() {
        if (armor.isOnGround()) {
            activation();
        }
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
        // Молись богу, чтобы этот метод работал так, как я его задумывал
        new BukkitRunnable() {
            @Override
            public void run() {
                boolean isAir = true;

                // Проверяем блоки под ArmorStand
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        if (armor.getLocation().add(x, -1, z).getBlock().getType() != Material.AIR) {
                            isAir = false;
                            break;
                        }
                    }
                    if (!isAir) break;
                }

                if (!isAir) {
                    location = armor.getLocation();
                    armor.remove();

                    // Устанавливаем блок на место
                    location.getBlock().setType(Material.BARREL);

                    // Получаем BlockData для бочки и устанавливаем направление
                    BlockData blockData = location.getBlock().getBlockData();
                    if (blockData instanceof Directional directional) {
                        directional.setFacing(BlockFace.WEST); // Устанавливаем направление на запад
                        location.getBlock().setBlockData(directional);
                    }

                    // Добавляем лут в бочку
                    airdropLoot.addLoot(location.getBlock());

                    // Создаем случайный ключ
                    ItemStack key = new ItemStack(Material.TRIPWIRE_HOOK); // Замените на нужный тип ключа
                    ItemMeta keyMeta = key.getItemMeta();
                    if (keyMeta != null) {
                        keyMeta.setDisplayName(ChatColor.GOLD + "Случайный ключ");
                        keyMeta.getPersistentDataContainer().set(new NamespacedKey("yourplugin", "Key"), PersistentDataType.STRING, "Key"); // Устанавливаем тег
                        key.setItemMeta(keyMeta);
                    }

                    // Устанавливаем ключ в бочку
                    Block barrelBlock = location.getBlock();
                    if (barrelBlock.getState() instanceof Barrel barrel) {
                        barrel.getInventory().addItem(key); // Добавляем ключ в инвентарь бочки
                        barrel.update(); // Обновляем состояние бочки
                    }

                    // Запускаем таймер на 5 минут
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            // Логика удаления тега
                            if (barrelBlock.getState() instanceof Barrel barrel) {
                                for (ItemStack item : barrel.getInventory().getContents()) {
                                    if (item != null && item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().has(new NamespacedKey("GribMine", "Key"), PersistentDataType.STRING)) {
                                        // Удаляем тег
                                        ItemMeta meta = item.getItemMeta();
                                        if (meta != null) {
                                            meta.getPersistentDataContainer().remove(new NamespacedKey("GribMine", "Key")); // Удаляем тег
                                            item.setItemMeta(meta);
                                        }
                                    }
                                }
                            }

                            // Сообщение игрокам
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.sendMessage(ChatColor.RED + "Дроп закрыт на координатах: " + location.getX() + ", " + location.getZ());
                            }
                        }
                    }.runTaskLater(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("GribMine")), 5 * 60 * 20); // 5 минут

                    cancel(); // Останавливаем текущий таск
                }
            }
        }.runTaskTimer(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("GribMine")), 0, 5);
    }
    public Location getLocation() { return location; }
    public static List<airdropMain> getAirdropList() { return airdropList; }
}

