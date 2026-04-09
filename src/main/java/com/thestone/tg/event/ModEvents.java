package com.thestone.tg.event;

import com.thestone.tg.Tg;
import com.thestone.tg.core.ModAttachments;
import com.thestone.tg.ghoul.GhoulPlayer;
import com.thestone.tg.networking.packet.GhoulFeedPayload;
import com.thestone.tg.networking.packet.GhoulSyncPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import top.theillusivec4.curios.api.extensions.RegisterCuriosExtensionsEvent;

@EventBusSubscriber(modid = Tg.MOD_ID)
public class ModEvents {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {

    }
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1")
                .executesOn(HandlerThread.MAIN);

        registrar.playToClient(
                GhoulSyncPayload.TYPE,
                GhoulSyncPayload.STREAM_CODEC,
                GhoulSyncPayload::handle);
        registrar.playToServer(
                GhoulFeedPayload.TYPE,
                GhoulFeedPayload.STREAM_CODEC,
                GhoulFeedPayload::handle);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        GhoulPlayer ghoul = GhoulPlayer.get(player);
        boolean updated = ghoul.onUpdate(player);
        if (updated) {
            ghoul.syncToClient(player);
        }
    }

    //Синхронизация при входе в мир (первый логин)
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;
        GhoulPlayer.get(player).syncToClient(player);
    }

    //Синхронизация при смене измерения
    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;
        GhoulPlayer.get(player).syncToClient(player);
    }

    //Синхронизация после респавна
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        GhoulPlayer.get(player).syncToClient(player);
    }

    //Копирование данных при смерти
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            Player original = event.getOriginal();
            Player newPlayer = event.getEntity();

            if (original.hasData(ModAttachments.GHOUL_PLAYER)) {
                GhoulPlayer orig = GhoulPlayer.get(original);
                GhoulPlayer copy = GhoulPlayer.get(newPlayer);

                copy.setGhoul(orig.isGhoul());
                // Сохраняем текущий голод (или уменьшаем на 5 для баланса)
                copy.getHunger().setHungerLevel(
                        Math.max(10, orig.getHunger().getHungerLevel() - 5)
                );
            }
        }
    }
}
