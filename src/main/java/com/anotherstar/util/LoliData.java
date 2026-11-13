package com.anotherstar.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * NBT helper for the port. 1.12.2 stored everything in {@code ItemStack#getTagCompound()};
 * 1.21 stores it in the {@link DataComponents#CUSTOM_DATA} component. This class keeps the
 * old read/write style so the original logic could be ported almost line by line.
 */
public final class LoliData {

    private LoliData() {
    }

    public static CompoundTag get(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? new CompoundTag() : data.copyTag();
    }

    public static void set(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static boolean has(ItemStack stack, String key) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null && data.copyTag().contains(key);
    }

    public static CompoundTag mutate(ItemStack stack, java.util.function.Consumer<CompoundTag> action) {
        CompoundTag tag = get(stack);
        action.accept(tag);
        set(stack, tag);
        return tag;
    }

    public static int getInt(ItemStack stack, String key, int def) {
        CompoundTag tag = get(stack);
        return tag.contains(key) ? tag.getIntOr(key, def) : def;
    }

    public static void setInt(ItemStack stack, String key, int value) {
        mutate(stack, tag -> tag.putInt(key, value));
    }

    public static double getDouble(ItemStack stack, String key, double def) {
        CompoundTag tag = get(stack);
        return tag.contains(key) ? tag.getDoubleOr(key, def) : def;
    }

    public static void setDouble(ItemStack stack, String key, double value) {
        mutate(stack, tag -> tag.putDouble(key, value));
    }

    public static long getLong(ItemStack stack, String key, long def) {
        CompoundTag tag = get(stack);
        return tag.contains(key) ? tag.getLongOr(key, def) : def;
    }

    public static void setLong(ItemStack stack, String key, long value) {
        mutate(stack, tag -> tag.putLong(key, value));
    }

    public static boolean getBoolean(ItemStack stack, String key, boolean def) {
        CompoundTag tag = get(stack);
        return tag.contains(key) ? tag.getBooleanOr(key, def) : def;
    }

    public static void setBoolean(ItemStack stack, String key, boolean value) {
        mutate(stack, tag -> tag.putBoolean(key, value));
    }

    public static String getString(ItemStack stack, String key, String def) {
        CompoundTag tag = get(stack);
        return tag.contains(key) ? tag.getStringOr(key, def) : def;
    }

    public static void setString(ItemStack stack, String key, String value) {
        mutate(stack, tag -> tag.putString(key, value));
    }

    public static ListTag getList(ItemStack stack, String key) {
        CompoundTag tag = get(stack);
        return tag.getListOrEmpty(key);
    }

    public static void setList(ItemStack stack, String key, ListTag list) {
        mutate(stack, tag -> tag.put(key, list));
    }

    public static CompoundTag getCompound(ItemStack stack, String key) {
        CompoundTag tag = get(stack);
        return tag.getCompoundOrEmpty(key);
    }

    public static void setCompound(ItemStack stack, String key, CompoundTag value) {
        mutate(stack, tag -> tag.put(key, value));
    }

    public static boolean isEmptyTag(Tag tag) {
        return tag == null || tag instanceof CompoundTag c && c.isEmpty();
    }
}
