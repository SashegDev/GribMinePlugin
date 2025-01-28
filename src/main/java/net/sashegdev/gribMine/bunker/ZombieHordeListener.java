package net.sashegdev.gribMine.bunker;

import net.sashegdev.gribMine.DebugLogger;
import net.sashegdev.gribMine.GribMine;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.NamespacedKey;
import java.util.Objects;
import java.util.Random;

public class ZombieHordeListener implements Listener {

    private static JavaPlugin plugin = GribMine.getPlugin(GribMine.class);
    private final Random random = new Random();

    public ZombieHordeListener(JavaPlugin plugin) {
        ZombieHordeListener.plugin = plugin;
    }

    @EventHandler
    public void onZombieSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() != EntityType.ZOMBIE) return;
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) return;

        World world = event.getLocation().getWorld();
        if (world == null) return;

        // Проверка времени (ночь)
        long time = world.getTime();
        if (!isNight(time)) return;

        // 3% шанс спавна орды
        if (random.nextDouble() > 0.03) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                spawnHorde(event.getLocation(), (Zombie) event.getEntity());
            }
        }.runTaskLater(plugin, 1L);
    }

    public static void spawnHorde(org.bukkit.Location location, Zombie original) {
        for (int i = 0; i < 10; i++) {
            Zombie zombie = (Zombie) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.ZOMBIE);
            customizeZombie(zombie);
        }
        Location originLocation = original.getLocation();
        original.remove(); // Удаляем оригинального зомби
        DebugLogger.log("Zombies at: X:"+originLocation.getBlockX()+" Z:"+originLocation.getBlockZ()+" World:"+originLocation.getWorld(), DebugLogger.LogLevel.INFO);
    }

    private static void customizeZombie(Zombie zombie) {
        // Настройка характеристик
        zombie.setRemoveWhenFarAway(false);
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1,false,false,false));

        // Установка здоровья
        Objects.requireNonNull(zombie.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(25.0);
        zombie.setHealth(25.0);

        // Добавление тега "zombi"
        NamespacedKey key = new NamespacedKey(plugin, "zombi");
        zombie.getPersistentDataContainer().set(key, PersistentDataType.STRING, "true");

        // Настройка AI
        zombie.setAI(true);
        zombie.setCanPickupItems(false);
    }

    private boolean isNight(long time) {
        return time >= 13000 && time < 23000;
    }
}