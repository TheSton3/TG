package com.thestone.tg.core;

import com.thestone.tg.Tg;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Tg.MOD_ID);

    public static final DeferredItem<Item> HUMAN_MEAT =
            ITEMS.registerItem("human_meat", Item::new, new Item.Properties().food(MoodFoodProperties.HUMAN_MEAT));

    public static final DeferredItem<Item> KAKUHOE =
            ITEMS.registerItem("kakuhoe", Item::new, new Item.Properties());


    public static void init(IEventBus bus) {
        ITEMS.register(bus);

    }
}
