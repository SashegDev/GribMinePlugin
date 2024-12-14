package net.sashegdev.gribMine.weapon;

public class Weapon {
    private final String rarity;
    private final double damageModifier;

    public Weapon(String rarity, double damageModifier) {
        this.rarity = rarity;
        this.damageModifier = damageModifier;
    }

    public String getRarity() {
        return rarity;
    }

    public double getDamageModifier() {
        return damageModifier;
    }
}