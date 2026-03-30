package com.thestone.tg.ghoulhunger;

import com.thestone.tg.networking.packet.HungerData;
import com.thestone.tg.core.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;


public class HungerHandler {
    public static void setHungerForPlayer(Player player, int value) {
        player.setData(ModAttachments.GHOUL_HUNGER, value);
        PacketDistributor.sendToPlayer(((ServerPlayer) player), new HungerData(0, value));
    }

    public static void increaseHungerForPlayer(Player player, int value) {
        int newValue = player.getData(ModAttachments.GHOUL_HUNGER) + value;
        player.setData(ModAttachments.GHOUL_HUNGER, newValue);
        PacketDistributor.sendToPlayer(((ServerPlayer) player), new HungerData(0, newValue));
    }

    public static void decreaseHungerForPlayer(Player player, int value) {
        int newValue = player.getData(ModAttachments.GHOUL_HUNGER) - value;
        player.setData(ModAttachments.GHOUL_HUNGER, newValue);
        PacketDistributor.sendToPlayer(((ServerPlayer) player), new HungerData(0, newValue));
    }
}
