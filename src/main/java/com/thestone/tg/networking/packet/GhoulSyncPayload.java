package com.thestone.tg.networking.packet;

import com.thestone.tg.Tg;
import com.thestone.tg.core.ModAttachments;
import com.thestone.tg.ghoul.GhoulPlayer;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record GhoulSyncPayload(int playerId,
                               boolean isGhoul,
                               int hungerLevel,
                               int maxHunger,
                               float saturation)
        implements CustomPacketPayload {




    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final CustomPacketPayload.Type<GhoulSyncPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Tg.MOD_ID, "ghoulsyncpayload"));


    public static final StreamCodec<ByteBuf, GhoulSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, GhoulSyncPayload::playerId,
                    ByteBufCodecs.BOOL, GhoulSyncPayload::isGhoul,
                    ByteBufCodecs.VAR_INT, GhoulSyncPayload::hungerLevel,
                    ByteBufCodecs.VAR_INT, GhoulSyncPayload::maxHunger,
                    ByteBufCodecs.FLOAT, GhoulSyncPayload::saturation,
                    GhoulSyncPayload::new
            );

    /**
     * Вызывается на клиенте при получении пакета.
     */
    public static void handle(final GhoulSyncPayload data, final IPayloadContext context) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        Entity entity = mc.level.getEntity(data.playerId());
        if (entity instanceof Player player) {
            GhoulPlayer ghoul = player.getData(ModAttachments.GHOUL_PLAYER);

            // Обновляем данные
            ghoul.setGhoul(data.isGhoul());
            ghoul.getHunger().setHungerLevel(data.hungerLevel());
            ghoul.getHunger().setMaxHunger(data.maxHunger());




        }
    }
}
