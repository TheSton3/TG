package com.thestone.tg.event;

import com.thestone.tg.Tg;
import com.thestone.tg.core.ModAttachments;
import com.thestone.tg.ghoulhunger.HungerHandler;
import com.thestone.tg.networking.packet.HungerData;
import com.thestone.tg.networking.packet.ServerPayloadHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Tg.MOD_ID)
public class ModEvents {
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1")
                .executesOn(HandlerThread.MAIN);


        registrar.playToClient(HungerData.TYPE, HungerData.STREAM_CODEC, ServerPayloadHandler::handleHungerOnClient);
    }
    @SubscribeEvent
    public static void setPlayersHungerOnSpawn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        HungerHandler.setHungerForPlayer(player, 5);
    }


    @SubscribeEvent
    public static void setPlayersHungerOnClone(final PlayerEvent.Clone event) {
        ServerPlayer player = ((ServerPlayer) event.getEntity());
        HungerHandler.setHungerForPlayer(player, event.getOriginal().getData(ModAttachments.GHOUL_HUNGER));
    }

    @SubscribeEvent
    public static void setPlayersHungerOnDimensionChange(final PlayerEvent.PlayerChangedDimensionEvent event) {
        PacketDistributor.sendToPlayer(((ServerPlayer) event.getEntity()), new HungerData(0, event.getEntity().getData(ModAttachments.GHOUL_HUNGER)));
    }

    @SubscribeEvent
    public static void setPlayersHungerOnRespawn(final PlayerEvent.PlayerRespawnEvent event) {
        PacketDistributor.sendToPlayer(((ServerPlayer) event.getEntity()), new HungerData(0, event.getEntity().getData(ModAttachments.GHOUL_HUNGER)));
    }
}
