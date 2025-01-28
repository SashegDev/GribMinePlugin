package net.sashegdev.gribMine.airdrop;

import net.sashegdev.gribMine.DebugLogger;
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
        this.location.setY(p.getLocation().getY() + 250); // Устанавливаем высоту

        DebugLogger.log("Airdrop! X:" + location.getBlockX() + " Z:" + location.getBlockZ(),DebugLogger.LogLevel.INFO);
        airdropList.add(this);

        // Спавн колонны частиц на месте игрока
        spawnParticleColumn(p.getLocation());

        new BukkitRunnable() {
            @Override
            public void run() {
                armor = p.getWorld().spawn(location, ArmorStand.class);
                armor.setInvisible(true);
                armor.setInvulnerable(true);
                armor.setGravity(false);
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
                DebugLogger.log("Airdrop spawned at X:" + location.getBlockX() + " Z:" + location.getBlockZ(),DebugLogger.LogLevel.INFO);
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
                if (ticks < 20*35) {
                    // Получаем начальную координату Y
                    double startY = playerLocation.getY(); // Используем double для большей точности
                    // Цикл для спавна частиц от startY до startY + 18 с шагом 0.2
                    for (double y = startY; y <= startY + 24; y += 0.2) {
                        Location particleLocation = new Location(playerLocation.getWorld(), playerLocation.getX(), y, playerLocation.getZ());
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.spawnParticle(Particle.WITCH, particleLocation, 1, 0.2, 0, 0.2, 0.001, null, true);
                        }
                    }
                    ticks++;
                } else {
                    cancel(); // Останавливаем задачу после 20 секунд
                }
            }
        }.runTaskTimer(GribMine.getPlugin(GribMine.class), 0, 1); // Запускаем сразу и выполняем каждый тик
    }

    public void startFalling() {
        double fallingSpeed = GribMine.getMineConfig().getDouble("airdrop_falling_speed", 0.5); // Скорость кастомного падения
        int updateInterval = GribMine.getMineConfig().getInt("airdrop_update_interval", 2); // Интервал обновления падения

        new BukkitRunnable() {
            boolean useVanillaFall = false; // Флаг для переключения на ванильное падение
            final int surfaceY = getSurfaceY(armor.getLocation()); // Высота поверхности (кэшируем)
            final int vanillaFallHeight = surfaceY + 5; // Высота, на которой включается ванильное падение (5 блоков выше поверхности)

            @Override
            public void run() {
                if (armor == null || armor.isDead()) {
                    DebugLogger.log("Airdrop is null or dead. Stopping fall.", DebugLogger.LogLevel.INFO);
                    cancel();
                    return;
                }

                Location armorLocation = armor.getLocation();

                // Если аирдроп ещё не переключился на ванильное падение
                if (!useVanillaFall) {
                    // Проверяем, достиг ли аирдроп высоты vanillaFallHeight
                    if (armorLocation.getY() <= vanillaFallHeight) {
                        useVanillaFall = true; // Переключаемся на ванильное падение
                        armor.setGravity(true); // Включаем гравитацию
                        DebugLogger.log("Airdrop has reached vanilla fall height. Switching to vanilla gravity.", DebugLogger.LogLevel.INFO);
                    } else {
                        // Продолжаем падение с кастомной механикой
                        armor.setGravity(false); // Отключаем гравитацию
                        armorLocation.setY(armorLocation.getY() - fallingSpeed);
                        armor.teleport(armorLocation);
                        DebugLogger.log("Airdrop is falling with custom speed. Current Y: " + armorLocation.getY(), DebugLogger.LogLevel.INFO);
                    }
                } else {
                    // Если аирдроп близко к поверхности, используем ванильное падение
                    if (armorLocation.getY() - surfaceY <= 0.5) {
                        DebugLogger.log("Airdrop has landed. Activating.", DebugLogger.LogLevel.INFO);
                        activate();
                        cancel();
                        return;
                    }

                    // Продолжаем падение с ванильной гравитацией
                    DebugLogger.log("Airdrop is falling with vanilla gravity. Current Y: " + armorLocation.getY(), DebugLogger.LogLevel.INFO);
                }
            }
        }.runTaskTimer(GribMine.getPlugin(GribMine.class), 0, updateInterval);
    }

    // Метод для определения высоты поверхности (максимальный Y блока под аирдропом)
    private int getSurfaceY(Location location) {
        World world = location.getWorld();
        int x = location.getBlockX();
        int z = location.getBlockZ();

        // Ищем самый верхний блок на координатах X и Z
        assert world != null;
        for (int y = world.getMaxHeight(); y >= world.getMinHeight(); y--) {
            Block block = world.getBlockAt(x, y, z);
            if (block.getType().isSolid()) {
                DebugLogger.log("Surface found at Y: " + y, DebugLogger.LogLevel.INFO);
                return y + 1; // Возвращаем Y поверхности (верхний блок + 1)
            }
        }

        DebugLogger.log("No surface found. Using min height: " + world.getMinHeight(), DebugLogger.LogLevel.INFO);
        return world.getMinHeight(); // Если поверхность не найдена, возвращаем минимальную высоту мира
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
        armor.getWorld().getChunkAt(armor.getLocation()).load();
        barrelBlock.setType(Material.BARREL); // Устанавливаем тип блока на бочку

        // Устанавливаем направление бочки
        BlockData blockData = barrelBlock.getBlockData();
        if (blockData instanceof Directional directional) {
            directional.setFacing(BlockFace.WEST); // Направление бочки
            barrelBlock.setBlockData(directional);
        }

        // Добавляем лут в бочку
        airdropLoot.addLoot(barrelBlock);
        DebugLogger.log("Loot added to barrel at X:" + barrelBlock.getX() + " Y:" + barrelBlock.getY() + " Z:" + barrelBlock.getZ(),DebugLogger.LogLevel.INFO);

        return barrelBlock;
    }

    public Location getLocation() {
        return location;
    }
}