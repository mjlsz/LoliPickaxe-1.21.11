package com.anotherstar.network;

import com.anotherstar.LoliPickaxe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Client asks the server to open the storage GUI of the held pickaxe. */
public record OpenStoragePayload() implements CustomPacketPayload {

    public static final OpenStoragePayload INSTANCE = new OpenStoragePayload();
    public static final Type<OpenStoragePayload> TYPE = new Type<>(LoliPickaxe.id("open_storage"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenStoragePayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
