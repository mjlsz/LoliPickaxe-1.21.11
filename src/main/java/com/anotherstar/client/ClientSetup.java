package com.anotherstar.client;

import com.anotherstar.client.model.LoliModel;
import com.anotherstar.client.render.LoliRenderer;
import com.anotherstar.network.KillFacingPayload;
import com.anotherstar.registry.ModEntities;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/** Client-only wiring: entity renderer, model layer and the left-click packet. */
public final class ClientSetup {

    private ClientSetup() {
    }

    public static void init(IEventBus modBus) {
        modBus.addListener(ClientSetup::registerRenderers);
        modBus.addListener(ClientSetup::registerLayers);
        modBus.addListener(ClientSetup::registerScreens);
        ClientKeys.init(modBus);
        NeoForge.EVENT_BUS.addListener(ClientSetup::onLeftClickEmpty);
    }

    private static void registerScreens(net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
        event.<com.anotherstar.common.gui.LoliStorageMenu, com.anotherstar.client.gui.LoliStorageScreen>register(
                com.anotherstar.registry.ModMenus.LOLI_STORAGE.get(),
                (menu, inventory, title) -> new com.anotherstar.client.gui.LoliStorageScreen(menu, inventory, title));
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.LOLI.get(), LoliRenderer::new);
    }

    private static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(LoliRenderer.LAYER, LoliModel::createBodyLayer);
    }

    private static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        var player = event.getEntity();
        if (player.getMainHandItem().getItem() instanceof com.anotherstar.common.item.ItemLoliPickaxe
                && com.anotherstar.common.config.Config.getBoolean(player.getMainHandItem(), "loliPickaxeKillFacing")) {
            ClientPacketDistributor.sendToServer(KillFacingPayload.INSTANCE);
        }
    }
}

