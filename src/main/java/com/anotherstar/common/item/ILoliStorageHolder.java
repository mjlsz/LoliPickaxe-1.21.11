package com.anotherstar.common.item;

import net.minecraft.world.item.ItemStack;

/** Implemented by items that expose the paginated storage GUI. */
public interface ILoliStorageHolder {

    int storagePages(ItemStack stack);
}
