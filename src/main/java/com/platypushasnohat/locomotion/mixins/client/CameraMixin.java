package com.platypushasnohat.locomotion.mixins.client;

import com.platypushasnohat.locomotion.mixin_utils.StepSmoothing;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow
    protected abstract void setPosition(Vec3 pos);

    @Inject(method = "setup", at = @At("RETURN"))
    private void setup(BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTicks, CallbackInfo ci) {
        if (entity instanceof Player player && !detached) {
            StepSmoothing smoothing = (StepSmoothing) player;
            double offset = smoothing.getStepOffset(partialTicks);
            if (offset < -0.001F) {
                Camera camera = (Camera) (Object) this;
                Vec3 position = camera.getPosition();
                this.setPosition(new Vec3(position.x, position.y + offset, position.z));
            }
        }
    }
}
