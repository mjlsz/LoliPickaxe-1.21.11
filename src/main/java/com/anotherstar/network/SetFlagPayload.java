package com.anotherstar.network;

import com.anotherstar.LoliPickaxe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Client toggles one per-item configuration flag on the held pickaxe. */
public record SetFlagPayload(String flag, boolean value) implements CustomPacketPayload {

    public static final Type<SetFlagPayload> TYPE = new Type<>(LoliPickaxe.id("set_flag"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetFlagPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SetFlagPayload::flag,
            ByteBufCodecs.BOOL, SetFlagPayload::value,
            SetFlagPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
