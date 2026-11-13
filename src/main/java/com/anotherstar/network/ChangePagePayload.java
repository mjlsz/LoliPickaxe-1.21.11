package com.anotherstar.network;

import com.anotherstar.LoliPickaxe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Client asks the server to flip a page in the storage GUI. */
public record ChangePagePayload(int delta) implements CustomPacketPayload {

    public static final Type<ChangePagePayload> TYPE = new Type<>(LoliPickaxe.id("change_page"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ChangePagePayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.VAR_INT, ChangePagePayload::delta, ChangePagePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
