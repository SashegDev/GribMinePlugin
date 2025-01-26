package net.sashegdev.gribMine;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DebugLogger {

    public enum LogLevel {
        INFO,
        WARNING,
        ERROR
    }

    public static void log(String message, LogLevel level) {
        String className = getCallingClassName();
        String formattedMessage = "[" + className + "] " + message;

        // Логирование в консоль
        if (level == LogLevel.ERROR || level == LogLevel.WARNING) {
            logToConsole(formattedMessage, level);
        } else if (GribMine.getMineConfig().getBoolean("debug_logs", true)) {
            logToConsole(formattedMessage, level);
        }

        // Логирование в чат
        if (GribMine.getMineConfig().getBoolean("chat_debug_logs", true)) {
            logToChat(formattedMessage, level);
        }
    }

    private static String getCallingClassName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > 3) {
            String fullClassName = stackTrace[3].getClassName();
            return fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
        }
        return "UnknownClass";
    }

    private static void logToConsole(String message, LogLevel level) {
        GribMine.getInstance().getLogger().info(formatConsoleMessage(message, level));
    }

    private static void logToChat(String message, LogLevel level) {
        String formatted = formatChatMessage(message, level);
        for (Player player : GribMine.getInstance().getServer().getOnlinePlayers()) {
            if (player.isOp()) {
                player.sendMessage(formatted);
            }
        }
    }

    private static String formatConsoleMessage(String message, LogLevel level) {
        return "[" + level.name() + "] " + message;
    }

    private static String formatChatMessage(String message, LogLevel level) {
        ChatColor color = switch (level) {
            case INFO -> ChatColor.DARK_AQUA;
            case WARNING -> ChatColor.YELLOW;
            case ERROR -> ChatColor.RED;
            default -> ChatColor.WHITE;
        };
        return color + "[DEBUG] " + ChatColor.RESET + message;
    }
}