package net.sashegdev.gribMine.airdrop;

import net.sashegdev.gribMine.DebugLogger;
import net.sashegdev.gribMine.weapon.WeaponManager;
import net.sashegdev.gribMine.weapon.WeaponAbility;
import net.sashegdev.gribMine.GribMine;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class airdropLoot {
    private static final Map<String, Map<String, Object>> lootTable = new HashMap<>();
    private static final Random random = new Random();
    private static final WeaponManager weaponManager = new WeaponManager(GribMine.getMineConfig().getStringList("rarity_list"), (HashMap<String, Double>) GribMine.getMineConfig().getList("damage_mod"));

    // Инициализация lootTable
    static {
        ConfigurationSection lootConfigSection = GribMine.getMineConfig().getConfigurationSection("airdrop_items");
        if (lootConfigSection != null) {
            for (String itemName : lootConfigSection.getKeys(false)) {
                ConfigurationSection itemDataSection = lootConfigSection.getConfigurationSection(itemName);
                if (itemDataSection != null) {
                    double chance = itemDataSection.getDouble("chance", 0.0);
                    if (chance <= 0) {
                        DebugLogger.log("Item " + itemName + " has non-positive chance: " + chance, DebugLogger.LogLevel.WARNING);
                        continue;
                    }
                    int amount = itemDataSection.getInt("amount", 1);
                    Map<String, Object> itemData = new HashMap<>();
                    itemData.put("chance", chance);
                    itemData.put("amount", amount);
                    lootTable.put(itemName.toUpperCase(), itemData);
                    DebugLogger.log("Loaded item: " + itemName + " with chance: " + chance + " and amount: " + amount, DebugLogger.LogLevel.INFO);
                } else {
                    DebugLogger.log("Invalid item data for " + itemName, DebugLogger.LogLevel.ERROR);
                }
            }
        } else {
            DebugLogger.log("Airdrop items configuration section not found.", DebugLogger.LogLevel.ERROR);
        }
    }

    public static void addLoot(Block block) {
        if (block.getState() instanceof Barrel barrel) {
            DebugLogger.log("Adding loot to barrel at: " + block.getLocation(), DebugLogger.LogLevel.INFO);

            // Add weapons
            for (int i = 0; i < GribMine.getMineConfig().getInt("AirDropWeaponGenerateNumber"); i++) {
                ItemStack weapon = generateRandomWeapon();
                if (weapon != null) {
                    barrel.getInventory().addItem(weapon);
                    DebugLogger.log("Added weapon: " + weapon.getType(), DebugLogger.LogLevel.INFO);
                } else {
                    DebugLogger.log("Failed to generate a weapon.", DebugLogger.LogLevel.ERROR);
                }
            }

            // Add random items
            for (int rot = random.nextInt(1, GribMine.getMineConfig().getInt("AirDropMaxRotations") + 1); rot > 0; rot--) {
                String randomItem = getRandomItemWithChance();
                if (randomItem != null) {
                    Material material = Material.matchMaterial(randomItem);
                    if (material != null) {
                        int amount = getItemAmount(randomItem);
                        ItemStack itemStack = new ItemStack(material, amount);
                        barrel.getInventory().addItem(itemStack);
                        DebugLogger.log("Added item: " + material + " x" + amount, DebugLogger.LogLevel.INFO);
                    } else {
                        DebugLogger.log("Invalid material: " + randomItem, DebugLogger.LogLevel.ERROR);
                    }
                } else {
                    DebugLogger.log("Failed to select a random item.", DebugLogger.LogLevel.ERROR);
                }
            }

            // Debug: Print barrel inventory contents
            DebugLogger.log("Barrel inventory contents:", DebugLogger.LogLevel.INFO);
            for (ItemStack item : barrel.getInventory().getContents()) {
                if (item != null) {
                    DebugLogger.log(item.getType() + " x" + item.getAmount(), DebugLogger.LogLevel.INFO);
                }
            }
        } else {
            DebugLogger.log("Block is not a barrel.", DebugLogger.LogLevel.ERROR);
        }
    }

    private static String getRandomItemWithChance() {
        double totalWeight = lootTable.values().stream()
                .mapToDouble(item -> (double) item.get("chance"))
                .sum();

        if (totalWeight <= 0) {
            DebugLogger.log("Total weight is zero or negative. Cannot select a random item.", DebugLogger.LogLevel.ERROR);
            return null; // or handle it appropriately
        }

        DebugLogger.log("Total weight: " + totalWeight, DebugLogger.LogLevel.INFO);

        double randomValue = ThreadLocalRandom.current().nextDouble(totalWeight);
        double cumulativeWeight = 0.0;

        for (Map.Entry<String, Map<String, Object>> entry : lootTable.entrySet()) {
            cumulativeWeight += (double) entry.getValue().get("chance");
            if (randomValue < cumulativeWeight) {
                return entry.getKey();
            }
        }

        return null;
    }

    private static int getItemAmount(String itemName) {
        Map<String, Object> itemData = lootTable.get(itemName);
        if (itemData != null) {
            return (int) itemData.get("amount");
        }
        return 1; // По умолчанию возвращаем 1, если количество не указано
    }

    private static ItemStack generateRandomWeapon() {
        // Получаем случайную рарность
        List<String> rarityList = weaponManager.getRarityList();
        String randomRarity = rarityList.get(random.nextInt(rarityList.size()));

        // Получаем случайную способность для этой рарности
        List<WeaponAbility> abilities = WeaponManager.getWeaponAbilitiesForRarity(randomRarity);
        WeaponAbility randomAbility = abilities.isEmpty() ? null : abilities.get(random.nextInt(abilities.size()));

        // Получаем список допустимых типов оружия из конфигурации
        List<String> allowedWeaponTypes = WeaponManager.getAllowedWeaponTypes();

        // Случайно выбираем тип оружия из списка
        String randomWeaponTypeString = allowedWeaponTypes.get(random.nextInt(allowedWeaponTypes.size()));
        Material weaponMaterial = Material.matchMaterial(randomWeaponTypeString); // Преобразуем строку в Material

        // Создаем ItemStack для оружия
        assert weaponMaterial != null;
        return getWeapon(weaponMaterial, randomRarity, randomAbility);
    }

    private static ItemStack getWeapon(Material weaponMaterial, String randomRarity, WeaponAbility randomAbility) {
        ItemStack weapon = new ItemStack(weaponMaterial); // Используем выбранный тип оружия
        ItemMeta meta = weapon.getItemMeta();
        if (meta != null) {
            List<String> lore = new ArrayList<>();

            // Добавляем редкость с цветом
            lore.add("Редкость: " + randomRarity);

            // Добавляем способность с цветом из конфига
            if (randomAbility != null) {
                lore.add("Способность: " + randomAbility.getRussianName());
            } else {
                lore.add("Способность: none");
            }

            meta.setLore(lore);
            weapon.setItemMeta(meta);
        }
        return weapon;
    }
}