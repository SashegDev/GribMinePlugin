package net.sashegdev.gribMine;

import net.sashegdev.gribMine.weapon.WeaponManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import net.sashegdev.gribMine.commands.handleWeaponCommand;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public final class GribMine extends JavaPlugin implements CommandExecutor {

    Logger logger = getLogger();
    FileConfiguration config;

    private WeaponManager weaponManager;

    @Override
    public void onEnable() {
        // Загружаем конфигурацию
        saveDefaultConfig();
        config = getConfig();

        // Извлекаем список рарностей из конфигурации
        List<String> rarityList = config.getStringList("rarity_list");

        // Извлекаем множители урона из конфигурации
        HashMap<String, Double> damageModifiers = new HashMap<>();
        for (String rarity : rarityList) {
            double modifier = config.getDouble("damage_mod." + rarity, 1.0); // Значение по умолчанию 1.0
            damageModifiers.put(rarity, modifier);
        }

        // Инициализируем WeaponManager с полученным списком рарностей и множителями
        weaponManager = new WeaponManager(rarityList, damageModifiers);

        System.out.println("GribMine Plugin initialized ;)");
        System.out.println("Версия плагина: " + getDescription().getVersion());

        logger.info("===RARITY_LIST===");
        for (String string : rarityList) {logger.info(string);}

        logger.info("===DAMAGE_MOD==");
        for (String rarity : rarityList) {
            double modifier = config.getDouble("damage_mod." + rarity, 1.0);
            logger.info("damage_mod."+rarity+": "+modifier);
        }

        // Регистрируем команды
        getCommand("gribadmin").setExecutor(this);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("gribadmin")) {
            if (args.length == 0) {
                sender.sendMessage("Используйте /gribadmin <reload|get_config|weapon>");
                return true;
            }
            switch (args[0].toLowerCase()) {

                case "reload":
                    reloadConfig();
                    sender.sendMessage("Конфигурация перезагружена.");
                    break;

                case "get_config":
                    StringBuilder configMessage = new StringBuilder("Конфигурация:\n");
                    // Получаем все ключи верхнего уровня
                    for (String key : config.getKeys(false)) {
                        Object value = config.get(key); // Получаем значение по ключу
                        configMessage.append(key).append(": ").append(value).append("\n");
                    }

                    // Отправляем сообщение
                    sender.sendMessage(configMessage.toString());
                    break;

                case "weapon":
                    if (args.length < 2) {
                        sender.sendMessage("Используйте /gribadmin weapon <get|set|reassemble|reset>");
                        return true;
                    }
                    new handleWeaponCommand(sender, args);
                    break;

                default:
                    sender.sendMessage("Неизвестная подкоманда.");
                    break;

            }return true;

        }return false;
    }
}