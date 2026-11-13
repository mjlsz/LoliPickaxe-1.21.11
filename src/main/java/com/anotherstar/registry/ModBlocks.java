package com.anotherstar.registry;

import com.anotherstar.LoliPickaxe;
import com.anotherstar.common.block.BlockLoliTNT;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(LoliPickaxe.MODID);

    public static final DeferredBlock<BlockLoliTNT> BLUE_SCREEN_TNT = BLOCKS.registerBlock("loli_blue_screen_tnt",
            props -> new BlockLoliTNT(props, BlockLoliTNT.Variant.BLUE_SCREEN), tntProperties());
    public static final DeferredBlock<BlockLoliTNT> EXIT_TNT = BLOCKS.registerBlock("loli_exit_tnt",
            props -> new BlockLoliTNT(props, BlockLoliTNT.Variant.EXIT), tntProperties());
    public static final DeferredBlock<BlockLoliTNT> FAIL_RESPOND_TNT = BLOCKS.registerBlock("loli_fail_respond_tnt",
            props -> new BlockLoliTNT(props, BlockLoliTNT.Variant.FAIL_RESPOND), tntProperties());

    public static final DeferredItem<BlockItem> BLUE_SCREEN_TNT_ITEM = ModItems.ITEMS.registerSimpleBlockItem(BLUE_SCREEN_TNT);
    public static final DeferredItem<BlockItem> EXIT_TNT_ITEM = ModItems.ITEMS.registerSimpleBlockItem(EXIT_TNT);
    public static final DeferredItem<BlockItem> FAIL_RESPOND_TNT_ITEM = ModItems.ITEMS.registerSimpleBlockItem(FAIL_RESPOND_TNT);

    private static BlockBehaviour.Properties tntProperties() {
        return BlockBehaviour.Properties.of().strength(0.0F, 0.0F).sound(SoundType.GRASS);
    }

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
    }

    private ModBlocks() {
    }
}
