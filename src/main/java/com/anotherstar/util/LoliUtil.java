package com.anotherstar.util;

import com.anotherstar.common.config.Config;
import com.anotherstar.common.item.ItemLoliPickaxe;
import com.anotherstar.common.item.ItemSmallLoliPickaxe;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Port of {@code com.anotherstar.util.LoliPickaxeUtil}. */
public final class LoliUtil {

    private LoliUtil() {
    }

    public static ItemStack getLoliPickaxe(LivingEntity entity) {
        if (!(entity instanceof Player player)) {
            return ItemStack.EMPTY;
        }
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof ItemLoliPickaxe) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public static boolean invHaveLoliPickaxe(LivingEntity entity) {
        if (entity instanceof EntityLoliMarker) {
            return true;
        }
        return !getLoliPickaxe(entity).isEmpty();
    }

    /** Marker interface implemented by {@link com.anotherstar.common.entity.EntityLoli}. */
    public interface EntityLoliMarker {
    }

    public static void killEntity(Level level, LivingEntity target, Entity source) {
        if (level.isClientSide() || target.isRemoved()) {
            return;
        }
        if (target instanceof Player player) {
            killPlayer(player, source);
            return;
        }
        DamageSource damage = level.damageSources().genericKill();
        target.hurt(damage, Float.MAX_VALUE);
        if (target.isAlive()) {
            target.setHealth(0.0F);
            target.discard();
        }
    }

    public static void killPlayer(Player player, Entity source) {
        Level level = player.level();
        if (level.isClientSide()) {
            return;
        }
        ItemStack pickaxe = getLoliPickaxe(player);
        if (Config.getBoolean(pickaxe, "loliPickaxeClearInventory")) {
            player.getInventory().clearContent();
        }
        player.hurt(level.damageSources().genericKill(), Float.MAX_VALUE);
        if (player.getHealth() > 0.0F) {
            player.setHealth(0.0F);
        }
        if (Config.getBoolean(pickaxe, "loliPickaxeKickPlayer") && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.disconnect(Component.literal(Config.getString(pickaxe, "loliPickaxeKickMessage")));
        }
    }

    public static int killRangeEntity(Level level, LivingEntity source, int range) {
        if (level.isClientSide()) {
            return 0;
        }
        int count = 0;
        AABB box = source.getBoundingBox().inflate(range);
        for (Entity entity : level.getEntities(source, box)) {
            if (entity instanceof LivingEntity living && entity != source) {
                if (living instanceof Player player && invHaveLoliPickaxe(player)) {
                    continue;
                }
                killEntity(level, living, source);
                count++;
            }
        }
        return count;
    }

    public static void killFacing(Player player) {
        Level level = player.level();
        if (level.isClientSide()) {
            return;
        }
        ItemStack stack = player.getMainHandItem();
        int range = Config.getInt(stack, "loliPickaxeKillFacingRange");
        double slope = Config.getDouble(stack, "loliPickaxeKillFacingSlope");
        Vec3 look = player.getLookAngle();
        Vec3 eye = player.getEyePosition();
        AABB box = player.getBoundingBox().inflate(range);
        for (Entity entity : level.getEntities(player, box)) {
            if (!(entity instanceof LivingEntity living) || entity == player) {
                continue;
            }
            Vec3 delta = entity.position().subtract(eye);
            double forward = delta.dot(look);
            if (forward <= 0.0D) {
                continue;
            }
            double perpendicular = delta.subtract(look.scale(forward)).length();
            if (perpendicular <= forward * slope + 0.5D) {
                killEntity(level, living, player);
            }
        }
    }

    /** Called when a pickaxe-wielding player attacks something. */
    public static boolean attackWithLoliPickaxe(Player player, Entity target, ItemStack stack) {
        if (player.level().isClientSide()) {
            return false;
        }
        boolean success = false;
        if (target instanceof Player other) {
            killPlayer(other, player);
            success = true;
        } else if (target instanceof LivingEntity living) {
            killEntity(player.level(), living, player);
            success = true;
        } else if (Config.getBoolean(stack, "loliPickaxeValidToAllEntity") && !(target instanceof LivingEntity)) {
            target.discard();
            success = true;
        }
        if (Config.getBoolean(stack, "loliPickaxeKillFacing")) {
            killFacing(player);
            success = true;
        }
        if (success) {
            ItemLoliPickaxe.playSuccess(player.level(), player.blockPosition());
        }
        return success;
    }

    /** Area mining used by the small pickaxe. */
    public static void mineArea(Player player, ItemStack stack, BlockPos origin) {
        if (!(stack.getItem() instanceof ItemSmallLoliPickaxe pickaxe)) {
            return;
        }
        int range = pickaxe.getRange(stack);
        if (range <= 0) {
            return;
        }
        Level level = player.level();
        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                for (int dz = -range; dz <= range; dz++) {
                    BlockPos pos = origin.offset(dx, dy, dz);
                    if (pos.equals(origin)) {
                        continue;
                    }
                    var state = level.getBlockState(pos);
                    if (state.isAir()) {
                        continue;
                    }
                    if (state.getDestroySpeed(level, pos) < 0.0F) {
                        continue;
                    }
                    level.destroyBlock(pos, !player.isCreative(), player);
                }
            }
        }
    }

    public static void collectDrops(Player player, List<ItemEntity> drops) {
        for (ItemEntity drop : drops) {
            drop.playerTouch(player);
        }
    }
}
