package com.anotherstar.network;

import com.anotherstar.common.gui.LoliStorageMenu;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModNetwork {

    private ModNetwork() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(KillFacingPayload.TYPE, KillFacingPayload.STREAM_CODEC, (payload, context) -> {
            if (context.player() instanceof net.minecraft.server.level.ServerPlayer player) {
                context.enqueueWork(() -> com.anotherstar.util.LoliUtil.killFacing(player));
            }
        });
        registrar.playToServer(SetFlagPayload.TYPE, SetFlagPayload.STREAM_CODEC, (payload, context) -> {
            if (context.player() instanceof net.minecraft.server.level.ServerPlayer player) {
                context.enqueueWork(() -> {
                    var stack = player.getMainHandItem();
                    if (stack.getItem() instanceof com.anotherstar.common.item.ILoliStorageHolder) {
                        com.anotherstar.common.config.Config.set(stack, payload.flag(), payload.value());
                    }
                });
            }
        });
        registrar.playToServer(OpenStoragePayload.TYPE, OpenStoragePayload.STREAM_CODEC, (payload, context) -> {
            if (context.player() instanceof net.minecraft.server.level.ServerPlayer player) {
                context.enqueueWork(() -> {
                    if (player.getMainHandItem().getItem() instanceof com.anotherstar.common.item.ILoliStorageHolder) {
                        com.anotherstar.common.gui.LoliStorageMenu.open(player, player.getInventory().getSelectedSlot());
                    }
                });
            }
        });
        registrar.playToServer(ChangePagePayload.TYPE, ChangePagePayload.STREAM_CODEC, (payload, context) -> {
            if (context.player() instanceof net.minecraft.server.level.ServerPlayer player
                    && player.containerMenu instanceof LoliStorageMenu menu) {
                context.enqueueWork(() -> menu.changePage(payload.delta()));
            }
        });
    }
}


