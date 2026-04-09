package com.thestone.tg.client.render;

import com.thestone.tg.Tg;
import com.thestone.tg.item.armor.RinkakuKagune;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class RinkakuKaguneRenderer extends GeoArmorRenderer<RinkakuKagune> {
    public RinkakuKaguneRenderer() {
        super(new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(Tg.MOD_ID, "rinkaku_kagune"))); // Using DefaultedItemGeoModel like this puts our 'location' as item/armor/example armor in the assets folders.
    }
}