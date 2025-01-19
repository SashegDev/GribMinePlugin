package net.sashegdev.gribMine.weapon.ability.passive;

import net.sashegdev.gribMine.weapon.PassiveAbility;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HealPassive extends PassiveAbility {

    public HealPassive() {
        super("heal", "Лечение", 0.1);
    }

    @Override
    public void onTick(Player player) {
        if (Math.random() < getChance()) {
            // Получаем текущий эффект REGENERATION, если он есть
            PotionEffect currentEffect = player.getPotionEffect(PotionEffectType.REGENERATION);
            int currentAmplifier = 0; // Базовый уровень усиления
            int duration = 20; // Длительность эффекта в тиках (1 секунда)

            if (currentEffect != null) {
                // Если эффект уже активен, увеличиваем его уровень
                currentAmplifier = currentEffect.getAmplifier() + 1;
                duration = currentEffect.getDuration() + 20; // Увеличиваем длительность
            }

            // Накладываем эффект с новым уровнем и длительностью
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, currentAmplifier,false,false,true));
        }
    }

    @Override
    public void activate(Player player, Entity entity) {
        // Пассивная способность не требует активации, поэтому метод можно оставить пустым
    }
}