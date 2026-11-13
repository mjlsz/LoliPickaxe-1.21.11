package com.anotherstar.registry;

import com.anotherstar.LoliPickaxe;
import com.anotherstar.common.item.ItemAddon;
import com.anotherstar.common.item.ItemSmallLoliPickaxe;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModTabs {

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LoliPickaxe.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> LOLI = TABS.register("loli",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                    .title(Component.translatable("itemGroup.loli"))
                    .icon(() -> new ItemStack(ModItems.ENTITY_SOUL_ADDON.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.LOLI_PICKAXE.get());
                        output.accept(ItemSmallLoliPickaxe.createFull());
                        output.accept(ModItems.LOLI_DISPERSAL.get());
                        output.accept(ModItems.BUG_ENTITY_CLEAR.get());
                        output.accept(ModItems.LOLI_CARD.get());
                        output.accept(ModItems.LOLI_CARD_ALBUM.get());
                        output.accept(ModItems.LOLI_CARD_ONLINE.get());
                        output.accept(ModItems.LOLI_RECORD.get());
                        output.accept(ModBlocks.BLUE_SCREEN_TNT_ITEM.get());
                        output.accept(ModBlocks.EXIT_TNT_ITEM.get());
                        output.accept(ModBlocks.FAIL_RESPOND_TNT_ITEM.get());
                    })
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> LOLI_RECIPE = TABS.register("loli_recipe",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 1)
                    .title(Component.translatable("itemGroup.loliRecipe"))
                    .icon(() -> new ItemStack(ModItems.LOLI_PICKAXE.get()))
                    .displayItems((params, output) -> {
                        for (DeferredItem<ItemAddon> addon : ModItems.addonItems()) {
                            for (int level = 0; level < addon.get().maxLevel(); level++) {
                                output.accept(addon.get().withLevel(level));
                            }
                        }
                    })
                    .build());

    public static void register(IEventBus modBus) {
        TABS.register(modBus);
    }

    private ModTabs() {
    }
}

