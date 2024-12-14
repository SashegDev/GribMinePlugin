package net.sashegdev.gribMine;

import net.sashegdev.gribMine.weapon.WeaponAbility;
import net.sashegdev.gribMine.weapon.WeaponManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
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

        // Инициализируем WeaponManager
        List<String> rarityList = config.getStringList("rarity_list");
        HashMap<String, Double> damageModifiers = new HashMap<>();
        for (String rarity : rarityList) {
            double modifier = config.getDouble("damage_mod." + rarity, 1.0);
            damageModifiers.put(rarity, modifier);
        }
        weaponManager = new WeaponManager(rarityList, damageModifiers);

        // Регистрируем слушатели
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(weaponManager, this); // Регистрация WeaponManager как слушателя

        // Логирование
        logger.info("GribMine Plugin initialized ;)");
        logger.info("Версия плагина: " + getDescription().getVersion());
        logger.info("===RARITY_LIST===");
        for (String string : rarityList) {
            logger.info(string);
        }
        logger.info("===DAMAGE_MOD==");
        for (String rarity : rarityList) {
            double modifier = config.getDouble("damage_mod." + rarity, 1.0);
            logger.info("damage_mod." + rarity + ": " + modifier);
        }

        // Регистрируем команды
        getCommand("gribadmin").setExecutor(this);
    }

    @EventHandler
    public void PlayerAttackEpta(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (damager instanceof Player) {
            Player player = (Player) damager;
            logger.info("player attacked ponos!");

            ItemStack weapon = player.getInventory().getItemInMainHand();
            ItemMeta weaponMeta = weapon.getItemMeta();

            if (weaponMeta != null) {
                List<String> lore = weaponMeta.getLore();
                String rarity = null;
                String passiveAbility = null;

                if (lore != null) {
                    for (String line : lore) {
                        if (line.startsWith("Редкость: ")) {
                            rarity = line.substring(10);
                        } else if (line.startsWith("Способность: ")) {
                            passiveAbility = line.substring(13);
                        }
                    }
                }

                if (rarity != null && weaponManager.getRarityList().contains(rarity) && passiveAbility != null) {
                    HashMap<String, WeaponAbility> abilities = WeaponManager.getWeaponAbilities();
                    if (abilities != null) {
                        WeaponAbility ability = abilities.get(WeaponManager.getNameByRussian(passiveAbility));
                        if (ability != null) {
                            if (Math.random() < ability.getChance()) {
                                logger.info("Сработала способка!");
                                ability.activate(player);
                            }
                        } else {
                            logger.warning("Способность не найдена: " + passiveAbility);
                        }
                    }
                }
            }
        } else if (damager instanceof Arrow) {
            Arrow arrow = (Arrow) damager;
            Entity shooter = (Entity) arrow.getShooter();
            if (shooter instanceof Player) {
                Player player = (Player) shooter;
                logger.info("player shot an arrow!");

                ItemStack weapon = player.getInventory().getItemInMainHand();
                ItemMeta weaponMeta = weapon.getItemMeta();

                if (weaponMeta != null) {
                    List<String> lore = weaponMeta.getLore();
                    String rarity = null;
                    String passiveAbility = null;

                    if (lore != null) {
                        for (String line : lore) {
                            if (line.startsWith("Редкость: ")) {
                                rarity = line.substring(10);
                            } else if (line.startsWith("Способность: ")) {
                                passiveAbility = line.substring(13);
                            }
                        }
                    }

                    if (rarity != null && weaponManager.getRarityList().contains(rarity) && passiveAbility != null) {
                        HashMap<String, WeaponAbility> abilities = WeaponManager.getWeaponAbilities();
                        if (abilities != null) {
                            WeaponAbility ability = abilities.get(WeaponManager.getNameByRussian(passiveAbility));
                            if (ability != null) {
                                if (Math.random() < ability.getChance()) {
                                    logger.info("Сработала способка от стрелы!");
                                    ability.activate(player);
                                }
                            } else {
                                logger.warning("Способность не найдена: " + passiveAbility);
                            }
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
                    for (String key : config.getKeys(false)) {
                        Object value = config.get(key);
                        configMessage.append(key).append(": ").append(value).append("\n");
                    }
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
            }
            return true;
        }
        return false;
    }

    public static FileConfiguration getMineConfig() {
        return config;
    }
}