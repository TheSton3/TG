package com.thestone.tg.event;

import com.ibm.icu.impl.Utility;
import com.thestone.tg.Tg;
import com.thestone.tg.core.ModAttachments;
import com.thestone.tg.core.ModItems;
import com.thestone.tg.ghoulhunger.HungerHandler;
import com.thestone.tg.util.Util;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = Tg.MOD_ID)
public class GhoulHungerEvents {
    @SubscribeEvent
    public static void onGhoulTick(PlayerTickEvent.Post event) {
       Player player = event.getEntity();
        int hunger = player.getData(ModAttachments.GHOUL_HUNGER.get());
        if (hunger > 0 && player instanceof ServerPlayer serverPlayer) {
            if (player.level().getGameTime() % 80 == 0) {
                if (hunger > 0) {
                    HungerHandler.decreaseHungerForPlayer(serverPlayer, 1);
                } else {
                    if (player.level().getGameTime() % 40 == 0) {
                        player.hurt(player.damageSources().starve(), 1.0F);
                    }
                }
            }
        }

    }
    @SubscribeEvent
    public static void onFinishUsingItem(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!Util.isGhoul(player) || event.getEntity().level().isClientSide()) return;
        ItemStack stack = event.getItem();

        // Проверяем тег "безопасной" еды для гулей
        boolean isSafeFood = stack.is(ModItems.HUMAN_MEAT);

       /* if (!isSafeFood) {

            player.addEffect(new MobEffectInstance(MobEffects.POISON, 400, 1));
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 1));

        }*/

            if(Util.isGhoul(player)){
              HungerHandler.increaseHungerForPlayer(player, 3);
            }

        // Если еда безопасная - голод восстановится через HungerHandler (если вызовешь его из своего Item)
    }
}
