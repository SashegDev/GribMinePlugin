// LegendaryItem.java
package net.sashegdev.gribMine.core;

import net.sashegdev.gribMine.GribMine;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.ChatColor;

import java.util.List;

public abstract class LegendaryItem {
    private final String id;
    private final Material material;
    private final String displayName;
    private final List<String> lore;
    private final double spawnChance;
    private final boolean enabled;

    public LegendaryItem(String id, Material material, String displayName,
                         List<String> lore, double spawnChance, boolean enabled) {
        this.id = id;
        this.material = material;
        this.displayName = displayName;
        this.lore = lore;
        this.spawnChance = spawnChance;
        this.enabled = enabled;
    }

    public ItemStack createItem() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        // Установка названия и лора
        assert meta != null;
        meta.setDisplayName(displayName);
        meta.setLore(lore);

        // Добавление NBT-тега для идентификации
        NamespacedKey key = new NamespacedKey(GribMine.getInstance(), "legendary_id");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, id);

        item.setItemMeta(meta);
        return item;
    }

    // Геттеры
    public String getId() { return id; }
    public double getSpawnChance() { return spawnChance; }
    public boolean isEnabled() { return enabled; }

    public abstract void onUse(Player player);
}