package com.anotherstar.common.entity;

import com.anotherstar.common.config.Config;
import com.anotherstar.util.LoliUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * 萝莉: an unkillable maid that one-shots whatever it touches.
 * See {@code EntityLoli} in the 1.12.2 source.
 */
public class EntityLoli extends PathfinderMob implements com.anotherstar.util.LoliUtil.EntityLoliMarker {

    private boolean dispersal;

    public EntityLoli(EntityType<? extends EntityLoli> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
        this.xpReward = 0;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 64.0D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new LoliAttackGoal(this));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(7, new net.minecraft.world.entity.ai.goal.RandomLookAroundGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        if (!this.dispersal && !this.level().isClientSide() && reason.shouldDestroy()) {
            EntityLoli reborn = new EntityLoli(com.anotherstar.registry.ModEntities.LOLI.get(), this.level());
            reborn.snapTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
            reborn.dispersal = true;
            this.level().addFreshEntity(reborn);
        }
    }

    public boolean isDispersal() {
        return dispersal;
    }

    public void setDispersal(boolean dispersal) {
        this.dispersal = dispersal;
    }

    /** Melee goal that kills instead of dealing damage. */
    private static class LoliAttackGoal extends Goal {
        private final EntityLoli loli;
        private int cooldown;

        LoliAttackGoal(EntityLoli loli) {
            this.loli = loli;
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.loli.getTarget();
            return target != null && target.isAlive() && this.loli.distanceToSqr(target) < 64.0D;
        }

        @Override
        public void tick() {
            LivingEntity target = this.loli.getTarget();
            if (target == null) {
                return;
            }
            this.loli.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.loli.getNavigation().moveTo(target, 1.0D);
            if (this.loli.distanceToSqr(target) <= 4.0D && this.cooldown-- <= 0) {
                this.cooldown = 10;
                LoliUtil.killEntity(this.loli.level(), target, this.loli);
            }
        }
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected boolean canRide(Entity vehicle) {
        return false;
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }
}
