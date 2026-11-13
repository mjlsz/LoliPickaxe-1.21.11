package com.anotherstar.common.item;

import com.anotherstar.util.LoliData;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/**
 * An upgrade material ("升级"). Levels are stored as {@code level} in custom data,
 * mirroring the 1.12.2 metadata damage value.
 */
public class ItemAddon extends Item {

    private final String baseName;
    private final int maxLevel;
    private final boolean differentEnd;

    public ItemAddon(Properties properties, String baseName, int maxLevel, boolean differentEnd) {
        super(properties);
        this.baseName = baseName;
        this.maxLevel = maxLevel;
        this.differentEnd = differentEnd;
    }

    public int maxLevel() {
        return maxLevel;
    }

    public int level(ItemStack stack) {
        return LoliData.getInt(stack, "level", 0);
    }

    public ItemStack withLevel(int level) {
        ItemStack stack = new ItemStack(this);
        LoliData.setInt(stack, "level", level);
        return stack;
    }

    @Override
    public Component getName(ItemStack stack) {
        int level = level(stack);
        Component base = Component.translatable(this.getDescriptionId());
        if (maxLevel <= 1) {
            return base;
        }
        String levelKey = differentEnd && level == maxLevel - 1 ? "item.loliMaterial.end" : "item.loliMaterial." + level;
        return Component.translatable("item.loliMaterialFormat", Component.translatable(levelKey), base).withStyle(ChatFormatting.RED);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        int level = level(stack);
        if (level < maxLevel - 1) {
            String next = level == maxLevel - 2 ? "item.loliMaterial.end" : "item.loliMaterial." + (level + 1);
            tooltip.accept(Component.translatable("item.loliMaterial.recipe",
                    Component.translatable("item.loliMaterial." + level),
                    Component.translatable(next)));
        }
    }
}
