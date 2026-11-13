package com.anotherstar.common.gui;

import com.anotherstar.util.LoliData;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Backpack storage of the Small Loli Pickaxe (and the Loli Pickaxe).
 *
 * <p>1.12.2 used {@code InventoryLoliBase} + an NBT list; the port keeps the same
 * layout ({@code Inventory: [{Slot, Item}, ...]}) but serializes with the 1.21
 * {@link ItemStack#CODEC}.</p>
 */
public class LoliStorage implements Container {

    public static final int COLUMNS = 9;
    public static final int ROWS = 6;
    public static final int PAGE_SIZE = COLUMNS * ROWS;

    private final ItemStack holder;
    private final HolderLookup.Provider registries;
    private final ItemStack[] items;
    private final int pages;

    public LoliStorage(ItemStack holder, int pages, HolderLookup.Provider registries) {
        this.holder = holder;
        this.pages = Math.max(1, pages);
        this.registries = registries;
        this.items = new ItemStack[this.pages * PAGE_SIZE];
        java.util.Arrays.fill(this.items, ItemStack.EMPTY);
        load();
    }

    public int pages() {
        return pages;
    }

    private void load() {
        ListTag list = LoliData.getList(holder, "Inventory");
        if (list.isEmpty()) {
            return;
        }
        RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registries);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompoundOrEmpty(i);
            int slot = entry.getIntOr("Slot", -1);
            if (slot < 0 || slot >= items.length) {
                continue;
            }
            ItemStack.CODEC.parse(ops, entry.getCompoundOrEmpty("Item"))
                    .result()
                    .ifPresent(stack -> items[slot] = stack);
        }
    }

    public void save() {
        RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registries);
        ListTag list = new ListTag();
        for (int i = 0; i < items.length; i++) {
            ItemStack stack = items[i];
            if (stack.isEmpty()) {
                continue;
            }
            CompoundTag entry = new CompoundTag();
            entry.putInt("Slot", i);
            ItemStack.CODEC.encodeStart(ops, stack)
                    .result()
                    .ifPresent(tag -> entry.put("Item", tag));
            list.add(entry);
        }
        LoliData.setList(holder, "Inventory", list);
    }

    @Override
    public int getContainerSize() {
        return items.length;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot >= 0 && slot < items.length ? items[slot] : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot < 0 || slot >= items.length || items[slot].isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = items[slot];
        ItemStack split = stack.split(amount);
        if (stack.isEmpty()) {
            items[slot] = ItemStack.EMPTY;
        }
        this.setChanged();
        return split;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot < 0 || slot >= items.length) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = items[slot];
        items[slot] = ItemStack.EMPTY;
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot < 0 || slot >= items.length) {
            return;
        }
        items[slot] = stack;
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    @Override
    public void setChanged() {
        save();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        java.util.Arrays.fill(items, ItemStack.EMPTY);
        this.setChanged();
    }
}
