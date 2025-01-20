package net.sashegdev.gribMine.weapon;

import net.md_5.bungee.api.ChatColor;
import net.sashegdev.gribMine.GribMine;
import net.sashegdev.gribMine.weapon.ability.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class WeaponManager implements Listener {
    private static List<String> rarityList = null;
    private static HashMap<UUID, List<String>> playerRarityMap = new HashMap<>();
    private static HashMap<String, Double> damageModifiers = new HashMap<>();
    private static final HashMap<String, List<WeaponAbility>> weaponAbilitiesForRarity = new HashMap<>(); // Хранит способности для каждого оружия
    private static final HashMap<String, WeaponAbility> weaponAbilities = new HashMap<>();

    // Список допустимых типов оружия (ТЕПЕРЬ В КОНФИГЕ)
    private static final List<String> allowedWeaponTypes = GribMine.getMineConfig().getStringList("allowed_weapon_types");

    private static final Map<String, ChatColor> rarityColors = new HashMap<>();

    static {
        rarityColors.put("common", ChatColor.GRAY);
        rarityColors.put("uncommon", ChatColor.GREEN);
        rarityColors.put("rare", ChatColor.BLUE);
        rarityColors.put("epic", ChatColor.LIGHT_PURPLE);
        rarityColors.put("legendary", ChatColor.GOLD);
    }

    public WeaponManager(List<String> rarityList, HashMap<String, Double> damageModifiers) {
        WeaponManager.rarityList = rarityList;
        // Инициализация damageModifiers, если он равен null
        WeaponManager.damageModifiers = damageModifiers != null ? damageModifiers : new HashMap<>();
        playerRarityMap = new HashMap<>();
        weaponAbilitiesForRarity.put("common", new ArrayList<>());
        weaponAbilitiesForRarity.put("uncommon", new ArrayList<>());
        weaponAbilitiesForRarity.put("rare", new ArrayList<>());
        weaponAbilitiesForRarity.put("epic", new ArrayList<>());
        weaponAbilitiesForRarity.put("legendary", new ArrayList<>());

        /*TODO:
           добавить возможность через конфиг включать/выключать возможность выпадения способки
           так же сделать так что бы шанс активации, рарность можно было настроить через конфиг
         */

        addAbility(new FireAbility().getName(), "rare", new FireAbility());
        addAbility(new LightStrike().getName(), "legendary", new LightStrike());
        addAbility(new DesiccationAbility().getName(), "uncommon", new DesiccationAbility());
        addAbility(new FreezeAbility().getName(), "epic", new FreezeAbility());
        addAbility(new BloodLust().getName(), "uncommon", new BloodLust());
        addAbility(new BladeVortex().getName(), "rare", new BladeVortex());
        addAbility(new SirenSong().getName(), "epic", new SirenSong());
        addAbility(new Sacrifice().getName(), "legendary", new Sacrifice());
        addAbility(new ShadowCloak().getName(), "uncommon", new ShadowCloak());
        addAbility(new FlamingDance().getName(), "common", new FlamingDance());
        addAbility(new Suffocation().getName(), "rare", new Suffocation());

        // Запускаем ChangeWeapon каждый тик
        new BukkitRunnable() {
            @Override
            public void run() {
                ChangeWeapon();
            }
        }.runTaskTimer(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("GribMine")), 0L, 1L); // 0L - начальная задержка, 1L - период (1 тик)
    }

    // Метод для добавления способностей к оружию
    public void addAbility(String weaponName, String rarity, WeaponAbility ability) {
        weaponAbilities.put(weaponName, ability);
        weaponAbilities.put(ability.getRussianName(), ability);
        weaponAbilitiesForRarity.get(rarity).add(ability);
    }

    public static String getNameByRussian(String name) {
        return weaponAbilities.get(name).getName();
    }

    private static final HashMap<UUID, String> lastRarityCache = new HashMap<>();
    private static final HashMap<UUID, Double> lastDamageModifierCache = new HashMap<>();

    public static void ChangeWeapon() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    ItemStack item = player.getInventory().getItemInMainHand();
                    if (item == null || item.getType().isAir()) continue; // Пропускаем, если в руке ничего нет

                    ItemMeta itemMeta = item.getItemMeta();
                    if (itemMeta == null) continue;

                    // Получаем текущую редкость
                    String rarity = getRarityFromLore(itemMeta.getLore());
                    if (rarity == null || !rarityList.contains(rarity)) {
                        rarity = rarityList.get(0); // Минимальная редкость
                    }

                    // Проверяем, нужно ли обновить предмет
                    UUID playerId = player.getUniqueId();
                    String lastRarity = lastRarityCache.get(playerId);

                    // Если редкость изменилась или предмет не имеет лора
                    if (!rarity.equals(lastRarity) || !itemMeta.hasLore()) {
                        // Обновляем кэш
                        lastRarityCache.put(playerId, rarity);

                        // Обновляем название и лор
                        String displayName = itemMeta.getDisplayName();
                        if (displayName == null || displayName.isEmpty()) {
                            displayName = item.getType().toString().toLowerCase().replace("_", " ");
                            displayName = capitalize(displayName.trim());
                        }
                        ChatColor color = rarityColors.getOrDefault(rarity, ChatColor.GRAY);
                        itemMeta.setDisplayName(color + ChatColor.stripColor(displayName).trim());
                        itemMeta.setLore(createLoreWithRarity(rarity, "none")); // Упрощено, без проверки способностей

                        // Применяем изменения к предмету
                        item.setItemMeta(itemMeta);
                    }
                }
            }
        }.runTaskTimer(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("GribMine")), 0, 1); // Оставляем проверку каждый тик
    }

    private static String getRarityFromLore(List<String> lore) {
        if (lore != null) {
            for (String line : lore) {
                if (line.startsWith("Редкость: ")) {
                    return ChatColor.stripColor(line).substring(10); // Убираем цветовые коды и возвращаем редкость
                }
            }
        }
        return null; // Если редкость не найдена
    }

    // Вспомогательный метод для получения способности из лора
    private static String getPassiveAbilityFromLore(List<String> lore) {
        if (lore != null) {
            for (String line : lore) {
                if (line.startsWith("Способность: ")) {
                    return line.substring(13); // Возвращаем способность
                }
            }
        }
        return "none"; // Если способность не найдена
    }

    //TODO: пассивки
    private static List<String> createLoreWithRarity(String rarity, String passiveAbility) {
        List<String> lore = new ArrayList<>();
        lore.add("Редкость: " + rarity);
        lore.add("Способность: " + passiveAbility);
        return lore;
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static double getDamageModifier(String rarity) {
        if (damageModifiers == null) {
            return 1.0; // Возвращаем значение по умолчанию, если damageModifiers не инициализирован
        }
        return damageModifiers.getOrDefault(rarity, 1.0);
    }

    public static List<String> getRarityList() {
        return rarityList;
    }

    public static List<WeaponAbility> getWeaponAbilitiesForRarity(String rarity) {
        return weaponAbilitiesForRarity.get(rarity);
    }

    public static HashMap<String, WeaponAbility> getWeaponAbilities() {
        return weaponAbilities;
    }

    public static List<String> getAllowedWeaponTypes() { return allowedWeaponTypes; }
}