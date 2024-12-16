package net.sashegdev.gribMine.airdrop.commands;

import net.sashegdev.gribMine.airdrop.*;
import org.bukkit.command.*;
import org.bukkit.*;
import org.bukkit.entity.*;

import java.util.*;
public class summon {

    public summon(CommandSender sender, String[] args) {
        switch (args[1].toLowerCase()) {
            case "summon":
                if (args.length == 2) {
                    sender.sendMessage("Генерация аирдропа...");
                    if (!sender.getServer().getOnlinePlayers().isEmpty()) {
                        int max_list = Bukkit.getOnlinePlayers().size();
                        ArrayList<Player> player_list = new ArrayList<>(Bukkit.getOnlinePlayers());
                        Random rand = new Random();
                        int chosen = rand.nextInt(max_list);
                        new airdropMain(player_list.get(chosen), 1, 1);
                    }
                } else if (args[2].equals("atme")) {
                    sender.sendMessage("Генерация аирдропа...");
                    if (!sender.getServer().getOnlinePlayers().isEmpty()) {
                        int max_list = Bukkit.getOnlinePlayers().size();
                        ArrayList<Player> player_list = new ArrayList<>(Bukkit.getOnlinePlayers());
                        Random rand = new Random();
                        int chosen = rand.nextInt(max_list);
                        new airdropMain(player_list.get(chosen), 1, 1);
                    }
                }
        }
    }
}
