package com.deathmotion.marlowcrystal.mixin;

import com.deathmotion.marlowcrystal.crystal.KeptCrystal;
import net.minecraft.entity.decoration.EndCrystalEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value={EndCrystalEntity.class})
public class EndCrystalMixin
implements KeptCrystal {
    @Unique
    private boolean marlowcrystal$kept;
    @Unique
    private long marlowcrystal$keptAt;
    @Unique
    private int marlowcrystal$sequence;

    @Override
    public void marlowcrystal$keep(long keptAt, int sequence) {
        this.marlowcrystal$kept = true;
        this.marlowcrystal$keptAt = keptAt;
        this.marlowcrystal$sequence = sequence;
    }

    @Override
    public void marlowcrystal$release() {
        this.marlowcrystal$kept = false;
    }

    @Override
    public boolean marlowcrystal$isKept() {
        return this.marlowcrystal$kept;
    }

    @Override
    public long marlowcrystal$keptAt() {
        return this.marlowcrystal$keptAt;
    }

    @Override
    public int marlowcrystal$sequence() {
        return this.marlowcrystal$sequence;
    }
}

