package net.sashegdev.gribMine;

import net.sashegdev.gribMine.weapon.WeaponAbility;
import net.sashegdev.gribMine.weapon.WeaponManager;
import net.sashegdev.gribMine.weapon.ability.FireAbility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import net.sashegdev.gribMine.commands.handleWeaponCommand;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public final class GribMine extends JavaPlugin implements CommandExecutor, Listener {

    Logger logger = getLogger();
    static FileConfiguration config;

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

    @EventHandler
    public void PlayerAttackEpta(EntityDamageByEntityEvent event) {
        // Проверяем, что атакующий - игрок
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();


            // Получаем предмет, который держит игрок
            ItemStack weapon = player.getInventory().getItemInMainHand();
            ItemMeta weaponMeta = weapon.getItemMeta();

            if (weaponMeta != null) {
                List<String> lore = weaponMeta.getLore();
                String rarity = null;
                String passiveAbility = null;
                // Проверяем наличие тега рарности и пассивной способности в лоре
                if (lore != null) {
                    for (String line : lore) {
                        if (line.startsWith("Редкость: ")) {
                            rarity = line.substring(10);
                        } else if (line.startsWith("Способность: ")) {
                            passiveAbility = line.substring(13); // Извлекаем пассивную способность
                        }
                    }
                }

                // Если рарность известна и есть пассивная способность
                if (rarity != null && weaponManager.getRarityList().contains(rarity) && passiveAbility != null) {
                    // Получаем список способностей для данной рарности
                    HashMap<String, WeaponAbility> abilities = WeaponManager.getWeaponAbilities();
                    if (abilities != null) {
                        // Проверяем, сработает ли способность
                        if (Math.random() < abilities.get(passiveAbility).getChance()) {
                            abilities.get(passiveAbility).activate(player); // Активируем способность на атакующем игроке
                        }
                    }
                }
            }
        }
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

    public static FileConfiguration getMineConfig() {
        return config;
    }
}