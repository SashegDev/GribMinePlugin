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
    private final List<String> rarityList; // Список рарностей из конфигурации
    private final HashMap<UUID, List<String>> playerRarityMap; // Хранит рарности, которые игрок уже поднимал
    private final HashMap<String, Double> damageModifiers; // Хранит множители урона для каждой рарности

    public WeaponManager(List<String> rarityList, HashMap<String, Double> damageModifiers) {
        this.rarityList = rarityList;
        this.playerRarityMap = new HashMap<>();
        this.damageModifiers = damageModifiers; // Загружаем множители из конфигурации
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

            // Проверяем наличие тега рарности в лоре
            String rarity = null;
            if (lore != null) {
                for (String line : lore) {
                    if (line.startsWith("Редкость: ")) { // Предполагаем, что рарность записана в лоре
                        rarity = line.substring(10); // Извлекаем рарность
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

                // Создаем объект Weapon и присваиваем его игроку или храним в каком-то месте
                Weapon weapon = new Weapon(rarity, damageModifier);
                // Здесь вы можете сохранить weapon в каком-то месте, например, в инвентаре игрока или в другой структуре данных
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
        lore.add("Пассивная способность: " + passiveAbility);
        lore.add("Модификатор урона: " + damageModifier);
        return lore;
    }

    public double getDamageModifier(String rarity) {
        return damageModifiers.getOrDefault(rarity, 1.0); // Возвращаем множитель урона, если рарность не найдена, возвращаем 1.0
    }
}