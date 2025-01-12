package net.sashegdev.gribMine.airdrop;

import net.sashegdev.gribMine.weapon.WeaponManager;
import net.sashegdev.gribMine.weapon.WeaponAbility;
import net.sashegdev.gribMine.GribMine;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
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
        // Получаем конфигурацию для аирдропов
        Map<String, Object> lootConfig = GribMine.getMineConfig().getConfigurationSection("airdrop_items").getValues(false);
        for (Map.Entry<String, Object> entry : lootConfig.entrySet()) {
            String itemName = entry.getKey();
            Map<String, Object> itemData = (Map<String, Object>) entry.getValue();
            lootTable.put(itemName.toUpperCase(), itemData); // Преобразуем в верхний регистр и добавляем в lootTable
        }
    }

    public static void addLoot(Block block) {
        // Проверяем, что блок является бочкой
        if (block.getState() instanceof Barrel barrel) {
            // Добавляем оружие в инвентарь бочки
            for (int i = 0; i < GribMine.getMineConfig().getInt("AirDropWeaponGenerateNumber"); i++) {
                barrel.getInventory().addItem(generateRandomWeapon());
            }

            // Добавляем рандомный дроп в инвентарь бочки
            for (int rot = random.nextInt(1, GribMine.getMineConfig().getInt("AirDropMaxRotations") + 1); rot > 0; rot--) {
                // Выбираем случайный предмет с учетом шансов
                String randomItem = getRandomItemWithChance();
                if (randomItem != null) {
                    Material material = Material.matchMaterial(randomItem);
                    if (material != null) {
                        // Получаем количество предметов
                        int amount = getItemAmount(randomItem);
                        barrel.getInventory().addItem(new ItemStack(material, amount));
                    }
                }
            }

            // Обновляем состояние бочки
            barrel.update();
        }
    }

    private static String getRandomItemWithChance() {
        double totalWeight = lootTable.values().stream()
                .mapToDouble(item -> (double) item.get("chance"))
                .sum();

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