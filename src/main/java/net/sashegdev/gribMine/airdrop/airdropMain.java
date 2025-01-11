package net.sashegdev.gribMine.airdrop;

import net.sashegdev.gribMine.GribMine;
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
import org.bukkit.persistence.PersistentDataContainer;
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
    private static LivingEntity armor;

    public airdropMain(@NotNull Player p) {
        this(p, 100, 100);
    }

    public airdropMain(@NotNull Player p, int w, int h) {
        this.location = p.getLocation().add(new Random().nextInt(-w, w), 0, new Random().nextInt(-h, h));
        this.location.setY(p.getLocation().getY() + 100); // Устанавливаем высоту

        System.out.println("Airdrop! X:" + location.getX() + " Z:" + location.getZ());
        airdropList.add(this);

        new BukkitRunnable() {
            @Override
            public void run() {
                // Создаем ArmorStand
                armor = p.getWorld().spawn(location, ArmorStand.class);
                armor.setInvisible(true); // ArmorStand невидим
                armor.setInvulnerable(true); // ArmorStand не получает урона
                armor.setCustomName(ChatColor.RED + "AirDrop"); // Устанавливаем имя
                armor.setCustomNameVisible(true); // Имя будет видно игрокам
                armor.setCanPickupItems(false); // Запретить подбирать предметы

                // Устанавливаем бочку на голову ArmorStand
                Objects.requireNonNull(armor.getEquipment()).setHelmet(new ItemStack(Material.BARREL));

                // Добавляем эффект максимального медленного падения
                armor.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, Integer.MAX_VALUE, 255));

                // Отправляем сообщение всем игрокам
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

    public static void startCheckingForGround() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (armor != null && armor.isOnGround()) {
                    activate(); // Call the activate method when armor is on the ground
                    cancel(); // Stop this task
                }
            }
        }.runTaskTimer(GribMine.getPlugin(GribMine.class), 0, 1); // Check every tick (1 tick = 1/20 second)
    }

    public static void activate() {
        // Добавляем лут в бочку
        addLootToBarrel();

        // Получаем время таймера из конфигурации
        int timerDuration = GribMine.getMineConfig().getInt("AirDropTimer"); // Время в секундах

        // Создаем новый BukkitRunnable для отсчета времени
        new BukkitRunnable() {
            int timeLeft = timerDuration; // Время, оставшееся до открытия бочки

            @Override
            public void run() {

                Block barrelBlock = armor.getLocation().getBlock();
                // Проверяем, осталось ли время
                if (timeLeft > 0) {
                    // Отображаем оставшееся время над бочкой
                    armor = barrelBlock.getWorld().spawn(barrelBlock.getLocation(), ArmorStand.class);
                    armor.setInvisible(true); // ArmorStand невидим
                    armor.setInvulnerable(true); // ArmorStand не получает урона
                    armor.setCustomNameVisible(true); // Имя будет видно игрокам
                    armor.setCanPickupItems(false); // Запретить подбирать предметы
                    armor.setCustomName(ChatColor.RED + "AirDrop (Открытие через " +ChatColor.RED+ timeLeft +ChatColor.RED+ " секунд)");
                    timeLeft--; // Уменьшаем оставшееся время
                } else {
                    // Получаем блок бочки


                    // Удаляем тег Lock
                    if (barrelBlock.getState() instanceof Barrel barrel) {
                        ItemStack item = barrel.getInventory().getItem(0);
                        if (item != null && item.hasItemMeta()) {
                            ItemMeta meta = item.getItemMeta();
                            if (meta != null) {
                                meta.getPersistentDataContainer().remove(new NamespacedKey("gribmine", "Lock")); // Удаляем тег Lock
                                item.setItemMeta(meta); // Обновляем предмет
                            }
                        }
                    }

                    // Удаляем ArmorStand или обновляем его имя
                    armor.setCustomName(ChatColor.GREEN + "AirDrop (Теперь открыто!)");
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            armor.remove();
                        }
                    }.runTaskLater(GribMine.getPlugin(GribMine.class),3*20);
                    cancel(); // Останавливаем задачу
                }
            }
        }.runTaskTimer(GribMine.getPlugin(GribMine.class), 0, 20); // Запускаем каждую секунду (20 тиков)
    }

    public airdropMain() {
    }

    private static void addLootToBarrel() {
        // Устанавливаем блок на место
        Block barrelBlock = armor.getLocation().getBlock();
        barrelBlock.setType(Material.BARREL);

        // Получаем BlockData для бочки и устанавливаем направление
        BlockData blockData = barrelBlock.getBlockData();
        if (blockData instanceof Directional directional) {
            directional.setFacing(BlockFace.WEST); // Устанавливаем направление на запад
            barrelBlock.setBlockData(directional);
        }

        // Добавляем лут в бочку
        airdropLoot.addLoot(barrelBlock);

        // Добавляем тег к бочке
        if (barrelBlock.getState() instanceof Barrel barrel) {
            // Получаем PersistentDataContainer для бочки
            ItemMeta meta = barrel.getInventory().getItem(0).getItemMeta(); // Получаем первый предмет в инвентаре
            if (meta != null) {
                // Устанавливаем метаданные для самой бочки
                PersistentDataContainer container = barrel.getPersistentDataContainer();
                String randomKey = generateRandomKey(); // Генерируем случайный ключ
                container.set(new NamespacedKey("gribmine", "Lock"), PersistentDataType.STRING, randomKey);
                container.set(new NamespacedKey("gribmine", "Tag"), PersistentDataType.STRING, "AirDrop");
            }
        }

        // Удаляем ArmorStand
        armor.remove();
    }

    private static String generateRandomKey() {
        // Генерация случайного ключа (например, 10 случайных символов)
        StringBuilder key = new StringBuilder();
        Random random = new Random();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < 10; i++) {
            key.append(characters.charAt(random.nextInt(characters.length())));
        }
        return key.toString();
    }

    @EventHandler
    public void NononoMisterFishYouDontWantBreakTheAirDrop(BlockBreakEvent event) {
        Player pl = event.getPlayer();
        Block block = event.getBlock();

        // Проверяем, является ли блок бочкой
        if (block.getState() instanceof Barrel barrel) {
            // Получаем первый предмет в инвентаре бочки
            ItemStack item = barrel.getInventory().getItem(0);

            // Проверяем, существует ли предмет и имеет ли он метаданные
            if (item != null && item.hasItemMeta()) {
                // Получаем тег Lock
                String key = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey("gribmine", "Lock"), PersistentDataType.STRING);

                // Получаем тег Tag
                String tag = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey("gribmine", "Tag"), PersistentDataType.STRING);

                // Проверяем, есть ли тег Tag со значением "AirDrop" и тег Lock
                if ("AirDrop".equals(tag) && key != null) {
                    event.setCancelled(true);
                    pl.sendMessage(ChatColor.RED + "AirDrop cannot be broken by player");
                }
            }
        }
    }
    public Location getLocation() { return location; }
    public static List<airdropMain> getAirdropList() { return airdropList; }
}