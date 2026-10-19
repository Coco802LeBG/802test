package com.deathmotion.marlowcrystal.mixin;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import com.deathmotion.marlowcrystal.crystal.SequencedLevel;
import net.minecraft.entity.Entity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.network.PendingUpdateManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ClientWorld.class})
public abstract class ClientLevelMixin
implements SequencedLevel {
    @Shadow
    @Final
    private PendingUpdateManager pendingUpdateManager;

    @Override
    public int marlowcrystal$blockSequence() {
        return this.pendingUpdateManager.getSequence();
    }

    @Inject(method={"getEntities"}, at={@At(value="RETURN")}, cancellable=true)
    private void marlowcrystal$hideKeptCrystals(CallbackInfoReturnable<Iterable<Entity>> cir) {
        MarlowCrystal mod = MarlowCrystal.get();
        if (!mod.settings().isKeepRender()) {
            cir.setReturnValue(mod.keptCrystals().hide((Iterable)cir.getReturnValue()));
        }
    }

    @Inject(method={"handlePlayerActionResponse"}, at={@At(value="HEAD")})
    private void marlowcrystal$releaseKeptCrystals(int sequence, CallbackInfo ci) {
        MarlowCrystal.get().keptCrystals().acknowledged(sequence);
    }
}

