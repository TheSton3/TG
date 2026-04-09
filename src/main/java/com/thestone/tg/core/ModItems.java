package com.thestone.tg.core;

import com.thestone.tg.Tg;
import com.thestone.tg.item.KakuhoeItem;
import com.thestone.tg.item.armor.RinkakuKagune;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Tg.MOD_ID);

    public static final DeferredItem<Item> HUMAN_MEAT =
            ITEMS.registerItem("human_meat", Item::new, new Item.Properties().food(MoodFoodProperties.HUMAN_MEAT));

    public static final DeferredItem<Item> KAKUHOE =
            ITEMS.registerItem("kakuhoe", KakuhoeItem::new, new Item.Properties());

    public static final DeferredItem<Item> RINKAKU_KAGUNE =
            ITEMS.register("rinkaku_kagune", ()-> new RinkakuKagune(new Item.Properties()));

    public static void init(IEventBus bus) {
        ITEMS.register(bus);

    }
}
