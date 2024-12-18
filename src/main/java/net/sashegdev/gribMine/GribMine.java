package net.sashegdev.gribMine;

import net.sashegdev.gribMine.airdrop.airdropMain;
import net.sashegdev.gribMine.airdrop.commands.summon;
import net.sashegdev.gribMine.commands.handleWeaponCommand;
import net.sashegdev.gribMine.weapon.WeaponAbility;
import net.sashegdev.gribMine.weapon.WeaponManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public final class GribMine extends JavaPlugin implements CommandExecutor, Listener {

    Logger logger = getLogger();
    static FileConfiguration config;

    private WeaponManager weaponManager;
    private airdropMain airdropMain;

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
        getServer().getPluginManager().registerEvents(weaponManager, this);
        getServer().getPluginManager().registerEvents(airdropMain,this);

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

        getCommand("gribadmin").setExecutor(this);

        //чисто для того что бы чекать инвент каждый тик вместо кривого листенера
        WeaponManager.ChangeWeapon();
    }

    @EventHandler
    public void PlayerAttackEpta(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (damager instanceof Player) {
            Player player = (Player) damager;
            //logger.info("player attacked ponos!");

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
                                if (player.getCooldown(player.getInventory().getItemInMainHand()) <= 1) {
                                    //logger.info("Сработала способка!");
                                    ability.activate(player, event.getEntity()); // Передаем целевую сущность
                                }
                            }
                        } else {
                            logger.warning("Способность не найдена: " + passiveAbility);
                        }
                    }
                }
            }
        } else if (damager instanceof Arrow arrow) {
            Entity shooter = (Entity) arrow.getShooter();
            if (shooter instanceof Player player) {
                //logger.info("player shot an arrow!");

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
                                    if (player.getCooldown(player.getInventory().getItemInMainHand()) <= 1) {
                                        //logger.info("Сработала способка от стрелы!");
                                        ability.activate(player, event.getEntity()); // Передаем целевую сущность
                                    }
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

    @EventHandler
    public void ClickHandler(PlayerInteractEvent e) {
        if (e.getItem() == null) return;
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
            if (e.getItem().getType() == Material.AMETHYST_SHARD && e.getItem().getItemMeta().getLore() == null
                && e.getItem().getItemMeta().getDisplayName().equals("бог, дай мне че-нить")) {
                if (!e.getPlayer().getServer().getOnlinePlayers().isEmpty()) {
                    if (e.getPlayer().getCooldown(Material.AMETHYST_SHARD) <= 1) {
                        int max_list = Bukkit.getOnlinePlayers().size();
                        ArrayList<Player> player_list = new ArrayList<>(Bukkit.getOnlinePlayers());
                        Random rand = new Random();
                        int chosen = rand.nextInt(max_list);
                        new airdropMain(player_list.get(chosen), 1, 1);
                        e.getPlayer().setCooldown(Material.AMETHYST_SHARD, 300);
                        e.getPlayer().getInventory().removeItem(new ItemStack(e.getItem().getType(), 1));
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

                    HashMap<String, WeaponAbility> abilities = WeaponManager.getWeaponAbilities();
                    abilities.get("fire").setChance(config.getDouble("ability_chance.fire"));
                    abilities.get("lightStrike").setChance(config.getDouble("ability_chance.lightStrike"));
                    abilities.get("desiccation").setChance(config.getDouble("ability_chance.desiccation"));
                    abilities.get("freeze").setChance(config.getDouble("ability_chance.freeze"));
                    abilities.get("bloodlust").setChance(config.getDouble("ability_chance.bloodlust"));

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
                case "airdrop":
                    if (args.length < 2) {
                        sender.sendMessage("Используйте /gribadmin airdrop <summon>");
                        return true;
                    }
                    new summon(sender, args);
                    break;
                default:
                    sender.sendMessage("Неизвестная подкоманда.");
                    break;
            }
            return true;
        }
        return false;
    }

    //подсказки епта, не знаю заработает ли с /gribadmin weapon set
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("gribadmin")) {
            if (args.length == 1) {
                // Подсказки для первого аргумента
                completions.add("reload");
                completions.add("get_config");
                completions.add("weapon");
                completions.add("airdrop");
            } else if (args.length == 2 && args[0].equalsIgnoreCase("weapon")) {
                // Подсказки для второго аргумента
                completions.add("get");
                completions.add("set");
                completions.add("reassemble");
                completions.add("reset");
            } else if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
                // Подсказки для третьего аргумента
                completions.add("rarity=");
                completions.add("ability=");
            } else if (args.length == 4 && args[0].equalsIgnoreCase("set")) {
                // Подсказки для значений rarity
                if (args[2].startsWith("rarity=")) {
                    List<String> rarities = GribMine.getMineConfig().getStringList("rarity_list");
                    for (String rarity : rarities) {
                        completions.add("rarity=" + rarity);
                    }
                }
                // Подсказки для значений ability
                if (args[2].startsWith("ability=")) {
                    HashMap<String, WeaponAbility> abilities = WeaponManager.getWeaponAbilities();
                    for (String abilityName : abilities.keySet()) {
                        completions.add("ability=" + abilityName);
                    }
                }
                //TODO: починить эту хуету, так как /gribmine airdrop summon не показывает еще atme
            } else if (args.length == 2 && args[0].equalsIgnoreCase("airdrop")) {
                completions.add("summon");
            } else if (args.length == 3 && args[0].equalsIgnoreCase("summon")) {
                completions.add("atme");
            }
        }

        // Возвращаем список подсказок
        return completions;
    }

    public static FileConfiguration getMineConfig() {
        return config;
    }
}