package com.anotherstar.common.item;

import com.anotherstar.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/**
 * "萝莉退散!" – removes every Loli entity around the player. In 1.12.2 this item
 * iterated over {@code EntityLoli} and set them dead; the port uses
 * {@link Entity#discard()}.
 */
public class ItemLoliDispersal extends Item {

    public ItemLoliDispersal(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        BlockPos pos = player.blockPosition();
        int removed = 0;
        for (Entity entity : level.getEntities(player, new AABB(pos).inflate(64.0D))) {
            if (entity.getType() == ModEntities.LOLI.get()) {
                entity.discard();
                removed++;
            }
        }
        player.displayClientMessage(Component.translatable("loliPickaxe.dispersal", removed), true);
        if (!player.isCreative()) {
            player.getItemInHand(hand).shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
}
