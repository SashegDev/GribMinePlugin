package net.sashegdev.gribMine.abilities;

import net.md_5.bungee.api.ChatColor;
import net.sashegdev.gribMine.GribMine;
import net.sashegdev.gribMine.tool.ToolAbilityManager;
import net.sashegdev.gribMine.tool.ability.SpeedMiningAbility;
import net.sashegdev.gribMine.weapon.WeaponManager;
import net.sashegdev.gribMine.weapon.ability.*;
import net.sashegdev.gribMine.weapon.ability.passive.HealPassive;
import net.sashegdev.gribMine.weapon.ability.passive.SlownessPassive;
import net.sashegdev.gribMine.weapon.ability.passive.UnhealPassive;

import java.util.HashMap;

public class AbilityInitializer {

    static {
        // Инициализация способностей для оружия
        WeaponManager weaponManager = new WeaponManager(GribMine.getMineConfig().getStringList("rarity_list"), new HashMap<>());
        initializeWeaponAbilities(weaponManager);

        // Инициализация способностей для инструментов
        initializeToolAbilities();
    }

    public static void initializeWeaponAbilities(WeaponManager weaponManager) {
        // Инициализация способностей для оружия
        weaponManager.addAbility(new FireAbility().getName(), "rare", new FireAbility());
        weaponManager.addAbility(new LightStrike().getName(), "legendary", new LightStrike());
        weaponManager.addAbility(new DesiccationAbility().getName(), "uncommon", new DesiccationAbility());
        weaponManager.addAbility(new FreezeAbility().getName(), "epic", new FreezeAbility());
        weaponManager.addAbility(new BloodLust().getName(), "uncommon", new BloodLust());
        weaponManager.addAbility(new BladeVortex().getName(), "rare", new BladeVortex());
        weaponManager.addAbility(new SirenSong().getName(), "epic", new SirenSong());
        weaponManager.addAbility(new Sacrifice().getName(), "legendary", new Sacrifice());
        weaponManager.addAbility(new ShadowCloak().getName(), "uncommon", new ShadowCloak());
        weaponManager.addAbility(new FlamingDance().getName(), "common", new FlamingDance());
        weaponManager.addAbility(new Suffocation().getName(), "rare", new Suffocation());

        // Инициализация пассивных способностей
        weaponManager.addAbility(new HealPassive().getName(), "common", new HealPassive());
        weaponManager.addAbility(new SlownessPassive().getName(), "uncommon", new SlownessPassive());
        weaponManager.addAbility(new UnhealPassive().getName(), "rare", new UnhealPassive());
    }

    public static void initializeToolAbilities() {
        // Инициализация способностей для инструментов
        ToolAbilityManager.addAbility(new SpeedMiningAbility().getName(), new SpeedMiningAbility());
    }
}