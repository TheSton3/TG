package com.thestone.tg.event;

import com.thestone.tg.Tg;
import com.thestone.tg.core.ModAttachments;
import com.thestone.tg.core.ModKeymappings;
import com.thestone.tg.ghoul.GhoulHunger;
import com.thestone.tg.ghoul.GhoulPlayer;
import com.thestone.tg.networking.packet.GhoulFeedPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = Tg.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void registerHUD(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, ResourceLocation.fromNamespaceAndPath(Tg.MOD_ID, "ghoul_hunger"), (guiGraphics, deltaTracker) -> {
            int x = guiGraphics.guiWidth() / 2;
            int y = guiGraphics.guiHeight();
            Player player = Minecraft.getInstance().player;
            GhoulHunger hunger = GhoulPlayer.get(player).getHunger();

            if (GhoulPlayer.get(player).isGhoul()) {
                for (int i = 0; i < 10; i++) {
                    guiGraphics.blitSprite(ResourceLocation.fromNamespaceAndPath(Tg.MOD_ID, "empty_hunger_icon"), 12, 12, 0, 0, x + i * 9, y - 41, 12, 12);
                }

                for (int i = 0; i < hunger.getHungerLevel() / 2; i++) {
                    guiGraphics.blitSprite(ResourceLocation.fromNamespaceAndPath(Tg.MOD_ID, "hunger_icon"), 12, 12, 0, 0, x + i * 9, y - 41, 12, 12);
                }
            }
        });
    }
    @SubscribeEvent
    public static void registerKeybind(RegisterKeyMappingsEvent event){
        event.register(ModKeymappings.PRESS_FEED.get());
    }
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        // Проверяем нажатие keybind
        while (ModKeymappings.PRESS_FEED.get().consumeClick()) {
            // Проверяем, смотрит ли игрок на валидную цель
            if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.ENTITY) {
                Entity target = ((EntityHitResult) mc.hitResult).getEntity();

                // Отправляем пакет только если цель — сущность
                if (target instanceof LivingEntity) {
                    sendFeedPacket(target.getId());
                }
            }
        }
    }

    private static void sendFeedPacket(int targetId) {
        var packet = new GhoulFeedPayload(targetId);
        // Отправка через NeoForge networking
        Minecraft.getInstance().getConnection().send(
                new ServerboundCustomPayloadPacket(packet)
        );
    }

}
