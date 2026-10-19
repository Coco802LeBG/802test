package com.deathmotion.marlowcrystal.crystal;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.client.network.ClientPlayerEntity;

final class AttackDamage {
    private static final int STRENGTH_ENDING_TICKS = 30;

    private AttackDamage() {
    }

    static boolean breaksCrystal(ClientPlayerEntity player) {
        StatusEffectInstance weakness;
        double damage = player.getAttributeBaseValue(EntityAttributes.ATTACK_DAMAGE);
        damage += AttackDamage.weaponDamage(player.getMainHandStack());
        StatusEffectInstance strength = player.getStatusEffect(StatusEffects.STRENGTH);
        if (strength != null && (strength.getDuration() < 0 || strength.getDuration() > 30)) {
            damage += 3.0 * (double)(strength.getAmplifier() + 1);
        }
        if ((weakness = player.getStatusEffect(StatusEffects.WEAKNESS)) != null) {
            damage -= 4.0 * (double)(weakness.getAmplifier() + 1);
        }
        return damage > 0.0;
    }

    private static double weaponDamage(ItemStack item) {
        if (item.isEmpty()) {
            return 0.0;
        }
        double[] sum = new double[]{0.0};
        item.applyAttributeModifiers(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
            if (EntityAttributes.ATTACK_DAMAGE.equals(attribute)) {
                sum[0] = sum[0] + modifier.value();
            }
        });
        return sum[0];
    }
}

