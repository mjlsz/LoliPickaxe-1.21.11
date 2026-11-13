package com.anotherstar.common;

import com.anotherstar.common.item.ItemAddon;
import com.anotherstar.util.LoliData;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * "九个同级升级合成一个更高级升级" (nine same-level addons make the next level).
 *
 * <p>1.12.2 did this with a custom {@code SuperpositionRecipe}. Instead of porting the
 * codec based recipe API, the port registers a plain 3x3 shapeless recipe per addon and
 * fixes the resulting level here, using the crafting matrix that the event provides.</p>
 */
public final class AddonCraftingHandler {

    private AddonCraftingHandler() {
    }

    @SubscribeEvent
    public static void onCrafted(PlayerEvent.ItemCraftedEvent event) {
        ItemStack result = event.getCrafting();
        if (!(result.getItem() instanceof ItemAddon addon)) {
            return;
        }
        Container matrix = event.getInventory();
        int found = 0;
        int level = -1;
        for (int i = 0; i < matrix.getContainerSize(); i++) {
            ItemStack stack = matrix.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() != addon) {
                result.setCount(0);
                return;
            }
            int stackLevel = addon.level(stack);
            if (level == -1) {
                level = stackLevel;
            } else if (level != stackLevel) {
                result.setCount(0);
                return;
            }
            found++;
        }
        if (found != 9 || level < 0) {
            return;
        }
        if (level >= addon.maxLevel() - 1) {
            result.setCount(0);
            return;
        }
        LoliData.setInt(result, "level", level + 1);
    }
}
