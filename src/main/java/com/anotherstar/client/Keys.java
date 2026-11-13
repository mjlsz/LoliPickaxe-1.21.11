package com.anotherstar.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public final class Keys {

    /** 1.21.11 requires a registered category instead of a translation key. */
    public static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath("lolipickaxe", "main"));

    public static final KeyMapping OPEN_STORAGE = new KeyMapping(
            "key.lolipickaxe.storage",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            CATEGORY);

    public static final KeyMapping OPEN_CONFIG = new KeyMapping(
            "key.lolipickaxe.config",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            CATEGORY);

    private Keys() {
    }
}

