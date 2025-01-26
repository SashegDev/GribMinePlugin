package net.sashegdev.gribMine.bunker;

import org.bukkit.Location;

import java.util.UUID;

public class BunkerData {
    private UUID bunkerUUID;
    private Location barrelLocation;

    public BunkerData(UUID bunkerUUID, Location barrelLocation) {
        this.bunkerUUID = bunkerUUID;
        this.barrelLocation = barrelLocation;
    }

    public UUID getBunkerUUID() {
        return bunkerUUID;
    }

    public Location getBarrelLocation() {
        return barrelLocation;
    }
}