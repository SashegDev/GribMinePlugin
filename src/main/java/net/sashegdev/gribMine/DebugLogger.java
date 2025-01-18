package net.sashegdev.gribMine;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DebugLogger {

    // Log levels
    public enum LogLevel {
        INFO,
        WARNING,
        ERROR
    }

    // Log to console and chat (if enabled)
    public static void log(String message, LogLevel level) {
        // Логи уровня ERROR и WARNING всегда выводятся в консоль
        if (level == LogLevel.ERROR || level == LogLevel.WARNING) {
            String formattedMessage = formatConsoleMessage(message, level);
            GribMine.getInstance().getLogger().info(formattedMessage);
        } else if (GribMine.getMineConfig().getBoolean("debug_logs", true)) {
            // Логи уровня INFO выводятся в консоль только если включены в конфиге
            String formattedMessage = formatConsoleMessage(message, level);
            GribMine.getInstance().getLogger().info(formattedMessage);
        }

        // Логи в чат (если включены в конфиге)
        if (GribMine.getMineConfig().getBoolean("chat_debug_logs", true)) {
            String formattedChatMessage = formatChatMessage(message, level);
            for (Player player : GribMine.getInstance().getServer().getOnlinePlayers()) {
                if (player.isOp()) {
                    player.sendMessage(formattedChatMessage);
                }
            }
        }
    }

    // Format console message with log level
    private static String formatConsoleMessage(String message, LogLevel level) {
        return "[" + level.name() + "] " + message;
    }

    // Format chat message with colors based on log level
    private static String formatChatMessage(String message, LogLevel level) {
        ChatColor color;
        switch (level) {
            case INFO:
                color = ChatColor.DARK_AQUA;
                break;
            case WARNING:
                color = ChatColor.YELLOW;
                break;
            case ERROR:
                color = ChatColor.RED;
                break;
            default:
                color = ChatColor.WHITE;
                break;
        }
        return color + "[DEBUG] " + ChatColor.RESET + message;
    }
}