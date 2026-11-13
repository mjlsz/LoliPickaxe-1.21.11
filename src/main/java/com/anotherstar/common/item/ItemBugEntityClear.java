package com.anotherstar.common.item;

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
 * "清除只在客户端存在的生物" – removes ghost entities around the player.
 * The 1.12.2 version relied on the coremod to track mismatched entities; the port
 * simply clears every mod entity that the server does not know about.
 */
public class ItemBugEntityClear extends Item {

    public ItemBugEntityClear(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            int removed = 0;
            for (Entity entity : level.getEntities(player, new AABB(player.blockPosition()).inflate(32.0D))) {
                if (entity.getType().getDescriptionId().startsWith("entity.lolipickaxe.") && !entity.isAlive()) {
                    entity.discard();
                    removed++;
                }
            }
            player.displayClientMessage(Component.translatable("loliPickaxe.bugClear", removed), true);
        }
        return InteractionResult.SUCCESS;
    }
}
