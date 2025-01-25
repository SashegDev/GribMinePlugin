package net.sashegdev.gribMine;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Set;
import java.util.UUID;

public class CommandSenderInterceptor implements CommandSender {
    private final CommandSender originalSender;

    public CommandSenderInterceptor(CommandSender originalSender) {
        this.originalSender = originalSender;
    }

    @Override
    public void sendMessage(@NotNull String message) {
        originalSender.sendMessage(message);
    }

    @Override
    public void sendMessage(@NotNull String... messages) {
        originalSender.sendMessage(messages);
    }

    @Override
    public void sendMessage(@Nullable UUID sender, @NotNull String message) {
        originalSender.sendMessage(sender, message);
    }

    @Override
    public void sendMessage(@Nullable UUID sender, @NotNull String... messages) {
        originalSender.sendMessage(sender, messages);
    }

    @NotNull
    @Override
    public Server getServer() {
        return originalSender.getServer();
    }

    @NotNull
    @Override
    public String getName() {
        return originalSender.getName();
    }

    @Override
    public boolean isOp() {
        return originalSender.isOp();
    }

    @Override
    public void setOp(boolean value) {
        originalSender.setOp(value);
    }

    @Override
    public boolean isPermissionSet(@NotNull String name) {
        return originalSender.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission perm) {
        return originalSender.isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return originalSender.hasPermission(permission);
    }

    @Override
    public boolean hasPermission(@NotNull Permission perm) {
        return originalSender.hasPermission(perm);
    }

    @NotNull
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value) {
        return originalSender.addAttachment(plugin, name, value);
    }

    @NotNull
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return originalSender.addAttachment(plugin);
    }

    @Nullable
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value, int ticks) {
        return originalSender.addAttachment(plugin, name, value, ticks);
    }

    @Nullable
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, int ticks) {
        return originalSender.addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(@NotNull PermissionAttachment attachment) {
        originalSender.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        originalSender.recalculatePermissions();
    }

    @NotNull
    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return originalSender.getEffectivePermissions();
    }

    @Override
    public @NotNull Spigot spigot() {
        return originalSender.spigot();
    }
}