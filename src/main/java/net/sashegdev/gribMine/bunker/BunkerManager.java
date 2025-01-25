package net.sashegdev.gribMine.bunker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.sashegdev.gribMine.DebugLogger;
import org.bukkit.Location;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BunkerManager {

    private static final String BUNKERS_FILE = "bunkers.json";
    private static List<BunkerData> bunkers = new ArrayList<>();

    // Загрузка данных из bunkers.json
    public static void loadBunkerData() {
        try (FileReader reader = new FileReader(BUNKERS_FILE)) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<BunkerData>>() {}.getType();
            bunkers = gson.fromJson(reader, type);
            DebugLogger.log("Данные бункеров загружены из bunkers.json", DebugLogger.LogLevel.INFO);
        } catch (IOException e) {
            DebugLogger.log("Ошибка при загрузке bunkers.json: " + e.getMessage(), DebugLogger.LogLevel.ERROR);
        }
    }

    // Сохранение данных в bunkers.json
    public static void saveBunkerData(BunkerData bunkerData) {
        bunkers.add(bunkerData);
        try (FileWriter writer = new FileWriter(BUNKERS_FILE)) {
            Gson gson = new Gson();
            gson.toJson(bunkers, writer);
            DebugLogger.log("Данные бункеров сохранены в bunkers.json", DebugLogger.LogLevel.INFO);
        } catch (IOException e) {
            DebugLogger.log("Ошибка при сохранении bunkers.json: " + e.getMessage(), DebugLogger.LogLevel.ERROR);
        }
    }
}