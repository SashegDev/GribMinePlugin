package net.sashegdev.gribMine.commands;

import net.sashegdev.gribMine.tool.ToolAbility;
import net.sashegdev.gribMine.tool.ToolAbilityManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ToolCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эта команда доступна только игрокам.");
            return true;
        }

        Player player = (Player) sender;
        ItemStack tool = player.getInventory().getItemInMainHand();

        if (args.length < 1) {
            sender.sendMessage("Используйте: /gribadmin tool <set|get|reset|reassemble>");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "set":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Используйте: /gribadmin tool set <ability>");
                    return true;
                }
                String abilityToSet = args[1];
                if (ToolAbilityManager.getAbility(abilityToSet) == null) {
                    sender.sendMessage(ChatColor.RED + "Способность не найдена: " + abilityToSet);
                    return true;
                }
                setToolAbility(tool, abilityToSet);
                sender.sendMessage(ChatColor.GREEN + "Способность установлена на инструмент.");
                break;

            case "get":
                ItemMeta meta = tool.getItemMeta();
                if (meta == null || !meta.hasLore()) {
                    sender.sendMessage(ChatColor.RED + "На инструменте нет способностей.");
                    return true;
                }
                List<String> lore = meta.getLore();
                for (String line : lore) {
                    if (line.startsWith("Способность(инструмент): ")) {
                        sender.sendMessage(ChatColor.GREEN + line);
                    }
                }
                break;

            case "reset":
                resetToolAbility(tool);
                sender.sendMessage(ChatColor.GREEN + "Способности инструмента сброшены.");
                break;

            case "reassemble":
                reassembleToolAbility(tool);
                sender.sendMessage(ChatColor.GREEN + "Способности инструмента пересобраны.");
                break;

            default:
                sender.sendMessage(ChatColor.RED + "Неизвестная подкоманда. Используйте: /gribadmin tool <set|get|reset|reassemble>");
                break;
        }

        return true;
    }

    private void setToolAbility(ItemStack tool, String abilityName) {
        ItemMeta meta = tool.getItemMeta();
        if (meta == null) return;

        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }

        // Удаляем старые способности инструмента
        lore.removeIf(line -> line.startsWith("Способность: "));

        // Добавляем новую способность
        ToolAbility ability = ToolAbilityManager.getAbility(abilityName);
        if (ability != null) {
            lore.add("Способность: " + ability.getRussianName());
        }
        meta.setLore(lore);
        tool.setItemMeta(meta);
    }

    private void resetToolAbility(ItemStack tool) {
        ItemMeta meta = tool.getItemMeta();
        if (meta == null) return;

        List<String> lore = meta.getLore();
        if (lore == null) return;

        // Удаляем все способности инструмента
        lore.removeIf(line -> line.startsWith("Способность(инструмент): "));
        meta.setLore(lore);
        tool.setItemMeta(meta);
    }

    private void reassembleToolAbility(ItemStack tool) {
        // Логика для пересборки способностей (например, случайная замена)
        resetToolAbility(tool);
        String randomAbility = ToolAbilityManager.getRandomAbility();
        if (randomAbility != null) {
            setToolAbility(tool, randomAbility);
        }
    }
}