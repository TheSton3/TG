package com.thestone.tg.mixin;

import com.thestone.tg.ghoul.GhoulPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiHungerMixin {


    @Inject(
            method = "renderFood",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onRenderFood(GuiGraphics guiGraphics, Player player, int y, int x, CallbackInfo ci) {
        if (player == null) return;


        if (GhoulPlayer.get(player).isGhoul()) {
            ci.cancel(); // Полностью отменяем ванильную отрисовку
        }
    }
}
