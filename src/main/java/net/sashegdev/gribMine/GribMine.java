package net.sashegdev.gribMine;

import net.sashegdev.gribMine.airdrop.airdropMain;
import net.sashegdev.gribMine.airdrop.commands.summon;
import net.sashegdev.gribMine.commands.handleWeaponCommand;
import net.sashegdev.gribMine.weapon.WeaponAbility;
import net.sashegdev.gribMine.weapon.WeaponManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
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

        // Удаляем все ArmorStand с именем аирдропа при запуске плагина
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof ArmorStand) {
                    ArmorStand armorStand = (ArmorStand) entity;
                    if (armorStand.getCustomName() != null && armorStand.getCustomName().equals(ChatColor.RED + "Воздушное Снабжение")) {
                        armorStand.remove(); // Удаляем ArmorStand
                    }
                }
            }
        }

        List<String> rarityList;
        HashMap<String, Double> damageModifiers;
        {
            // Initialize the weaponManager in a static block
            rarityList = getMineConfig().getStringList("rarity_list");
            damageModifiers = new HashMap<>();

            // Retrieve damage modifiers from the configuration
            Map<String, Object> damageModConfig = Objects.requireNonNull(getMineConfig().getConfigurationSection("damage_mod")).getValues(false);
            for (Map.Entry<String, Object> entry : damageModConfig.entrySet()) {
                String rarity = entry.getKey();
                double modifier = Double.parseDouble(entry.getValue().toString());
                damageModifiers.put(rarity, modifier);
            }
        }

        weaponManager = new WeaponManager(rarityList, damageModifiers); // Pass the initialized HashMap
        getServer().getPluginManager().registerEvents(new LootListener(), this);

        // Регистрируем слушатели
        getServer().getPluginManager().registerEvents(this, this);

        logger.info("GribMine Plugin initialized ;)");
        //logger.info("Версия плагина: " + getDescription().getVersion());
        logger.info("Версия плагина: 1.1.3 - РЕЛИЗ!!!!!!!!");

        Objects.requireNonNull(getCommand("gribadmin")).setExecutor(this);
    }

    @EventHandler
    public void PlayerAttackEpta(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        switch (damager) {
            case Player player when !(
                    ((Player) damager).getInventory().getItemInMainHand().equals(Material.CROSSBOW) ||
                            ((Player) damager).getInventory().getItemInMainHand().equals(Material.BOW)
            ) -> {
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
            }
            case Arrow arrow -> {
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
            case Trident arrow -> {
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
            default -> {
            }
        }
    }

    public static void giveAirSupplyItem(Player player) {
        // Create the ItemStack for the Amethyst Shard
        ItemStack item = new ItemStack(Material.AMETHYST_SHARD, 1);
        ItemMeta itemMeta = item.getItemMeta();

        if (itemMeta != null) {
            // Set the display name with color codes
            itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.LIGHT_PURPLE + "Воздушное снабжение");

            // Set the lore
            List<String> lore = new ArrayList<>();
            lore.add("Фиолетовая дымовая граната");
            lore.add("Которая вызывает дроп в небольшом радиусе вокруг себя.");
            lore.add("Данный дроп будет виден всем игрокам на сервере, так что будьте готовы к битве!");
            itemMeta.setLore(lore);

            // Apply the meta to the item
            item.setItemMeta(itemMeta);
        }

        // Give the item to the player
        player.getInventory().addItem(item);
    }

    @EventHandler
    public void ClickHandler(PlayerInteractEvent e) {
        if (e.getItem() == null) return;
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
            ItemStack item = e.getItem();

            // Проверяем, что предмет — это Amethyst Shard
            if (item.getType() == Material.AMETHYST_SHARD && item.getItemMeta() != null) {
                ItemMeta itemMeta = item.getItemMeta();

                // Проверяем название предмета (игнорируя цветовые коды)
                String displayName = ChatColor.stripColor(itemMeta.getDisplayName());
                if (displayName.equals("Воздушное снабжение")) {
                    // Проверяем лор предмета
                    List<String> lore = itemMeta.getLore();
                    List<String> expectedLore = new ArrayList<>();
                    expectedLore.add("Фиолетовая дымовая граната");
                    expectedLore.add("Которая вызывает дроп в небольшом радиусе вокруг себя.");
                    expectedLore.add("Данный дроп будет виден всем игрокам на сервере, так что будьте готовы к битве!");

                    // Убеждаемся, что лор не null и соответствует ожидаемому
                    if (lore != null && lore.equals(expectedLore)) {
                        // Проверяем, включены ли аирдропы в конфиге
                        if (!getMineConfig().getBoolean("airdrop_enabled", true)) {
                            // Если аирдропы отключены, выводим сообщение игроку
                            e.getPlayer().sendTitle(
                                    ChatColor.LIGHT_PURPLE + "Хм...", // Первая строка
                                    ChatColor.DARK_PURPLE + "Вы не знаете, как это использовать.", // Вторая строка
                                    10, 70, 20 // Время появления, время отображения, время исчезновения
                            );
                            return; // Прекращаем выполнение метода
                        }

                        // Проверяем, что на сервере есть игроки и кулдаун истек
                        if (!e.getPlayer().getServer().getOnlinePlayers().isEmpty()) {
                            if (e.getPlayer().getCooldown(Material.AMETHYST_SHARD) <= 1) {
                                // Создаем аирдроп
                                new airdropMain(e.getPlayer());
                                e.getPlayer().setCooldown(Material.AMETHYST_SHARD, 20 * 60 * 10);

                                // Убираем один Amethyst Shard из инвентаря игрока
                                item.setAmount(item.getAmount() - 1); // Уменьшаем количество на 1
                                if (item.getAmount() <= 0) {
                                    e.getPlayer().getInventory().remove(item); // Удаляем предмет, если количество равно 0
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("gribadmin")) {
            if (args.length == 0) {
                sender.sendMessage("Используйте /gribadmin <reload|get_config|weapon|airdrop>");
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "reload":
                    reloadConfig();
                    sender.sendMessage("Конфигурация перезагружена.");
                    sender.sendMessage(ChatColor.RED+"ЛУЧШЕ ИСПОЛЬЗУЙ /reload confirm ТАК КАК ЭТА ФУНКЦИЯ НЕ ВСЕГДА РАБОТАЕТ");

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
    public List<String> onTabComplete(@NotNull CommandSender sender, Command command, @NotNull String label, @NotNull String[] args) {
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
            } else if (args.length == 3 && args[0].equalsIgnoreCase("weapon") && args[1].equalsIgnoreCase("set")) {
                // Подсказки для rarity
                List<String> rarities = GribMine.getMineConfig().getStringList("rarity_list");
                completions.addAll(rarities);
            } else if (args.length == 4 && args[0].equalsIgnoreCase("weapon") && args[1].equalsIgnoreCase("set")) {
                // Подсказки для ability
                HashMap<String, WeaponAbility> abilities = WeaponManager.getWeaponAbilities();
                for (String abilityName : abilities.keySet()) {
                    // Фильтруем русские названия, оставляем только теги
                    if (!abilityName.equals(abilities.get(abilityName).getRussianName())) {
                        completions.add(abilityName);
                    }
                }
            } else if (args.length == 2 && args[0].equalsIgnoreCase("airdrop")) {
                // Подсказки для второго аргумента команды airdrop
                completions.add("summon");
                completions.add("give");
            } else if (args.length == 3 && args[0].equalsIgnoreCase("airdrop")) {
                // Подсказки для третьего аргумента команды airdrop
                if (args[1].equalsIgnoreCase("summon")) {
                    completions.add("atme");
                } else if (args[1].equalsIgnoreCase("give")) {
                    // Подсказки для списка игроков
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        completions.add(player.getName());
                    }
                }
            } else if (args.length == 4 && args[0].equalsIgnoreCase("airdrop") && args[1].equalsIgnoreCase("give")) {
                // Подсказка для числа (количество аирдропов)
                completions.add("[<count>]"); // Подсказка для ввода числа
            }
        }

        // Фильтруем подсказки по уже введенному тексту
        if (args.length > 0) {
            String lastArg = args[args.length - 1].toLowerCase();
            completions.removeIf(s -> !s.toLowerCase().startsWith(lastArg));
        }

        return completions;
    }

    public static FileConfiguration getMineConfig() {
        return config;
    }
}