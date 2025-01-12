package net.sashegdev.gribMine;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;
import org.bukkit.loot.LootContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LootListener implements Listener {

    private static final double LOOT_CHANCE = GribMine.getMineConfig().getDouble("Supply.chance");
    private static final int SUPPLY_COUNT = GribMine.getMineConfig().getInt("Supply.amount");

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {
        // Получаем контекст лута
        LootContext context = event.getLootContext();

        // Проверяем, что лут генерируется для контейнера (сундука, бочки и т.д.)
        if (isContainer(context)) {
            Random random = new Random();
            if (random.nextDouble() < LOOT_CHANCE) {
                // Генерируем случайное количество предметов (от 0 до SUPPLY_COUNT)
                int amount = random.nextInt(SUPPLY_COUNT);
                for (int i = 0; i < amount; i++) {
                    // Добавляем предмет в лут
                    event.getLoot().add(createAirSupplyItem());
                }
            }
        }
    }

    private boolean isContainer(LootContext context) {
        // Проверяем, что источник лута — это блок (например, сундук или бочка)
        if (context.getLocation() == null) return false;
        Material type = context.getLocation().getBlock().getType();
        return type == Material.CHEST || type == Material.BARREL || type == Material.SHULKER_BOX;
    }

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
}