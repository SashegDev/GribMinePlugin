package net.sashegdev.gribMine.core;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.sashegdev.gribMine.GribMine;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

public class LegendaryManager {
    private static final File STATE_FILE = new File("plugins/GribMine/legendary_state.json");
    private static JsonObject states = new JsonObject();

    static {
        loadStates();
    }

    private static void loadStates() {
        try {
            if (STATE_FILE.exists()) {
                states = JsonParser.parseReader(new FileReader(STATE_FILE)).getAsJsonObject();
            }
        } catch (IOException e) {
            GribMine.getInstance().getLogger().log(Level.SEVERE, "Failed to load legendary states", e);
        }
    }

    public static boolean canSpawn(LegendaryItem item) {
        return !states.has(item.getId()) || !states.get(item.getId()).getAsBoolean();
    }

    public static void markAsSpawned(LegendaryItem item) {
        states.addProperty(item.getId(), true);
        saveStates();
    }

    private static void saveStates() {
        try (FileWriter writer = new FileWriter(STATE_FILE)) {
            writer.write(states.toString());
        } catch (IOException e) {
            GribMine.getInstance().getLogger().log(Level.SEVERE, "Failed to save legendary states", e);
        }
    }
}