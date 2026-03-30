package com.thestone.tg.mixin;

import com.thestone.tg.util.Util;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiHungerMixin {


    @Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
    private void hideVanillaHunger(GuiGraphics p_335615_, Player player, int p_335399_, int p_335589_, CallbackInfo ci) {

        if(Util.isGhoul(player)){
            ci.cancel();
        }

    }
}
