package com.anotherstar.network;

import com.anotherstar.LoliPickaxe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Sent when the player left-clicks the air with a Loli Pickaxe. */
public record KillFacingPayload() implements CustomPacketPayload {

    public static final KillFacingPayload INSTANCE = new KillFacingPayload();
    public static final Type<KillFacingPayload> TYPE = new Type<>(LoliPickaxe.id("kill_facing"));
    public static final StreamCodec<RegistryFriendlyByteBuf, KillFacingPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
