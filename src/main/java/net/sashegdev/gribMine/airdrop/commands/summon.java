package net.sashegdev.gribMine.airdrop.commands;

import net.sashegdev.gribMine.GribMine;
import net.sashegdev.gribMine.airdrop.airdropMain;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class summon {

    public summon(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Использование: /<command> <summon|give> [доп. аргументы]");
            return;
        }

        String subcommand = args[1].toLowerCase();

        if (subcommand.equals("summon")) {
            if (args.length == 2) {
                if (!sender.getServer().getOnlinePlayers().isEmpty()) {
                    List<Player> playerList = new ArrayList<>(Bukkit.getOnlinePlayers());
                    Random rand = new Random();
                    int chosen = rand.nextInt(playerList.size());
                    new airdropMain(playerList.get(chosen));
                    //sender.sendMessage("Аирдроп сгенерирован для игрока: " + playerList.get(chosen).getName());
                } else {
                    sender.sendMessage("Нет игроков онлайн для генерации аирдропа.");
                }
            } else if (args.length == 3 && args[2].equalsIgnoreCase("atme")) {
                if (sender instanceof Player) {
                    new airdropMain((Player) sender, 1, 1);
                    //sender.sendMessage("Аирдроп сгенерирован для вас.");
                } else {
                    sender.sendMessage("Эта команда может быть выполнена только игроком.");
                }
            }
        } else if (subcommand.equals("give")) {
            if (args.length < 4) {
                sender.sendMessage("/gribadmin airdrop <player> <amount>");
                return;
            }

            Player targetPlayer = Bukkit.getPlayer(args[2]); // Получаем целевого игрока из аргументов
            if (targetPlayer == null) {
                sender.sendMessage("Игрок не найден.");
                return;
            }

            int amount;
            try {
                amount = Integer.parseInt(args[3]);
                if (amount <= 0) {
                    sender.sendMessage("Количество должно быть больше нуля.");
                    return;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("Пожалуйста, введите действительное число.");
                return;
            }

            // Выдаем указанное количество предметов
            for (int i = 0; i < amount; i++) {
                GribMine.giveAirSupplyItem(targetPlayer);
            }
            sender.sendMessage("Успешно выдано " + amount + " Воздушное снабжение игроку " + targetPlayer.getName() + ".");
        } else {
            sender.sendMessage("Неизвестная подкоманда. Используйте: /<command> <summon|give> [доп. аргументы]");
        }
    }
}