package net.sashegdev.gribMine.commands;

import net.sashegdev.gribMine.GribMine;
import net.sashegdev.gribMine.weapon.WeaponManager;
import net.sashegdev.gribMine.weapon.WeaponAbility;
import net.sashegdev.gribMine.exceptions.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

import java.util.*;
public class handleWeaponCommand {
    //TODO: сделать подсказки команды
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
                    //if (WeaponManager.getAllowedWeaponTypes().contains(item.getType().toString())) {
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

                        lore.add("Способность: " + WeaponManager.getWeaponAbilities().get(lines.get("ability")).getRussianName());

                        lore.add("Модификатор урона: " + GribMine.getMineConfig().getDouble("damage_mod." + lines.get("rarity"), 1.0));

                        meta.setLore(lore);

                        item.setItemMeta(meta);
                    //} else {

                        //throw new ItemTypeException("Wrong type of object");
                    //}
                } catch (NullPointerException ex) {
                    sender.sendMessage(ex.getMessage());
                //} catch (ItemTypeException ex) {
                    //sender.sendMessage(ex.getMessage());
                }
                break;
            case "reassemble":
                /*
                TODO: шансы в конфиг)
                 */
                // Логика для переоснащения оружия
                sender.sendMessage("Перековка оружия...");
                try {
                    double chance = Math.random();
                    if (chance >= 0.6) {
                        for (WeaponAbility wa : WeaponManager.getWeaponAbilitiesForRarity("common")) {
                            if (Math.random() > 0.5) {
                                ItemStack item = sender.getServer().getPlayer(sender.getName()).getInventory().getItemInMainHand();
                                ItemMeta meta = item.getItemMeta();
                                List<String> lore = item.getItemMeta().getLore();

                                if (lore.isEmpty()) {
                                    lore.add("Редкость: common");
                                    lore.add("Способность: " + wa.getRussianName());
                                    lore.add("Модификатор урона: " + GribMine.getMineConfig().getDouble("damage_mod.common", 1.0));
                                } else {
                                    lore.set(1, "Способность: " + wa.getRussianName());
                                }

                                meta.setLore(lore);
                                item.setItemMeta(meta);

                                break;
                            }
                        }
                    } else if (chance >= 0.2) {
                        for (WeaponAbility wa : WeaponManager.getWeaponAbilitiesForRarity("uncommon")) {
                            if (Math.random() > 0.5) {
                                ItemStack item = sender.getServer().getPlayer(sender.getName()).getInventory().getItemInMainHand();
                                ItemMeta meta = item.getItemMeta();
                                List<String> lore = item.getItemMeta().getLore();

                                if (lore.isEmpty()) {
                                    lore.add("Редкость: uncommon");
                                    lore.add("Способность: " + wa.getRussianName());
                                    lore.add("Модификатор урона: " + GribMine.getMineConfig().getDouble("damage_mod.uncommon", 1.0));
                                } else {
                                    lore.set(1, "Способность: " + wa.getRussianName());
                                }

                                meta.setLore(lore);
                                item.setItemMeta(meta);

                                break;
                            }
                        }
                    } else if (chance >= 0.14) {
                        for (WeaponAbility wa : WeaponManager.getWeaponAbilitiesForRarity("rare")) {
                            if (Math.random() > 0.5) {
                                ItemStack item = sender.getServer().getPlayer(sender.getName()).getInventory().getItemInMainHand();
                                ItemMeta meta = item.getItemMeta();
                                List<String> lore = item.getItemMeta().getLore();

                                if (lore.isEmpty()) {
                                    lore.add("Редкость: rare");
                                    lore.add("Способность: " + wa.getRussianName());
                                    lore.add("Модификатор урона: " + GribMine.getMineConfig().getDouble("damage_mod.rare", 1.0));
                                } else {
                                    lore.set(1, "Способность: " + wa.getRussianName());
                                }

                                meta.setLore(lore);
                                item.setItemMeta(meta);

                                break;
                            }
                        }
                    } else if (chance >= 0.055) {
                        for (WeaponAbility wa : WeaponManager.getWeaponAbilitiesForRarity("epic")) {
                            if (Math.random() > 0.5) {
                                ItemStack item = sender.getServer().getPlayer(sender.getName()).getInventory().getItemInMainHand();
                                ItemMeta meta = item.getItemMeta();
                                List<String> lore = item.getItemMeta().getLore();

                                if (lore.isEmpty()) {
                                    lore.add("Редкость: epic");
                                    lore.add("Способность: " + wa.getRussianName());
                                    lore.add("Модификатор урона: " + GribMine.getMineConfig().getDouble("damage_mod.epic", 1.0));
                                } else {
                                    lore.set(1, "Способность: " + wa.getRussianName());
                                }

                                meta.setLore(lore);
                                item.setItemMeta(meta);

                                break;
                            }
                        }
                    } else if (chance >= 0.005) {
                        for (WeaponAbility wa : WeaponManager.getWeaponAbilitiesForRarity("legendary")) {
                            if (Math.random() > 0.5) {
                                ItemStack item = sender.getServer().getPlayer(sender.getName()).getInventory().getItemInMainHand();
                                ItemMeta meta = item.getItemMeta();
                                List<String> lore = item.getItemMeta().getLore();

                                if (lore.isEmpty()) {
                                    lore.add("Редкость: legendary");
                                    lore.add("Способность: " + wa.getRussianName());
                                    lore.add("Модификатор урона: " + GribMine.getMineConfig().getDouble("damage_mod.legendary", 1.0));
                                } else {
                                    lore.set(1, "Способность: " + wa.getRussianName());
                                }

                                meta.setLore(lore);
                                item.setItemMeta(meta);

                                break;
                            }
                        }
                    } else {
                        ItemStack item = sender.getServer().getPlayer(sender.getName()).getInventory().getItemInMainHand();
                        ItemMeta meta = item.getItemMeta();
                        meta.setLore(new ArrayList<>());
                        item.setItemMeta(meta);
                    }
                } catch (NullPointerException ex) {
                    sender.sendMessage(ex.getMessage());
                }
                break;
            case "reset":
                sender.sendMessage("Сброс информации о оружии...");
                try {
                    Player player = sender.getServer().getPlayer(sender.getName());
                    if (player != null) {
                        ItemStack item = player.getInventory().getItemInMainHand();
                        if (item != null && item.hasItemMeta()) {
                            // Получаем тип предмета
                            Material itemType = item.getType();

                            // Создаем новый ItemMeta без атрибутов
                            ItemMeta newMeta = Bukkit.getItemFactory().getItemMeta(itemType);
                            if (newMeta != null) {
                                // Устанавливаем новый ItemMeta в предмет
                                item.setItemMeta(newMeta);
                            }

                            player.sendMessage("Информация о оружии сброшена.");
                        } else {
                            player.sendMessage("У вас нет предмета в руках.");
                        }
                    } else {
                        sender.sendMessage("Игрок не найден.");
                    }
                } catch (NullPointerException ex) {
                    sender.sendMessage("Произошла ошибка: " + ex.getMessage());
                }
                break;
            default:
                sender.sendMessage("Неизвестная подкоманда для weapon.");
                break;
        }
    }
}
