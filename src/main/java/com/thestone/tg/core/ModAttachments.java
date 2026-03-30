package com.thestone.tg.core;

import com.mojang.serialization.Codec;
import com.thestone.tg.Tg;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {
    // Регистрируем в NeoForgeRegistries.ATTACHMENT_TYPES
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Tg.MOD_ID);

    // Тип данных: Integer (уровень голода)
    public static final Supplier<AttachmentType<Integer>> GHOUL_HUNGER = ATTACHMENT_TYPES.register("ghoul_hunger",
            () -> AttachmentType.<Integer>builder(() -> 0).serialize(Codec.INT).build());

    public static void init(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }

}
