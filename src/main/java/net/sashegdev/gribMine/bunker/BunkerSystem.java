package net.sashegdev.gribMine.bunker;

import net.md_5.bungee.api.ChatColor;
import net.sashegdev.gribMine.DebugLogger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BunkerSystem {

    // Метод для проверки структуры бункера
    public static boolean isBunkerStructure(Block block) {
        Location loc = block.getLocation();
        World world = loc.getWorld();

        // Проверяем блоки структуры
        if (world.getBlockAt(loc).getType() == Material.WHITE_WOOL &&
                world.getBlockAt(loc.clone().add(1, 0, 0)).getType() == Material.DEEPSLATE_BRICKS &&
                world.getBlockAt(loc.clone().add(2, 0, 0)).getType() == Material.COAL_BLOCK &&
                world.getBlockAt(loc.clone().add(3, 0, 0)).getType() == Material.WAXED_COPPER_BLOCK &&
                world.getBlockAt(loc.clone().add(3, 1, 0)).getType() == Material.BARREL) {
            return true;
        }
        return false;
    }

    // Метод для создания бункера
    public static void createBunker(Player player, Block block) {
        if (isBunkerStructure(block)) {
            UUID bunkerUUID = UUID.randomUUID();
            Location barrelLocation = block.getLocation().clone().add(3, 1, 0);

            // Сохраняем данные бункера в bunkers.json
            BunkerData bunkerData = new BunkerData(bunkerUUID, barrelLocation);
            BunkerManager.saveBunkerData(bunkerData);

            // Логируем создание бункера
            DebugLogger.log("Бункер создан: " + bunkerUUID, DebugLogger.LogLevel.INFO);
            player.sendMessage(ChatColor.GREEN + "Бункер успешно создан!");
        } else {
            DebugLogger.log("Игрок " + player.getName() + " попытался создать бункер, но структура неверна", DebugLogger.LogLevel.WARNING);
            player.sendMessage(ChatColor.RED + "Структура бункера неверна!");
        }
    }
}