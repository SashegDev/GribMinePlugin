package net.sashegdev.gribMine.tool;

import net.sashegdev.gribMine.GribMine;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ToolAbilityManager {

    private static final Map<String, ToolAbility> toolAbilities = new HashMap<>();
    private static final List<String> allowedToolTypes = GribMine.getMineConfig().getStringList("allowed_tool_types");

    // Добавляем способность для инструмента
    public static void addAbility(String toolName, ToolAbility ability) {
        toolAbilities.put(toolName, ability);
    }

    // Получаем способность по названию
    public static ToolAbility getAbility(String toolName) {
        return toolAbilities.get(toolName);
    }

    // Добавляем способность на инструмент
    public static void addAbilityToTool(ItemStack tool, String abilityName) {
        if (tool == null || !allowedToolTypes.contains(tool.getType().name().toLowerCase())) {
            return;
        }

        ItemMeta meta = tool.getItemMeta();
        if (meta == null) return;

        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }

        // Добавляем способность инструмента с русским именем
        ToolAbility ability = getAbility(abilityName);
        if (ability != null) {
            lore.add("Способность: " + ability.getRussianName());
        }
        meta.setLore(lore);
        tool.setItemMeta(meta);
    }

    // Применяем способности инструмента
    public static void applyAbilities(Player player, ItemStack tool) {
        if (tool == null || !allowedToolTypes.contains(tool.getType().name().toLowerCase())) {
            return;
        }

        ItemMeta meta = tool.getItemMeta();
        if (meta == null) return;

        List<String> lore = meta.getLore();
        if (lore == null) return;

        // Применяем все способности инструмента
        for (String line : lore) {
            if (line.startsWith("Способность(инструмент): ")) {
                String abilityName = line.substring("Способность(инструмент): ".length());
                ToolAbility ability = getAbilityByName(abilityName);
                if (ability != null && Math.random() < ability.getChance()) {
                    ability.activate(player, tool);
                }
            }
        }
    }

    // Получаем способность по русскому имени
    private static ToolAbility getAbilityByName(String russianName) {
        for (ToolAbility ability : toolAbilities.values()) {
            if (ability.getRussianName().equals(russianName)) {
                return ability;
            }
        }
        return null;
    }

    public static String getRandomAbility() {
        if (toolAbilities.isEmpty()) {
            return null;
        }
        List<String> abilityNames = new ArrayList<>(toolAbilities.keySet());
        return abilityNames.get(new Random().nextInt(abilityNames.size()));
    }

    public static List<String> getAbilityNames() {
        return new ArrayList<>(toolAbilities.keySet());
    }
}