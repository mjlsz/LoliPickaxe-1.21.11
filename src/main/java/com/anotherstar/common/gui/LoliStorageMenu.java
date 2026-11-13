package com.anotherstar.common.gui;

import com.anotherstar.common.item.ILoliStorageHolder;
import com.anotherstar.registry.ModMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** Paginated backpack GUI of the Small Loli Pickaxe. */
public class LoliStorageMenu extends AbstractContainerMenu {

    private final Inventory playerInventory;
    private final LoliStorage storage;
    private final int itemSlot;
    private int page;

    public LoliStorageMenu(int id, Inventory playerInventory, int itemSlot) {
        super(ModMenus.LOLI_STORAGE.get(), id);
        this.playerInventory = playerInventory;
        this.itemSlot = itemSlot;
        ItemStack holder = playerInventory.getItem(itemSlot);
        int pages = holder.getItem() instanceof ILoliStorageHolder holderItem ? Math.max(1, holderItem.storagePages(holder)) : 1;
        this.storage = new LoliStorage(holder, pages, playerInventory.player.level().registryAccess());
        this.page = 0;
        this.addSlots();
        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return page;
            }

            @Override
            public void set(int value) {
                if (value != page) {
                    page = Math.max(0, Math.min(value, storage.pages() - 1));
                    rebuildSlots();
                }
            }
        });
    }

    public LoliStorage storage() {
        return storage;
    }

    public int page() {
        return page;
    }

    public int pages() {
        return storage.pages();
    }

    private void addSlots() {
        for (int row = 0; row < LoliStorage.ROWS; row++) {
            for (int col = 0; col < LoliStorage.COLUMNS; col++) {
                int index = page * LoliStorage.PAGE_SIZE + row * LoliStorage.COLUMNS + col;
                this.addSlot(new Slot(storage, index, 8 + col * 18, 18 + row * 18));
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 140 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 198));
        }
    }

    private void rebuildSlots() {
        this.slots.clear();
        this.addSlots();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            copy = stack.copy();
            int containerSlots = LoliStorage.PAGE_SIZE;
            if (index < containerSlots) {
                if (!this.moveItemStackTo(stack, containerSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, containerSlots, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getInventory().getItem(itemSlot).getItem() instanceof ILoliStorageHolder;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.storage.save();
    }

    /** Server-side page switch; the client receives the new page through the data slot. */
    public void changePage(int delta) {
        int next = Math.max(0, Math.min(this.page + delta, pages() - 1));
        if (next != this.page) {
            this.page = next;
            this.rebuildSlots();
            this.broadcastFullState();
        }
    }

    /** Server-side helper: opens this menu for the pickaxe in the given inventory slot. */
    public static void open(net.minecraft.world.entity.player.Player player, int slot) {
        ((net.neoforged.neoforge.common.extensions.IPlayerExtension) player).openMenu(
                new net.minecraft.world.SimpleMenuProvider(
                        (id, inventory, p) -> new LoliStorageMenu(id, inventory, slot),
                        net.minecraft.network.chat.Component.translatable("container.loliPickaxe")),
                buf -> buf.writeVarInt(slot));
    }
}
