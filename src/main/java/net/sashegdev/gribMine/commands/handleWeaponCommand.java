package net.sashegdev.gribMine.commands;

import net.sashegdev.gribMine.weapon.WeaponAbility;
import net.sashegdev.gribMine.weapon.WeaponManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class handleWeaponCommand {
    public handleWeaponCommand(CommandSender sender, String[] args) {
        switch (args[1].toLowerCase()) {
            case "get":
                sender.sendMessage("Получение информации о оружии...");
                try {
                    List<String> lore = sender.getServer().getPlayer(sender.getName()).getInventory().getItemInMainHand().getItemMeta().getLore();
                    if (lore != null)
                        for (String loreline : lore) {
                            sender.sendMessage(loreline);
                        }
                    else
                        throw new NullPointerException("Null Pointer in Lore");
                } catch (NullPointerException ex) {
                    sender.sendMessage(ChatColor.RED+ex.getMessage());
                }
                break;
            case "set":
                sender.sendMessage("Установка информации о оружии...");

                try {
                    if (args.length < 4) {
                        sender.sendMessage(ChatColor.RED + "Используйте: /gribadmin weapon set <rarity> <ability>");
                        return;
                    }

                    ItemStack item = sender.getServer().getPlayer(sender.getName()).getInventory().getItemInMainHand();
                    ItemMeta meta = item.getItemMeta();
                    List<String> lore = new ArrayList<>();

                    // Получаем rarity и ability из аргументов
                    String rarity = args[2].strip();
                    String abilityTag = args[3].strip();

                    // Проверяем, что рарность существует
                    if (!WeaponManager.getRarityList().contains(rarity)) {
                        sender.sendMessage(ChatColor.RED + "Некорректная рарность: " + rarity);
                        return;
                    }

                    // Проверяем, что способность существует
                    HashMap<String, WeaponAbility> abilities = WeaponManager.getWeaponAbilities();
                    if (!abilities.containsKey(abilityTag)) {
                        sender.sendMessage(ChatColor.RED + "Некорректная способность: " + abilityTag);
                        return;
                    }

                    // Устанавливаем лор
                    lore.add("Редкость: " + rarity);
                    lore.add("Способность: " + abilities.get(abilityTag).getRussianName());

                    meta.setLore(lore);
                    item.setItemMeta(meta);

                    sender.sendMessage(ChatColor.GREEN + "Информация о оружии успешно установлена.");
                } catch (NullPointerException ex) {
                    sender.sendMessage(ChatColor.RED + "Ошибка: " + ex.getMessage());
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
                                ItemMeta meta = getItemMeta(item, "Редкость: common", wa);
                                item.setItemMeta(meta);

                                break;
                            }
                        }
                    } else if (chance >= 0.2) {
                        for (WeaponAbility wa : WeaponManager.getWeaponAbilitiesForRarity("uncommon")) {
                            if (Math.random() > 0.5) {
                                ItemStack item = sender.getServer().getPlayer(sender.getName()).getInventory().getItemInMainHand();
                                ItemMeta meta = getItemMeta(item, "Редкость: uncommon", wa);
                                item.setItemMeta(meta);

                                break;
                            }
                        }
                    } else if (chance >= 0.14) {
                        for (WeaponAbility wa : WeaponManager.getWeaponAbilitiesForRarity("rare")) {
                            if (Math.random() > 0.5) {
                                ItemStack item = sender.getServer().getPlayer(sender.getName()).getInventory().getItemInMainHand();
                                ItemMeta meta = getItemMeta(item, "Редкость: rare", wa);
                                item.setItemMeta(meta);

                                break;
                            }
                        }
                    } else if (chance >= 0.055) {
                        for (WeaponAbility wa : WeaponManager.getWeaponAbilitiesForRarity("epic")) {
                            if (Math.random() > 0.5) {
                                ItemStack item = sender.getServer().getPlayer(sender.getName()).getInventory().getItemInMainHand();
                                ItemMeta meta = getItemMeta(item, "Редкость: epic", wa);
                                item.setItemMeta(meta);

                                break;
                            }
                        }
                    } else if (chance >= 0.005) {
                        for (WeaponAbility wa : WeaponManager.getWeaponAbilitiesForRarity("legendary")) {
                            if (Math.random() > 0.5) {
                                ItemStack item = sender.getServer().getPlayer(sender.getName()).getInventory().getItemInMainHand();
                                ItemMeta meta = getItemMeta(item, "Редкость: legendary", wa);
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
                    sender.sendMessage(ChatColor.RED+ex.getMessage());
                }
                break;
            case "reset":
                sender.sendMessage(ChatColor.DARK_GREEN+"Сброс информации о оружии...");
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

                            player.sendMessage(ChatColor.DARK_GREEN+"Информация о оружии сброшена.");
                        } else {
                            player.sendMessage(ChatColor.RED+"У вас нет предмета в руках.");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED+"Игрок не найден.");
                    }
                } catch (NullPointerException ex) {
                    sender.sendMessage(ChatColor.RED+"Произошла ошибка: " + ex.getMessage());
                }
                break;
            default:
                sender.sendMessage(ChatColor.RED+"Неизвестная подкоманда для weapon.");
                break;
        }
    }

    @NotNull
    private static ItemMeta getItemMeta(ItemStack item, String e, WeaponAbility wa) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = item.getItemMeta().getLore();

        assert lore != null;
        if (lore.isEmpty()) {
            lore.add(e);
            lore.add("Способность: " + wa.getRussianName());
        } else {
            lore.set(1, "Способность: " + wa.getRussianName());
        }

        assert meta != null;
        meta.setLore(lore);
        return meta;
    }
}
