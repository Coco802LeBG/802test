package com.deathmotion.marlowcrystal.mixin;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ClientPlayerInteractionManager.class})
public class MultiPlayerGameModeMixin {
    @Inject(method={"attackEntity"}, at={@At(value="INVOKE", target="Lnet/minecraft/entity/player/PlayerEntity;attack(Lnet/minecraft/entity/Entity;)V", shift=At.Shift.AFTER)})
    private void marlowcrystal$afterAttack(PlayerEntity player, Entity entity, CallbackInfo ci) {
        if (!(entity instanceof EndCrystalEntity)) {
            return;
        }
        EndCrystalEntity crystal = (EndCrystalEntity)entity;
        MarlowCrystal mod = MarlowCrystal.get();
        net.minecraft.client.network.ServerInfo info = MinecraftClient.getInstance().getCurrentServerEntry();
        boolean off = info != null && mod.settings().isDisabledOn(info.address);
        if (!mod.serverSession().isOptedOut() && !off) {
            mod.crystalBreaker().breakIfPossible(MinecraftClient.getInstance(), crystal);
        }
    }
}

