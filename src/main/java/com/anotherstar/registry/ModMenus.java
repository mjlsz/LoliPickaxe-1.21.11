package com.anotherstar.registry;

import com.anotherstar.LoliPickaxe;
import com.anotherstar.common.gui.LoliStorageMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(net.minecraft.core.registries.Registries.MENU, LoliPickaxe.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<LoliStorageMenu>> LOLI_STORAGE =
            MENUS.register("loli_storage", () -> IMenuTypeExtension.create(
                    (id, inventory, buf) -> new LoliStorageMenu(id, inventory, buf.readVarInt())));

    public static void register(IEventBus modBus) {
        MENUS.register(modBus);
    }

    private ModMenus() {
    }
}
