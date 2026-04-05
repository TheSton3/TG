package com.thestone.tg.core;

import com.thestone.tg.Tg;
import com.thestone.tg.ghoul.GhoulPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Tg.MOD_ID);


    public static final Supplier<AttachmentType<GhoulPlayer>> GHOUL_PLAYER =
            ATTACHMENT_TYPES.register("ghoul_player", () ->
                    AttachmentType.builder(GhoulPlayer::create)
                            .serialize(GhoulPlayer.CODEC)
                            .copyOnDeath()
                            .build()
            );

    public static void init(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }

}
