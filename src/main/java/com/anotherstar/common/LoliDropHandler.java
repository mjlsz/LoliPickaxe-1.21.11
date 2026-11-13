package com.anotherstar.common;

import com.anotherstar.common.config.Config;
import com.anotherstar.common.item.ItemSmallLoliPickaxe;
import com.anotherstar.registry.ModItems;
import com.anotherstar.util.LoliData;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

/** Fortune / auto-smelting / rare drops for the mod's tools. */
public final class LoliDropHandler {

    private LoliDropHandler() {
    }

    @SubscribeEvent
    public static void onBlockDrops(BlockDropsEvent event) {
        ItemStack tool = event.getTool();
        if (!(tool.getItem() instanceof ItemSmallLoliPickaxe pickaxe)) {
            return;
        }
        int fortune = LoliData.has(tool, "LoliFortuneLevel")
                ? pickaxe.getTransformValue("LoliFortuneLevel", LoliData.getInt(tool, "LoliFortuneLevel", 0))
                : 0;
        boolean autoFurnace = LoliData.has(tool, "LoliAutoFurnace")
                && pickaxe.getTransformValue("LoliAutoFurnace", LoliData.getInt(tool, "LoliAutoFurnace", 0)) == 0;

        for (ItemEntity drop : event.getDrops()) {
            ItemStack stack = drop.getItem();
            if (autoFurnace) {
                ItemStack smelted = smelt(event, stack);
                if (!smelted.isEmpty()) {
                    drop.setItem(smelted);
                    stack = smelted;
                }
            }
            if (fortune > 0 && !stack.isEmpty()) {
                int bonus = event.getLevel().getRandom().nextInt(fortune + 1);
                stack.setCount(Math.min(stack.getCount() + bonus, stack.getMaxStackSize()));
            }
        }
    }

    private static ItemStack smelt(BlockDropsEvent event, ItemStack input) {
        if (input.isEmpty()) {
            return ItemStack.EMPTY;
        }
        SingleRecipeInput recipeInput = new SingleRecipeInput(input);
        return event.getLevel().getServer().getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, recipeInput, event.getLevel())
                .map(holder -> holder.value().assemble(recipeInput, event.getLevel().registryAccess()))
                .orElse(ItemStack.EMPTY);
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        var source = event.getSource().getEntity();
        if (!(source instanceof net.minecraft.world.entity.player.Player player)) {
            return;
        }
        ItemStack tool = player.getMainHandItem();
        if (!(tool.getItem() instanceof com.anotherstar.common.item.ItemLoliPickaxe)) {
            return;
        }
        var random = event.getEntity().level().getRandom();
        if (random.nextDouble() < Config.ENTITY_SOUL_DROP_PROBABILITY.get()) {
            addDrop(event, new ItemStack(ModItems.ENTITY_SOUL_ADDON.get()));
        }
        if (random.nextDouble() < Config.CARD_DROP_PROBABILITY.get()) {
            addDrop(event, new ItemStack(ModItems.LOLI_CARD.get()));
        }
        if (random.nextDouble() < Config.RECORD_DROP_PROBABILITY.get()) {
            addDrop(event, new ItemStack(ModItems.LOLI_RECORD.get()));
        }
    }

    private static void addDrop(LivingDropsEvent event, ItemStack stack) {
        var entity = event.getEntity();
        event.getDrops().add(new ItemEntity(entity.level(), entity.getX(), entity.getY() + 0.5D, entity.getZ(), stack));
    }
}
