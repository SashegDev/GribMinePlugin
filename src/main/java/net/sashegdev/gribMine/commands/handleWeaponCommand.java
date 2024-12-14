package net.sashegdev.gribMine.commands;

import net.sashegdev.gribMine.GribMine;
import net.sashegdev.gribMine.weapon.ability.*;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

import java.util.*;
public class handleWeaponCommand {
    public handleWeaponCommand(CommandSender sender, String[] args) {
        switch (args[1].toLowerCase()) {
            case "get":
                sender.sendMessage("Получение информации о оружии...");
                try {
                    List<String> lore = sender.getServer().getPlayer(sender.getName()).getInventory().getItemInMainHand().getItemMeta().getLore();
                    if (lore != null)
                        sender.sendMessage(String.join("", lore));
                    else
                        throw new NullPointerException("Null Pointer in Lore");
                } catch (NullPointerException ex) {
                    sender.sendMessage(ex.getMessage());
                }
                break;
            case "set":
                sender.sendMessage("Установка информации о оружии...");
                try {
                    ItemStack item = sender.getServer().getPlayer(sender.getName()).getInventory().getItemInMainHand();
                    ItemMeta meta = item.getItemMeta();

                    List<String> lore = new ArrayList<String>();
                    //Хэш для строгого порядка описания на выходе
                    HashMap<String, String> lines = new HashMap<String, String>();

                    for (String arg : args) {

                        String[] m = arg.split("=");
                        if (m[0].strip().equals("rarity")) {
                            lines.put("rarity", m[1].strip());
                        }
                        if (m[0].strip().equals("ability")) {
                            lines.put("ability", m[1].strip());
                        }
                    }

                    lore.add("Редкость: " + (lines.get("rarity") != null ? lines.get("rarity") : "common"));
                    lore.add("Способность: " + (lines.get("ability") != null ? lines.get("ability") : "none"));
                    lore.add("Модификатор урона: " + GribMine.getMineConfig().getDouble("damage_mod." + lines.get("rarity"), 1.0));

                    meta.setLore(lore);

                    item.setItemMeta(meta);
                } catch (NullPointerException ex) {
                    sender.sendMessage(ex.getMessage());
                }
                break;
            case "reassemble":
                // Логика для переоснащения оружия
                sender.sendMessage("Перековка оружия...");
                
                break;
            case "reset":
                sender.sendMessage("Сброс информации о оружии...");
                try {
                    ItemStack item = sender.getServer().getPlayer(sender.getName()).getInventory().getItemInMainHand();
                    ItemMeta meta = item.getItemMeta();
                    meta.setLore(new ArrayList<String>());
                    item.setItemMeta(meta);
                } catch(NullPointerException ex) {
                    sender.sendMessage(ex.getMessage());
                }
                break;
            default:
                sender.sendMessage("Неизвестная подкоманда для weapon.");
                break;
        }
    }
}
