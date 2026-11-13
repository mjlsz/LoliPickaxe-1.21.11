package com.anotherstar.common.item;

import com.anotherstar.util.LoliData;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/** "萝莉网络卡片" – the online card simply stores a URL in custom data. */
public class ItemLoliCardOnline extends Item {

    public ItemLoliCardOnline(Properties properties) {
        super(properties);
    }

    public String getUrl(ItemStack stack) {
        return LoliData.getString(stack, "Url", "");
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        String url = getUrl(stack);
        tooltip.accept(Component.translatable("gui.loliCardOnline").append(": ").append(url).withStyle(ChatFormatting.GRAY));
    }
}
