package com.deathmotion.marlowcrystal.crystal;

import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.EntityHitResult;

final class Crosshair {
    private Crosshair() {
    }

    static void retargetPast(MinecraftClient client, EndCrystalEntity crystal) {
        EntityHitResult target;
        HitResult ItemStackParticleEffect = client.crosshairTarget;
        if (!(ItemStackParticleEffect instanceof EntityHitResult) || (target = (EntityHitResult)ItemStackParticleEffect).getEntity() != crystal) {
            return;
        }
        client.gameRenderer.updateCrosshairTarget(1.0f);
    }
}

