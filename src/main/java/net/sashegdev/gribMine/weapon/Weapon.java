package net.sashegdev.gribMine.weapon;

import org.bukkit.inventory.ItemStack;

public class Weapon {
    private String name;
    private String rarity;
    private String passiveAbility;
    private ItemStack itemStack; // Это может быть предмет, представляющий оружие

    public Weapon(String rarity, String passiveAbility, ItemStack itemStack) {
        this.rarity = rarity;
        this.passiveAbility = passiveAbility;
        this.itemStack = itemStack;
    }
    public String getRarity() {
        return rarity;
    }

    public String getPassiveAbility() {
        return passiveAbility;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getDescription() {
        return String.format("Оружие: %s\nРарность: %s\nПассивная способность: %s", name, rarity, passiveAbility);
    }


}