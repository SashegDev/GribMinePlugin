package net.sashegdev.gribMine.core;

import net.sashegdev.gribMine.legendary.HermesSandals;
import net.sashegdev.gribMine.legendary.NyktaClock;
import net.sashegdev.gribMine.legendary.ThorHammer;

import java.util.*;

public class LegendaryRegistry {
    private static final Map<String, LegendaryItem> legendaries = new HashMap<>();

    static {
        register(new NyktaClock());
        register(new HermesSandals());
        register(new ThorHammer());
    }

    private static void register(LegendaryItem item) {
        legendaries.put(item.getId(), item);
    }

    public static Collection<LegendaryItem> getAll() {
        return legendaries.values();
    }

    public static LegendaryItem getById(String id) {
        return legendaries.get(id);
    }

    // Получение списка всех ID
    public static List<String> getAllIds() {
        return new ArrayList<>(legendaries.keySet());
    }
}