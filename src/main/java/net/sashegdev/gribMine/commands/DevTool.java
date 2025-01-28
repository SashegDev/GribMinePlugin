package net.sashegdev.gribMine.commands;

import net.md_5.bungee.api.ChatColor;
import net.sashegdev.gribMine.DebugLogger;
import net.sashegdev.gribMine.GribMine;
import net.sashegdev.gribMine.TPSUtil;
import net.sashegdev.gribMine.airdrop.airdropLoot;
import net.sashegdev.gribMine.airdrop.airdropMain;
import net.sashegdev.gribMine.core.LegendaryItem;
import net.sashegdev.gribMine.core.LegendaryRegistry;
import net.sashegdev.gribMine.weapon.WeaponAbility;
import net.sashegdev.gribMine.weapon.WeaponManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.sashegdev.gribMine.bunker.ZombieHordeListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class DevTool implements CommandExecutor {

    public DevTool() {
        // Регистрация команды в основном классе плагина
        Objects.requireNonNull(GribMine.getInstance().getCommand("devtool")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Эта команда доступна только игрокам.");
            return true;
        }

        if (!(GribMine.getMineConfig().getBoolean("dev_tools"))) {sender.sendMessage("DevTools currently is not active");return true;}

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Используйте: /devtool <status|plugins|config|loot_from_airdrop|summon_zombie_horde|airdrop_event|super_airdrop_event|test_weapon_abilities|test_legendary_abilities|get_all_abilities>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "status":
                printServerStatus(player);
                break;
            case "plugins":
                printPluginInfo(player);
                break;
            case "config":
                printConfigInfo(player);
                break;
            case "loot_from_airdrop":
                lootFromAirdrop(player);
                break;
            case "summon_zombie_horde":
                summonZombieHorde(player);
                break;
            case "airdrop_event":
                airdropEvent(player);
                break;
            case "super_airdrop_event":
                player.sendMessage(ChatColor.RED + "Эта команда еще не реализована.");
                break;
            case "test_weapon_abilities":
                testWeaponAbilities(player);
                break;
            case "test_legendary_abilities":
                testLegendaryAbilities(player);
                break;
            case "get_all_abilities":
                getAllAbilities(player);
                break;
            case "get_item_meta":
                getItemMeta(player);
                break;
            default:
                player.sendMessage(ChatColor.RED + "Неизвестная команда. Используйте: /devtool <status|plugins|config|loot_from_airdrop|summon_zombie_horde|airdrop_event|super_airdrop_event|test_weapon_abilities|test_legendary_abilities|get_all_abilities|get_item_meta>");
                break;
        }

        return true;
    }

    private void printServerStatus(Player player) {
        String status = "Server Status: \n" +
                "Online Players: " + Bukkit.getOnlinePlayers().size() + "\n" +
                "Max Players: " + Bukkit.getMaxPlayers() + "\n" +
                "TPS: " + TPSUtil.getTPS(); // Используем существующий метод для получения TPS

        player.sendMessage(ChatColor.GREEN + status);
    }

    private void printPluginInfo(Player player) {
        StringBuilder pluginInfo = new StringBuilder(ChatColor.GOLD + "Loaded Plugins:\n");
        for (var plugin : Bukkit.getPluginManager().getPlugins()) {
            pluginInfo.append(ChatColor.YELLOW).append(plugin.getName()).append(" - Version: ").append(plugin.getDescription().getVersion()).append("\n");
        }
        player.sendMessage(pluginInfo.toString());
    }

    private void printConfigInfo(Player player) {
        StringBuilder configInfo = new StringBuilder(ChatColor.GOLD + "Configuration Settings:\n");
        for (String key : GribMine.getMineConfig().getKeys(true)) {
            configInfo.append(ChatColor.YELLOW).append(key).append(": ").append(GribMine.getMineConfig().get(key)).append("\n");
        }
        player.sendMessage(configInfo.toString());
    }

    private void lootFromAirdrop(Player player) {
        airdropLoot.addLootToPlayerInventory(player);
    }

    private void summonZombieHorde(Player player) {
        Zombie zombi = player.getWorld().spawn(player.getLocation(), Zombie.class);
        ZombieHordeListener.spawnHorde(player.getLocation(),zombi);
    }

    private void airdropEvent(Player player) {
        // Логика для спавна аирдропа в радиусе 1.2 км вокруг случайного игрока
        List<Player> onlinePlayers = (List<Player>) Bukkit.getOnlinePlayers();
        if (!onlinePlayers.isEmpty()) {
            Player randomPlayer = onlinePlayers.get(new Random().nextInt(onlinePlayers.size()));
            Location randomLocation = randomPlayer.getLocation().add(new Random().nextInt(-1200, 1200), 0, new Random().nextInt(-1200, 1200));
            new airdropMain(randomPlayer, (int) randomLocation.getX(), (int) randomLocation.getZ());
            player.sendMessage(ChatColor.GREEN + "Аирдроп был вызван в радиусе 1.2 км вокруг игрока " + randomPlayer.getName() + ".");
        } else {
            player.sendMessage(ChatColor.RED + "Нет игроков онлайн для генерации аирдропа.");
        }
    }

    private void testWeaponAbilities(Player player) {
        // Логика для тестирования способностей оружия
        for (WeaponAbility ability : WeaponManager.getWeaponAbilities().values()) {
            try {
                ability.activate(player, player); // Эмулируем активацию способности с 100% шансом
                player.sendMessage(ChatColor.GREEN + ability.getRussianName() + " - да"); // Успешная активация
            } catch (Exception e) {
                // Логируем ошибку в консоль
                DebugLogger.log("Ошибка при активации способности " + ability.getRussianName() + ": " + e.getMessage(), DebugLogger.LogLevel.ERROR);
                player.sendMessage(ChatColor.RED + ability.getRussianName() + " - нет"); // Ошибка при активации
            }
        }
    }

    private void testLegendaryAbilities(Player player) {
        // Логика для тестирования легендарных способностей
        for (LegendaryItem item : LegendaryRegistry.getAll()) { // Получаем все легендарные предметы
            if (item.isEnabled()) { // Проверяем, включен ли предмет
                try {
                    item.onUse(player); // Эмулируем активацию способности с 100% шансом
                    player.sendMessage(ChatColor.GREEN + item.getId() + " - да"); // Успешная активация
                } catch (Exception e) {
                    // Логируем ошибку в консоль
                    DebugLogger.log("Ошибка при активации способности " + item.getId() + ": " + e.getMessage(), DebugLogger.LogLevel.ERROR);
                    player.sendMessage(ChatColor.RED + item.getId() + " - нет"); // Ошибка при активации
                }
            }
        }
    }

    private void getItemMeta(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        DebugLogger.log("ItemMeta: " + Objects.requireNonNull(item.getItemMeta()).getAsString(), DebugLogger.LogLevel.INFO);
    }

    private void getAllAbilities(Player player) {
        // Логика для выдачи мечей с каждой способностью
        for (WeaponAbility ability : WeaponManager.getWeaponAbilities().values()) {
            ItemStack sword = new ItemStack(Material.WOODEN_SWORD);
            ItemMeta meta = sword.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.GOLD + ability.getRussianName());
                List<String> lore = new ArrayList<>();
                lore.add("Редкость: common");
                lore.add("Способность: " + ability.getRussianName());
                meta.setLore(lore);
                sword.setItemMeta(meta);
            }
            player.getInventory().addItem(sword);
        }
        player.sendMessage(ChatColor.GREEN + "Все способности выданы в виде деревянных мечей.");
    }
}