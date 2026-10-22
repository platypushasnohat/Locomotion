package com.platypushasnohat.locomotion.mixins;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "maxUpStep", at = @At(value = "HEAD"), cancellable = true)
    public void locomotion$maxUpStep(CallbackInfoReturnable<Float> cir) {
        LivingEntity living = (LivingEntity) (Object) this;
        if (living instanceof Player player) {
            float attribute = (float) player.getAttributeValue(Attributes.STEP_HEIGHT);
            float stepHeight = player.isShiftKeyDown() ? attribute : attribute + 0.6F;
            cir.setReturnValue(player.getControllingPassenger() instanceof Player ? Math.max(attribute, 1.0F) : stepHeight);
        }
    }
}
