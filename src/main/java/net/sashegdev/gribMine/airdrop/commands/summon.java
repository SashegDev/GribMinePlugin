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
                    if (sender.getServer().getOnlinePlayers().isEmpty()) {
                        if (sender.getServer().getOnlinePlayers() instanceof ArrayList) {
                            ArrayList<Player> players = (ArrayList<Player>) sender.getServer().getOnlinePlayers();
                            Player p = players.get(new Random().nextInt(0, players.size()));
                            new airdropMain(p);
                        }
                    }
                }
                if (args[2].equals("atme")) {
                    sender.sendMessage("Генерация аирдропа...");
                    if (sender.getServer().getOnlinePlayers().isEmpty()) {
                        if (sender.getServer().getOnlinePlayers() instanceof ArrayList) {
                            ArrayList<Player> players = (ArrayList<Player>) sender.getServer().getOnlinePlayers();
                            Player p = players.get(new Random().nextInt(0, players.size()));
                            new airdropMain(p, 1, 1);
                        }
                    }
                }
        }
    }
}
