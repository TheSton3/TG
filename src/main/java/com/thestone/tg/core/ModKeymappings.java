package com.thestone.tg.core;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;

import net.neoforged.jarjar.nio.util.Lazy;
import org.lwjgl.glfw.GLFW;

public class ModKeymappings {

    private static final KeyMapping KEY_MAPPING_FEED = new KeyMapping("key.tg.feed",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.categories.tg");
    private static final KeyMapping KEY_MAPPING_KAGUNE = new KeyMapping("key.tg.kagune",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "key.categories.tg");

    public static final Lazy<KeyMapping> PRESS_FEED = Lazy.of(() -> KEY_MAPPING_FEED);
    public static final Lazy<KeyMapping> PRESS_KAGUNE = Lazy.of(() -> KEY_MAPPING_KAGUNE);
}
