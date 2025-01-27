package net.sashegdev.gribMine.legendary;

import net.sashegdev.gribMine.DebugLogger;
import net.sashegdev.gribMine.core.LegendaryItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;

public class NyktaClock extends LegendaryItem {
    public NyktaClock() {
        super(
                "nykta_clock",
                Material.CLOCK,
                ChatColor.DARK_PURPLE + "Часы Нюкта",
                Arrays.asList(
                        ChatColor.GRAY + "Манипулирует временем",
                        ChatColor.BOLD+""+ChatColor.YELLOW + "Легендарный артефакт"
                ),
                0.05,
                true
        );
    }

    @Override
    public void onUse(Player player) {
        // Получаем предмет из главной руки игрока
        ItemStack item = player.getInventory().getItemInMainHand();

        // Проверяем, что предмет не null и соответствует легендарному предмету
        if (item != null && item.isSimilar(this.getItemStack())) {
            // Устанавливаем кулдаун на 20 минут (20 * 60 * 20 тиков)
            player.setCooldown(item.getType(), 20 * 60 * 20);

            // Изменяем время в мире
            World world = player.getWorld();
            world.setFullTime(world.getTime() < 13000 ? 14000 : 0);

            // Спауним частицы и отправляем сообщение игроку
            world.spawnParticle(Particle.REVERSE_PORTAL, player.getLocation(), 160, 0.1, 0.1, 0.1, 0.2, null, true);
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Время изменено!");
        } else {
            // Логируем ошибку, если предмет не найден или не соответствует
            DebugLogger.log("Предмет не найден в руке игрока или не соответствует легендарному предмету.", DebugLogger.LogLevel.WARNING);
        }
    }
}