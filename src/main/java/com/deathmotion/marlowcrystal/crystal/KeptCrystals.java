package com.deathmotion.marlowcrystal.crystal;

import com.deathmotion.marlowcrystal.crystal.KeptCrystal;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;

public final class KeptCrystals {
    private static long releaseAfter() { return TimeUnit.MILLISECONDS.toNanos(com.deathmotion.marlowcrystal.MarlowCrystal.get().settings().getKeepReleaseMs()); }
    private final List<EndCrystalEntity> kept = new ArrayList<EndCrystalEntity>();
    private long lastKeptAt;

    private static boolean isVisible(Entity entity, long keptSince) {
        KeptCrystal crystal;
        return !(entity instanceof KeptCrystal) || !(crystal = (KeptCrystal)entity).marlowcrystal$isKept() || crystal.marlowcrystal$keptAt() - keptSince <= 0L;
    }

    public void keep(EndCrystalEntity crystal, int sequence) {
        long now = System.nanoTime();
        this.forgetSettled(now - releaseAfter());
        ((KeptCrystal)crystal).marlowcrystal$keep(now, sequence);
        while (this.kept.size() >= com.deathmotion.marlowcrystal.MarlowCrystal.get().settings().getMaxKeptCrystals()) {
            ((KeptCrystal)this.kept.remove(0)).marlowcrystal$release();
        }
        this.kept.add(crystal);
        this.lastKeptAt = now;
    }

    public void acknowledged(int sequence) {
        this.forgetSettled(System.nanoTime() - releaseAfter());
        this.kept.removeIf(crystal -> {
            KeptCrystal keptCrystal = (KeptCrystal)crystal;
            if (keptCrystal.marlowcrystal$sequence() >= sequence) {
                return false;
            }
            keptCrystal.marlowcrystal$release();
            return true;
        });
    }

    public Predicate<? super Entity> hide(Predicate<? super Entity> predicate) {
        long keptSince = System.nanoTime() - releaseAfter();
        if (this.nothingKept(keptSince)) {
            return predicate;
        }
        return entity -> KeptCrystals.isVisible(entity, keptSince) && predicate.test((Entity)entity);
    }

    public Iterable<Entity> hide(Iterable<Entity> entities) {
        long keptSince = System.nanoTime() - releaseAfter();
        if (this.nothingKept(keptSince)) {
            return entities;
        }
        return Iterables.filter(entities, entity -> KeptCrystals.isVisible(entity, keptSince));
    }

    private void forgetSettled(long keptSince) {
        this.kept.removeIf(crystal -> crystal.isRemoved() || KeptCrystals.isVisible((Entity)crystal, keptSince));
    }

    private boolean nothingKept(long keptSince) {
        if (this.kept.isEmpty()) {
            return true;
        }
        if (this.lastKeptAt - keptSince <= 0L) {
            this.kept.clear();
            return true;
        }
        return false;
    }
}

