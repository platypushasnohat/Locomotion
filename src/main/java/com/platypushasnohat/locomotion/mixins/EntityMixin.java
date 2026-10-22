package com.platypushasnohat.locomotion.mixins;

import com.platypushasnohat.locomotion.mixin_utils.StepSmoothing;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(Entity.class)
public class EntityMixin implements StepSmoothing {

    @Unique
    private static final float MAX_OFFSET = 0.5F;
    @Unique
    private static final float SMOOTHING = 0.33F;

    @Unique
    private float prevStepOffset = 0.0F;
    @Unique
    private float stepOffset = 0.0F;
    @Unique
    private float startY = 0.0F;

    @Override
    public float getStepOffset(float partialTicks) {
        return Mth.lerp(partialTicks, this.prevStepOffset, this.stepOffset);
    }

    @Inject(method = "move", at = @At("HEAD"))
    private void moveHead(MoverType type, Vec3 pos, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (entity.level().isClientSide && entity instanceof Player player) {
            this.startY = (float) player.getY();
        }
    }

    @Inject(method = "move", at = @At("RETURN"))
    private void moveReturn(MoverType type, Vec3 pos, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (entity.level().isClientSide && entity instanceof Player player) {
            float yPos = (float) player.getY() - this.startY;
            if (yPos > 0.0F && yPos > pos.y + 0.01F) {
                this.stepOffset -= yPos;
                if (this.stepOffset < -MAX_OFFSET) {
                    this.stepOffset = -MAX_OFFSET;
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (entity.level().isClientSide) {
            this.prevStepOffset = this.stepOffset;
            if (Math.abs(this.stepOffset) < 0.001F) {
                this.stepOffset = 0.0F;
            } else {
                this.stepOffset += SMOOTHING;
                if (this.stepOffset > 0.0F) {
                    this.stepOffset = 0.0F;
                }
            }
        }
    }
}
