package net.sashegdev.gribMine.weapon;

import net.sashegdev.gribMine.weapon.ability.*;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class WeaponManager implements Listener {
    private final List<String> rarityList;
    private final HashMap<UUID, List<String>> playerRarityMap;
    private final HashMap<String, Double> damageModifiers;
    private static final HashMap<String, List<WeaponAbility>> weaponAbilitiesForRarity = new HashMap<>(); // Хранит способности для каждого оружия
    private static final HashMap<String, WeaponAbility> weaponAbilities = new HashMap<>();
    public WeaponManager(List<String> rarityList, HashMap<String, Double> damageModifiers) {
        this.rarityList = rarityList;
        this.playerRarityMap = new HashMap<>();
        this.damageModifiers = damageModifiers;
        weaponAbilitiesForRarity.put("common", new ArrayList<>());
        weaponAbilitiesForRarity.put("uncommon", new ArrayList<>());
        weaponAbilitiesForRarity.put("rare", new ArrayList<>());
        weaponAbilitiesForRarity.put("epic", new ArrayList<>());
        weaponAbilitiesForRarity.put("legendary", new ArrayList<>());

        addAbility(new FireAbility().getName(), "rare", new FireAbility());
    }

    // Метод для добавления способностей к оружию
    public void addAbility(String weaponName, String rarity, WeaponAbility ability) {
        weaponAbilities.put(weaponName, ability);
        weaponAbilities.put(ability.getRussianName(), ability);
        weaponAbilitiesForRarity.get(rarity).add(ability);
    }

    public static void addWeaponAbility(ItemStack item, String abilityName) {
        try {
            List<String> lore = item.getItemMeta().getLore();
            ItemMeta meta = item.getItemMeta();
            lore.set(1, "Способность: " + weaponAbilities.get(abilityName).getRussianName());
            meta.setLore(lore);
            item.setItemMeta(meta);
        } catch (NullPointerException ex) {
        }
    }

    @EventHandler
    public void PickUpEvent(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        Location loc = player.getLocation();

        // Получаем ItemMeta предмета
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            List<String> lore = itemMeta.getLore();
            String rarity = null;
            String passiveAbility = null;

            // Проверяем наличие тега рарности и способности в лоре
            if (lore != null) {
                for (String line : lore) {
                    if (line.startsWith("Редкость: ")) {
                        rarity = line.substring(10);
                    } else if (line.startsWith("Способность: ")) {
                        passiveAbility = line.substring(13);
                    }
                }
            }

            // Если рарности нет, присваиваем минимальную
            if (rarity == null) {
                rarity = rarityList.get(0); // Минимальная рарность
            } else if (!rarityList.contains(rarity)) {
                // Если рарность не соответствует ни одной из известных, присваиваем минимальную
                rarity = rarityList.get(0); // Минимальная рарность
            }

            // Обновляем лор только если он не содержит информацию о способности
            if (passiveAbility == null) {
                itemMeta.setLore(createLoreWithRarity(rarity, "none", 1.0)); // Устанавливаем рарность и пассивку
            } else {
                // Если рарность известна, получаем модификатор урона
                double damageModifier = getDamageModifier(rarity);
                itemMeta.setLore(createLoreWithRarity(rarity, passiveAbility, damageModifier)); // Обновляем лор с модификатором
            }

            item.setItemMeta(itemMeta);

            // Устанавливаем атрибут урона
            if (!itemMeta.hasAttributeModifiers()) {
                double damageModifier = getDamageModifier(rarity);
                itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier("generic.attack_damage", damageModifier, AttributeModifier.Operation.ADD_NUMBER));
                item.setItemMeta(itemMeta);
            }

            // Обработка рарности
            if (rarityList.contains(rarity)) {
                // Проверяем, поднимал ли игрок эту рарность ранее
                if (!playerRarityMap.containsKey(player.getUniqueId())) {
                    playerRarityMap.put(player.getUniqueId(), new ArrayList<>());
                }

                List<String> playerRarities = playerRarityMap.get(player.getUniqueId());
                if (!playerRarities.contains(rarity)) {
                    // Если игрок поднимает рарность в первый раз, создаем частицы
                    for (int i = 0; i < 30; i++) { // Количество частиц
                        double angle = Math.random() * 2 * Math.PI; // Случайный угол
                        double x = Math.cos(angle) * 0.5; // Смещение по X
                        double z = Math.sin(angle) * 0.5; // Смещение по Z
                        player.getWorld().spawnParticle(Particle.END_ROD, loc.getX() + x, loc.getY() + 1, loc.getZ() + z, 1, 0, 0, 0, 0.13);
                    }
                    playerRarities.add(rarity); // Добавляем рарность в список
                }
            }
        }
    }

    private List<String> createLoreWithRarity(String rarity, String passiveAbility, double damageModifier) {
        List<String> lore = new ArrayList<>();
        lore.add("Редкость: " + rarity);
        lore.add("Способность: " + passiveAbility);
        lore.add("Модификатор урона: " + damageModifier);
        return lore;
    }

    public double getDamageModifier(String rarity) {
        return damageModifiers.getOrDefault(rarity, 1.0); // Возвращаем множитель урона, если рарность не найдена, возвращаем 1.0
    }

    public List<String> getRarityList() {
        return rarityList;
    }

    public static List<WeaponAbility> getWeaponAbilitiesForRarity(String rarity) {
        return weaponAbilitiesForRarity.get(rarity);
    }

    public static HashMap<String, WeaponAbility> getWeaponAbilities() {
        return weaponAbilities;
    }

}