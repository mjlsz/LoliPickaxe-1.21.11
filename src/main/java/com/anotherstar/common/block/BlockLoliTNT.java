package com.anotherstar.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

/**
 * "蓝屏炸弹 / 蹦溃炸弹 / 未响应炸弹".
 *
 * <p>The 1.12.2 blocks extended vanilla TNT and, on detonation, sent a packet that
 * forced a fake crash screen on every nearby client. That is intentionally not
 * reproduced; the blocks now detonate on flint &amp; steel and kill everything in a
 * large radius, which is what the original did to entities before the client crash.</p>
 */
public class BlockLoliTNT extends Block {

    public enum Variant {
        BLUE_SCREEN("loli_blue_screen_tnt"),
        EXIT("loli_exit_tnt"),
        FAIL_RESPOND("loli_fail_respond_tnt");

        private final String id;

        Variant(String id) {
            this.id = id;
        }

        public String id() {
            return id;
        }
    }

    private final Variant variant;

    public BlockLoliTNT(Properties properties, Variant variant) {
        super(properties);
        this.variant = variant;
    }

    public Variant variant() {
        return variant;
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return simpleCodec(BlockLoliTNT::new);
    }

    private BlockLoliTNT(Properties properties) {
        this(properties, Variant.BLUE_SCREEN);
    }

    @Override
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!stack.is(Items.FLINT_AND_STEEL) && !stack.is(Items.FIRE_CHARGE)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            detonate(level, pos);
        }
        return InteractionResult.SUCCESS;
    }

    private void detonate(Level level, BlockPos pos) {
        level.removeBlock(pos, false);
        float power = variant == Variant.FAIL_RESPOND ? 8.0F : 6.0F;
        level.explode(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, power, Level.ExplosionInteraction.TNT);
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(power * 2.0D))) {
            if (!(entity instanceof Player)) {
                entity.hurt(level.damageSources().magic(), Float.MAX_VALUE);
            }
        }
        level.playSound(null, pos, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 2.0F, 0.8F);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, net.minecraft.world.level.block.entity.BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
        if (!level.isClientSide()) {
            player.displayClientMessage(Component.translatable("tile." + variant.id() + ".name"), true);
        }
    }
}
