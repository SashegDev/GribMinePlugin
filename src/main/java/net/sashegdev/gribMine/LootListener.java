package net.sashegdev.gribMine;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;
import org.bukkit.loot.LootContext;
import org.bukkit.persistence.PersistentDataType;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LootListener implements Listener {

    private static final double LOOT_CHANCE = GribMine.getMineConfig().getDouble("Supply.chance");
    private static final int SUPPLY_COUNT = GribMine.getMineConfig().getInt("Supply.amount");
    private final Random random = new Random();

    // Обработка генерации лута в контейнерах
    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {
        LootContext context = event.getLootContext();
        if (isContainer(context)) {
            if (random.nextDouble() < LOOT_CHANCE) {
                int amount = random.nextInt(SUPPLY_COUNT + 1);
                for (int i = 0; i < amount; i++) {
                    event.getLoot().add(createAirSupplyItem());
                }
            }
        }
    }

    // Обработка смерти зомби с тегом "zombi"
    @EventHandler
    public void onZombieDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Zombie zombie)) return;

        NamespacedKey key = new NamespacedKey(GribMine.getInstance(), "zombi");
        if (zombie.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            if (random.nextDouble() < 0.1) {
                event.getDrops().add(createAirSupplyItem());
            }
        }
    }

    // Создание предмета
    private ItemStack createAirSupplyItem() {
        ItemStack item = new ItemStack(Material.AMETHYST_SHARD, 1);
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.LIGHT_PURPLE + "Воздушное снабжение");
            List<String> lore = new ArrayList<>();
            lore.add("Фиолетовая дымовая граната");
            lore.add("Которая вызывает дроп в небольшом радиусе вокруг себя.");
            lore.add("Данный дроп будет виден всем игрокам на сервере, так что будьте готовы к битве!");
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
        }
        return item;
    }

    // Проверка, что лут генерируется для контейнера
    private boolean isContainer(LootContext context) {
        Material type = context.getLocation().getBlock().getType();
        return type == Material.CHEST || type == Material.BARREL || type == Material.SHULKER_BOX || type == Material.DECORATED_POT;
    }
}