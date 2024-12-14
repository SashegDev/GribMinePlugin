package net.sashegdev.gribMine.commands;

import org.bukkit.command.CommandSender;

public class handleWeaponCommand {
    public handleWeaponCommand(CommandSender sender, String[] args) {
        switch (args[1].toLowerCase()) {
            case "get":
                // Логика для получения информации о оружии
                sender.sendMessage("Получение информации о оружии...");
                break;
            case "set":
                // Логика для установки информации о оружии
                sender.sendMessage("Установка информации о оружии...");
                break;
            case "reassemble":
                // Логика для переоснащения оружия
                sender.sendMessage("Переоснащение оружия...");
                break;
            case "reset":
                // Логика для сброса информации о оружии
                sender.sendMessage("Сброс информации о оружии...");
                break;
            default:
                sender.sendMessage("Неизвестная подкоманда для weapon.");
                break;
        }
    }

}
