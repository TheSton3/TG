package com.thestone.tg.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.thestone.tg.Tg;
import com.thestone.tg.core.ModAttachments;
import com.thestone.tg.core.ModKeymappings;
import com.thestone.tg.ghoul.GhoulHunger;
import com.thestone.tg.ghoul.GhoulPlayer;
import com.thestone.tg.item.armor.RinkakuKagune;
import com.thestone.tg.networking.packet.GhoulFeedPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.network.PacketDistributor;


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
    public static void registerFeedIndicator(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, ResourceLocation.fromNamespaceAndPath(Tg.MOD_ID, "ghoul_feed"), (guiGraphics, deltaTracker) -> {
            int x = guiGraphics.guiWidth() / 2;
            int y = guiGraphics.guiHeight() / 2;
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (mc.hitResult instanceof EntityHitResult ehr) {
                Entity target = ehr.getEntity();
                if (GhoulPlayer.get(player).isGhoul()) {
                    if (target instanceof LivingEntity le) {
                        if (GhoulFeedPayload.isValidTarget(le, player)) {
                            guiGraphics.blitSprite(ResourceLocation.fromNamespaceAndPath(Tg.MOD_ID, "feed_icon"), 16, 16, 0, 0, x, y, 16, 16);
                        }
                    }
                }
            }
        });
    }

    @SubscribeEvent
    public static void registerKeybind(RegisterKeyMappingsEvent event) {
        event.register(ModKeymappings.PRESS_FEED.get());
        event.register(ModKeymappings.PRESS_KAGUNE.get());
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        while (ModKeymappings.PRESS_KAGUNE.get().consumeClick()){
            RinkakuKagune.toggleKagune(mc.player);
        }
        // Проверяем нажатие keybind
        while (ModKeymappings.PRESS_FEED.get().consumeClick()) {
            // Проверяем, смотрит ли игрок на валидную цель
            if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.ENTITY) {
                Entity target = ((EntityHitResult) mc.hitResult).getEntity();

                // Отправляем пакет только если цель — сущность
                if (target instanceof LivingEntity) {
                    PacketDistributor.sendToServer(new GhoulFeedPayload(target.getId()));
                }
            }
        }
    }


}
