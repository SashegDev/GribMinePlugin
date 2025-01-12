package net.sashegdev.gribMine.weapon;

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

    public WeaponManager(List<String> rarityList, HashMap<String, Double> damageModifiers) {
        WeaponManager.rarityList = rarityList;
        WeaponManager.damageModifiers = damageModifiers; // This should now be properly initialized
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
        //addAbility(new FlamingDance().getName(), "common", new FlamingDance());
        addAbility(new Suffocation().getName(), "rare", new Suffocation());
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

    //TODO: заменить на другой ивент так как этот хуйня, лучше конешн что бы вообще проверял каждый тик у каждого игрока, но тогда тпс упадет

    public static void ChangeWeapon() {
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
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

                        // Проверяем, является ли предмет допустимым типом оружия
                        String weaponType = item.getType().name().toLowerCase(); // Получаем тип оружия
                        if (allowedWeaponTypes.contains(weaponType)) {
                            itemMeta.setLore(createLoreWithRarity(rarity, Objects.requireNonNullElse(passiveAbility, "none"))); // Обновляем лор с модификатором

                            item.setItemMeta(itemMeta);

                            // Устанавливаем атрибут урона
                            if (!itemMeta.hasAttributeModifiers()) {
                                double damageModifier = getDamageModifier(rarity);
                                if (damageModifier > 1) {
                                    itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier("generic.attack_damage", damageModifier, AttributeModifier.Operation.ADD_SCALAR));
                                    item.setItemMeta(itemMeta);
                                }
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
                            //типо подсвечивание если епик или ЛеГеНдАрКа!
                            if (Objects.requireNonNull(item.getItemMeta().getLore()).contains("epic")) {
                                player.spawnParticle(Particle.FLAME,player.getLocation().add(0,1,0),3,0.05);
                            } else if (Objects.requireNonNull(item.getItemMeta().getLore()).contains("legendary")) {
                                player.spawnParticle(Particle.SOUL_FIRE_FLAME,player.getLocation().add(0,1,0),3,0.1);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("GribMine")), 0, 1);
    }

    //TODO: нормальную апишку для лора, + добавление цветов для редкостей/абилок
    private static List<String> createLoreWithRarity(String rarity, String passiveAbility) {
        List<String> lore = new ArrayList<>();
        lore.add("Редкость: " + rarity);
        lore.add("Способность: " + passiveAbility);
        return lore;
    }

    public static double getDamageModifier(String rarity) {
        return damageModifiers.getOrDefault(rarity, 1.0); // Возвращаем множитель урона, если рарность не найдена, возвращаем 1.0
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