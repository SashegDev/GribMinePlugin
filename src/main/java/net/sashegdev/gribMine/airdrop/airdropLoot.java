package net.sashegdev.gribMine.airdrop;

import net.sashegdev.gribMine.weapon.WeaponManager;
import net.sashegdev.gribMine.weapon.WeaponAbility;
import net.sashegdev.gribMine.GribMine;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class airdropLoot {
    private static List<String> lootTableGet = GribMine.getMineConfig().getStringList("airdrop_items");
    private static final List<String> lootTable = new ArrayList<>();

    // Инициализация lootTable
    static {
        for (String item : lootTableGet) {
            lootTable.add(item.toUpperCase()); // Преобразуем в верхний регистр и добавляем в lootTable
        }
    }
    private static final Random random = new Random();
    private static final WeaponManager weaponManager = new WeaponManager(GribMine.getMineConfig().getStringList("rarity_list"), (HashMap<String, Double>) GribMine.getMineConfig().getList("damage_mod"));

    public static void addLoot(Block block){
        // Проверяем, что блок является бочкой
        if (block.getState() instanceof Barrel barrel) {
            // Добавляем оружие в инвентарь бочки
            for (int i = 0; i<GribMine.getMineConfig().getInt("AirDropWeaponGenerateNumber"); i++) {
                barrel.getInventory().addItem(generateRandomWeapon());
            }

            // Добавляем рандомный дроп в инвентарь бочки
            for (int rot = random.nextInt(1, GribMine.getMineConfig().getInt("AirDropMaxRotations")+1); rot > 0; rot--) {
                // Выбираем случайный элемент из lootTable
                String randomItem = lootTable.get(random.nextInt(lootTable.size()));
                Material material = Material.matchMaterial(randomItem);

                // Проверяем, что материал не равен null
                assert material != null;

                // Добавляем новый ItemStack в инвентарь бочки
                barrel.getInventory().addItem(new ItemStack(material, random.nextInt(1, GribMine.getMineConfig().getInt("AirDropMaxItemInOneRot")+1)));
            }

            // Обновляем состояние бочки
            barrel.update();
        }
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

    @NotNull
    private static ItemStack getWeapon(Material weaponMaterial, String randomRarity, WeaponAbility randomAbility) {
        ItemStack weapon = new ItemStack(weaponMaterial); // Используем выбранный тип оружия
        ItemMeta meta = weapon.getItemMeta();
        if (meta != null) {
            List<String> lore = new ArrayList<>();
            lore.add("Редкость: " + randomRarity);
            if (randomAbility != null) {
                lore.add("Способность: " + randomAbility.getName());
            } else {
                lore.add("Способность: none");
            }
            meta.setLore(lore);
            weapon.setItemMeta(meta);
        }
        return weapon;
    }
}