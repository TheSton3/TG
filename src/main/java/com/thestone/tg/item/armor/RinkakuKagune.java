package com.thestone.tg.item.armor;

import com.thestone.tg.client.render.RinkakuKaguneRenderer;
import com.thestone.tg.core.ModItems;
import com.thestone.tg.ghoul.GhoulPlayer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.List;
import java.util.function.Consumer;

public class RinkakuKagune extends ArmorItem implements GeoItem, ICurioItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public RinkakuKagune(Properties properties) {
        super(ArmorMaterials.GOLD, ArmorItem.Type.CHESTPLATE, properties);
    }



    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
      return true;
    }
    @Override
    public boolean canUnequip(SlotContext context, ItemStack stack) {
        return true ;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(@Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable HumanoidModel<T> original) {
                if(this.renderer == null)
                    this.renderer = new RinkakuKaguneRenderer();

                return this.renderer;
            }
        });
    }

    public static void toggleKagune(Player player) {
        // Получаем доступ к инвентарю Curios
        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            handler.getStacksHandler("kagune").ifPresent(stacksHandler -> {
                var inventory = stacksHandler.getStacks();
                ItemStack current = inventory.getStackInSlot(0);
                GhoulPlayer ghoul = GhoulPlayer.get(player);

                /*if (ghoul.isKaguneActive()) {
                    // === ВЫКЛЮЧИТЬ: Очищаем слот ===
                    inventory.setStackInSlot(0, ItemStack.EMPTY);
                    ghoul.setKaguneActive(false);}*/
                  if (current.isEmpty()) {
                    // === ВКЛЮЧИТЬ: Кладем предмет в слот ===
                    // Используем ТВОЙ существующий KaguneArmorItem
                    inventory.setStackInSlot(0, new ItemStack(ModItems.RINKAKU_KAGUNE.get()));
                    //ghoul.setKaguneActive(true);
                }

                // Синхронизируем состояние с клиентом (чтобы сработал рендер)
                ghoul.syncToClient(player);
            });
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {}

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    @Override
    public @Nullable ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        return null;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
}
