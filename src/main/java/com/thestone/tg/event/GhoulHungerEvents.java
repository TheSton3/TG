package com.thestone.tg.event;

import com.thestone.tg.Tg;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = Tg.MOD_ID)
public class GhoulHungerEvents {
     @SubscribeEvent
     public static void onGhoulTick(PlayerTickEvent.Post event) {
         Player player = event.getEntity();



     }
    @SubscribeEvent
    public static void onFinishUsingItem(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) return;





    }
}
