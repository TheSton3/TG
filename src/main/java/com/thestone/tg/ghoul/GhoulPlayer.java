package com.thestone.tg.ghoul;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thestone.tg.core.ModAttachments;
import com.thestone.tg.networking.packet.GhoulSyncPayload;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

public class GhoulPlayer  {

    public static final Codec<GhoulPlayer> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("isGhoul").forGetter(GhoulPlayer::isGhoul),
                    GhoulHunger.CODEC.fieldOf("hunger").forGetter(GhoulPlayer::getHunger)
            ).apply(instance, GhoulPlayer::new)
    );

    private boolean isGhoul;
    private GhoulHunger hunger;

    private GhoulPlayer(boolean isGhoul, GhoulHunger hunger) {
        this.isGhoul = isGhoul;
        this.hunger = hunger;
    }

    public static GhoulPlayer create(IAttachmentHolder holder) {
        if (!(holder instanceof Player player)) throw new IllegalArgumentException("Expected Player");
        return new GhoulPlayer(false, GhoulHunger.create(player));
    }

    public static GhoulPlayer get(Player player) {
        return player.getData(ModAttachments.GHOUL_PLAYER);
    }

    public boolean isGhoul() { return isGhoul; }
    public void setGhoul(boolean ghoul) { this.isGhoul = ghoul; }
    public GhoulHunger getHunger() { return hunger; }

    public boolean onUpdate(Player player) {
        if (player.level().isClientSide) return false;
        if (isGhoul) return hunger.onUpdate(player);
        return false;
    }

    public void syncToClient(Player player) {
        if (player instanceof ServerPlayer sp) {
            sp.connection.send(new ClientboundCustomPayloadPacket(
                    new GhoulSyncPayload(
                            sp.getId(), isGhoul,
                            hunger.getHungerLevel(), hunger.getMaxHunger(), hunger.getSaturation()
                    )
            ));
        }
    }
}