package com.anotherstar.common.entity;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

/**
 * Primed TNT of the three Loli bombs. Behaves like vanilla primed TNT but keeps
 * track of which bomb type it came from so the explosion can differ.
 */
public class EntityLoliBuffAttackTNT extends Entity {

    private int fuse = 80;
    private String variant = "loli_blue_screen_tnt";

    public EntityLoliBuffAttackTNT(EntityType<? extends EntityLoliBuffAttackTNT> type, Level level) {
        super(type, level);
        this.blocksBuilding = true;
    }

    public void setFuse(int fuse) {
        this.fuse = fuse;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getVariant() {
        return variant;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        this.fuse = input.getIntOr("Fuse", 80);
        this.variant = input.getStringOr("Variant", "loli_blue_screen_tnt");
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putInt("Fuse", this.fuse);
        output.putString("Variant", this.variant);
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(motion.x, motion.y - 0.04D, motion.z);
        this.move(net.minecraft.world.entity.MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
        }
        if (--this.fuse <= 0) {
            if (!this.level().isClientSide()) {
                this.explode();
            }
            this.discard();
        }
    }

    private void explode() {
        float power = variant.contains("fail") ? 8.0F : 5.0F;
        this.level().explode(this, this.getX(), this.getY(), this.getZ(), power, Level.ExplosionInteraction.TNT);
        for (var entity : this.level().getEntities(this, this.getBoundingBox().inflate(power * 2.0D))) {
            if (entity instanceof net.minecraft.world.entity.LivingEntity living) {
                living.hurt(this.damageSources().explosion(this, null), Float.MAX_VALUE);
            }
        }
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float amount) {
        return false;
    }
}
