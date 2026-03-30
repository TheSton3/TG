package com.thestone.tg.event;

import com.thestone.tg.Tg;
import com.thestone.tg.core.ModAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = Tg.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void registerHUD(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, ResourceLocation.fromNamespaceAndPath(Tg.MOD_ID, "ghoul_hunger"),
                (guiGraphics, deltaTracker) -> {
                    int x = guiGraphics.guiWidth() / 2;
                    int y = guiGraphics.guiHeight();

                    if(!Minecraft.getInstance().player.isCreative() && Minecraft.getInstance().player.hasData(ModAttachments.GHOUL_HUNGER)) {
                        for(int i = 0; i < 10; i++) {
                            guiGraphics.blitSprite(ResourceLocation.fromNamespaceAndPath(Tg.MOD_ID, "empty_hunger_icon"),
                                    12, 12, 0, 0, x + i * 9 , y - 41, 12, 12);
                        }

                        for(int i = 0; i < Minecraft.getInstance().player.getData(ModAttachments.GHOUL_HUNGER); i++) {
                            guiGraphics.blitSprite(ResourceLocation.fromNamespaceAndPath(Tg.MOD_ID, "hunger_icon"),
                                    12, 12, 0, 0, x + i * 9, y - 41, 12, 12);
                        }
                    }
                });
    }
}
