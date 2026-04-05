package com.thestone.tg.networking.packet;

import com.thestone.tg.Tg;
import com.thestone.tg.core.ModAttachments;
import com.thestone.tg.ghoul.GhoulHungerStats;
import com.thestone.tg.ghoul.GhoulPlayer;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record SyncGhoulDataPacket(
        UUID targetPlayer,
        boolean isGhoul,
        float hungerLevel,
        float maxHunger,
        long lastTick,
        long transformTime)
        implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncGhoulDataPacket> TYPE
            = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Tg.MOD_ID, "sync_ghoul"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static final StreamCodec<ByteBuf, UUID> UUID_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG, u -> u.getMostSignificantBits(),
            ByteBufCodecs.VAR_LONG, u -> u.getLeastSignificantBits(),
            UUID::new
    );

    public static final StreamCodec<ByteBuf, SyncGhoulDataPacket> STREAM_CODEC = StreamCodec.composite(
            UUID_CODEC, SyncGhoulDataPacket::targetPlayer,
            ByteBufCodecs.BOOL, SyncGhoulDataPacket::isGhoul,
            ByteBufCodecs.FLOAT, SyncGhoulDataPacket::hungerLevel,
            ByteBufCodecs.FLOAT, SyncGhoulDataPacket::maxHunger,
            ByteBufCodecs.VAR_LONG, SyncGhoulDataPacket::lastTick,
            ByteBufCodecs.VAR_LONG, SyncGhoulDataPacket::transformTime,
            SyncGhoulDataPacket::new
    );


    public static void handle(SyncGhoulDataPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.player.getUUID().equals(packet.targetPlayer())) {
                GhoulHungerStats hungerStats = new GhoulHungerStats(
                        packet.hungerLevel(), packet.maxHunger(), packet.lastTick()
                );
                GhoulPlayer ghoulData = new GhoulPlayer(
                        packet.isGhoul(), hungerStats, packet.transformTime()
                );
                // Обновляем клиентский attachment
                mc.player.setData(ModAttachments.GHOUL_PLAYER, ghoulData);
            }
        });
    }
}
