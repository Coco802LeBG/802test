package com.deathmotion.marlowcrystal.crystal;

import com.deathmotion.marlowcrystal.crystal.AttackDamage;
import com.deathmotion.marlowcrystal.crystal.Crosshair;
import com.deathmotion.marlowcrystal.crystal.KeptCrystals;
import com.deathmotion.marlowcrystal.crystal.SequencedLevel;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.network.ClientPlayerEntity;

public final class CrystalBreaker {
    private final KeptCrystals keptCrystals;

    public CrystalBreaker(KeptCrystals keptCrystals) {
        this.keptCrystals = keptCrystals;
    }

    public void breakIfPossible(MinecraftClient client, EndCrystalEntity crystal) {
        ClientPlayerEntity player = client.player;
        ClientWorld level = client.world;
        if (player == null || level == null) {
            return;
        }
        if (!level.getWorldBorder().contains(crystal.getBlockPos()) || !AttackDamage.breaksCrystal(player)) {
            return;
        }
        this.keptCrystals.keep(crystal, ((SequencedLevel)level).marlowcrystal$blockSequence());
        Crosshair.retargetPast(client, crystal);
    }
}

