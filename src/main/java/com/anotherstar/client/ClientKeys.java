package com.anotherstar.client;

import com.anotherstar.network.OpenStoragePayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.common.NeoForge;

/** Registers the storage key and sends the open request. */
public final class ClientKeys {

    private ClientKeys() {
    }

    public static void init(IEventBus modBus) {
        modBus.addListener(ClientKeys::registerKeys);
        NeoForge.EVENT_BUS.addListener(ClientKeys::onClientTick);
    }

    private static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(Keys.OPEN_STORAGE);
        event.register(Keys.OPEN_CONFIG);
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        var minecraft = net.minecraft.client.Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }
        while (Keys.OPEN_CONFIG.consumeClick()) {
            if (minecraft.player.getMainHandItem().getItem() instanceof com.anotherstar.common.item.ILoliStorageHolder) {
                minecraft.setScreen(new com.anotherstar.client.gui.LoliConfigScreen());
            }
        }
        while (Keys.OPEN_STORAGE.consumeClick()) {
            if (minecraft.player.getMainHandItem().getItem() instanceof com.anotherstar.common.item.ILoliStorageHolder) {
                ClientPacketDistributor.sendToServer(OpenStoragePayload.INSTANCE);
            }
        }
    }
}

