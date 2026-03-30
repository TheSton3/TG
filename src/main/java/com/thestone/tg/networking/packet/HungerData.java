package com.thestone.tg.networking.packet;

import com.thestone.tg.Tg;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record HungerData (int oldValue, int newValue) implements CustomPacketPayload {
    public static final Type<HungerData> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tg.MOD_ID, "hunger_data"));

    public static final StreamCodec<ByteBuf, HungerData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            HungerData::oldValue,

            ByteBufCodecs.VAR_INT,
            HungerData::newValue,

            HungerData::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
