package com.deathmotion.marlowcrystal.mixin;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value={World.class})
public abstract class LevelMixin {
    @ModifyVariable(method={"getOtherEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;)Ljava/util/List;"}, at=@At(value="HEAD"), argsOnly=true)
    private Predicate<? super Entity> marlowcrystal$hideKeptCrystals(Predicate<? super Entity> predicate) {
        if (!((World)(Object)this).isClient()) {
            return predicate;
        }
        return MarlowCrystal.get().keptCrystals().hide(predicate);
    }
}

