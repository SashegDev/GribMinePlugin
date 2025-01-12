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
    private boolean gravityEnabled = false; // Флаг для включения гравитации

    public airdropMain(@NotNull Player p) {
        this(p, 350, 350);
    }

    public airdropMain(@NotNull Player p, int w, int h) {
        this.location = p.getLocation().add(new Random().nextInt(-w, w), 0, new Random().nextInt(-h, h));
        this.location.setY(p.getLocation().getY() + 500); // Устанавливаем высоту

        System.out.println("Airdrop! X:" + location.getX() + " Z:" + location.getZ());
        airdropList.add(this);

        // Спавн колонны частиц на месте игрока
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

                // Убираем гравитацию на старте
                armor.setGravity(false);

                // Уведомляем игроков
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(ChatColor.GRAY +
                            "Я увидел самолет...\n" +
                            "И он что-то выбросил...\n" +
                            "Где то на:\n" +
                            "X:" + ChatColor.GOLD + location.getBlockX() + ChatColor.GRAY + " Z:" + ChatColor.GOLD + location.getBlockZ()
                    );
                }
                startFalling();
            }
        }.runTaskLater(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("GribMine")), 20 * 25);
    }

    private void spawnParticleColumn(Location playerLocation) {
        // Запускаем новый BukkitRunnable для спавна частиц в течение 20 секунд
        new BukkitRunnable() {
            int ticks = 0; // Счетчик тиков

            @Override
            public void run() {
                // Спавним частицы каждый тик в течение 20 секунд (20 секунд = 400 тиков)
                if (ticks < 400) {
                    // Получаем начальную координату Y
                    double startY = playerLocation.getY(); // Используем double для большей точности
                    // Цикл для спавна частиц от startY до startY + 18 с шагом 0.2
                    for (double y = startY; y <= startY + 18; y += 0.2) {
                        Location particleLocation = new Location(playerLocation.getWorld(), playerLocation.getX(), y, playerLocation.getZ());
                        Objects.requireNonNull(playerLocation.getWorld()).spawnParticle(Particle.WITCH, particleLocation, 1, 0.2, 0, 0.2, 0.001);
                    }
                    ticks++;
                } else {
                    cancel(); // Останавливаем задачу после 20 секунд
                }
            }
        }.runTaskTimer(GribMine.getPlugin(GribMine.class), 0, 1); // Запускаем сразу и выполняем каждый тик
    }

    public void startFalling() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (armor == null || armor.isDead()) {
                    cancel();
                    return;
                }

                Location armorLocation = armor.getLocation();
                Block blockBelow = armorLocation.getBlock().getRelative(BlockFace.DOWN);

                // Проверяем, находится ли аирдроп над водой
                if (blockBelow.getType() == Material.WATER) {
                    activate(); // Активируем аирдроп, если он над водой
                    cancel();
                    return;
                }

                // Проверяем, находится ли аирдроп на высоте 5 блоков над землей
                if (!gravityEnabled && armorLocation.getY() - blockBelow.getY() <= 5) {
                    gravityEnabled = true;
                    armor.setGravity(true); // Включаем гравитацию
                }

                // Если гравитация включена, проверяем, достиг ли аирдроп земли
                if (gravityEnabled && armor.isOnGround()) {
                    activate(); // Активируем аирдроп, если он на земле
                    cancel();
                } else if (!gravityEnabled) {
                    // Медленно опускаем аирдроп, если гравитация выключена
                    armorLocation.setY(armorLocation.getY() - 0.1); // Кастомная скорость падения
                    armor.teleport(armorLocation);
                }
            }
        }.runTaskTimer(GribMine.getPlugin(GribMine.class), 0, 1); // Запускаем сразу и выполняем каждый тик
    }

    public void activate() {
        // Добавляем лут в бочку
        barrelBlock = addLootToBarrel();
        if (armor != null) {
            armor.remove();
        }
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