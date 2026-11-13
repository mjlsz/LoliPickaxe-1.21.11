package com.anotherstar.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** "萝莉唱片" – a music disc. The song itself is shipped as an ogg asset. */
public class ItemLoliRecord extends Item {

    public ItemLoliRecord(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId()).withStyle(ChatFormatting.RED);
    }
}
