package net.sashegdev.gribMine.weapon;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
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
    private final HashMap<String, List<WeaponAbility>> weaponAbilitiesForRarity; // Хранит способности для каждого оружия
    private final HashMap<String, WeaponAbility> weaponAbilities;
    public WeaponManager(List<String> rarityList, HashMap<String, Double> damageModifiers) {
        this.rarityList = rarityList;
        this.playerRarityMap = new HashMap<>();
        this.damageModifiers = damageModifiers;
        this.weaponAbilitiesForRarity = new HashMap<>(); // Инициализация карты способностей
        this.weaponAbilities = new HashMap<>();
    }

    // Метод для добавления способностей к оружию
    public void addWeaponAbility(String weaponName, WeaponAbility ability) {
        weaponAbilitiesForRarity.computeIfAbsent(weaponName, k -> new ArrayList<>()).add(ability);
        weaponAbilities.put(weaponName, ability);
    }

    @EventHandler
    public void PickUpEvent(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem().getItemStack();
        Location loc = player.getLocation();

        // Получаем ItemMeta предмета
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            List<String> lore = itemMeta.getLore();
            String rarity = null;

            // Проверяем наличие тега рарности в лоре
            if (lore != null) {
                for (String line : lore) {
                    if (line.startsWith("Редкость: ")) {
                        rarity = line.substring(10);
                        break;
                    }
                }
            }

            // Если рарности нет, присваиваем минимальную
            if (rarity == null) {
                rarity = rarityList.get(0); // Минимальная рарность
                itemMeta.setLore(createLoreWithRarity(rarity, "none", 1.0)); // Устанавливаем рарность и пассивку
                item.setItemMeta(itemMeta);
            } else if (!rarityList.contains(rarity)) {
                // Если рарность не соответствует ни одной из известных, присваиваем минимальную
                rarity = rarityList.get(0); // Минимальная рарность
                itemMeta.setLore(createLoreWithRarity(rarity, "none", 1.0)); // Устанавливаем рарность и пассивку
                item.setItemMeta(itemMeta);
            } else {
                // Если рарность известна, получаем модификатор урона
                double damageModifier = getDamageModifier(rarity);
                itemMeta.setLore(createLoreWithRarity(rarity, "none", damageModifier)); // Обновляем лор с модификатором
                item.setItemMeta(itemMeta);

                // Устанавливаем атрибут урона
                itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier("generic.attack_damage", damageModifier, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
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
                        player.getWorld().spawnParticle(Particle.END_ROD, loc.getX() + x, loc.getY() + 1, loc.getZ() + z, 1);
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

    public List<WeaponAbility> getWeaponAbilitiesForRarity(String rarity) {
        return weaponAbilitiesForRarity.get(rarity);
    }

    public HashMap<String, WeaponAbility> getWeaponAbilities() {
        return weaponAbilities;
    }
}