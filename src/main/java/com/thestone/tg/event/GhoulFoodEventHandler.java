package com.thestone.tg.event;

import com.thestone.tg.Tg;
import com.thestone.tg.ghoul.GhoulPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = Tg.MOD_ID)
public class GhoulFoodEventHandler {
    // Параметры эффектов (1 секунда = 20 тиков)
    private static final int POISON_DURATION = 200;   // 10 секунд
    private static final int POISON_AMPLIFIER = 0;    // Уровень I
    private static final int WEAKNESS_DURATION = 300; // 15 секунд
    private static final int WEAKNESS_AMPLIFIER = 0;  // Уровень I

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return; // Логика только на сервере

        if (!GhoulPlayer.get(player).isGhoul()) return;

        // Если предмет имеет свойства еды, разрешаем есть ВСЕГДА
        if (event.getItemStack().getFoodProperties(player) != null) {
            player.startUsingItem(event.getHand());
            event.setCanceled(true); // Отменяем ванильную логику, которая проверяет canEat()
        }
    }
    @SubscribeEvent
    public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return; // ⚠️ КРИТИЧНО: только серверная логика

        GhoulPlayer ghoul = GhoulPlayer.get(player);
        if (!ghoul.isGhoul()) return;

        ItemStack stack = event.getItem();
        FoodProperties food = stack.getFoodProperties(player);
        if (food == null) return; // Не еда (например, зелье)

        // Здесь можно добавить проверку на тег гульской еды: stack.is(ModTags.GHOUL_FOOD)
        // Пока считаем ВСЮ обычную еду токсичной
        boolean isSafeForGhoul = false;

        if (!isSafeForGhoul) {
            // НАЛОЖЕНИЕ ДЕБАФФОВ
            player.addEffect(new MobEffectInstance(MobEffects.POISON, POISON_DURATION, POISON_AMPLIFIER));
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, WEAKNESS_DURATION, WEAKNESS_AMPLIFIER));


            // БЛОКИРОВКА ВОССТАНОВЛЕНИЯ ГОЛОДА (канон Tokyo Ghoul)
            // Обычная еда НЕ восстанавливает голод гуля.
            // Если вы хотите, чтобы она восстанавливала хотя бы половину, раскомментируйте:
            // ghoul.getHunger().addFood(food.getNutrition() / 2, food.getSaturationModifier() / 2);
            return; // Прерываем выполнение, чтобы не сработала логика ниже
        }

        // Логика для БЕЗОПАСНОЙ/ГУЛЬСКОЙ еды (будет работать, когда добавите свои предметы)
        ghoul.getHunger().addFood(food.nutrition(), food.saturation());
    }
}
