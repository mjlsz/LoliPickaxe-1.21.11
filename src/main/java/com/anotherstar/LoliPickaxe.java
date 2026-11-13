package com.anotherstar;

import com.anotherstar.common.CommonEvents;
import com.anotherstar.common.config.Config;
import com.anotherstar.network.ModNetwork;
import com.anotherstar.registry.ModBlocks;
import com.anotherstar.registry.ModEntities;
import com.anotherstar.registry.ModItems;
import com.anotherstar.registry.ModMenus;
import com.anotherstar.registry.ModRecipes;
import com.anotherstar.registry.ModSounds;
import com.anotherstar.registry.ModTabs;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point of the 1.21.11 (NeoForge 21.11) port of LoliPickaxe.
 *
 * <p>The 1.12.2 original is a Forge coremod. Everything that used to be done by
 * {@code LoliPickaxeTransformer} (ASM class transformation) is either done with a
 * proper NeoForge API here, or documented as not ported.</p>
 */
@Mod(LoliPickaxe.MODID)
public class LoliPickaxe {

    public static final String MODID = "lolipickaxe";
    public static final String NAME = "LoliPickaxe Mod";
    public static final String VERSION = "2.0.0";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    public LoliPickaxe(IEventBus modBus, ModContainer container) {
        ModSounds.register(modBus);
        ModItems.register(modBus);
        ModBlocks.register(modBus);
        ModEntities.register(modBus);
        ModMenus.register(modBus);
        ModRecipes.register(modBus);
        ModTabs.register(modBus);

        modBus.addListener(ModNetwork::register);
        modBus.addListener(LoliPickaxe::commonSetup);

        // Game bus (old MinecraftForge.EVENT_BUS)
        NeoForge.EVENT_BUS.register(CommonEvents.class);
        NeoForge.EVENT_BUS.register(com.anotherstar.common.AddonCraftingHandler.class);
        NeoForge.EVENT_BUS.register(com.anotherstar.common.LoliDropHandler.class);
        NeoForge.EVENT_BUS.register(com.anotherstar.common.command.LoliCommand.class);

        if (isClient()) {
            com.anotherstar.client.ClientSetup.init(modBus);
        }

        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("LoliPickaxe {} loading for Minecraft 1.21.11 / NeoForge", VERSION);
        net.minecraft.core.registries.BuiltInRegistries.ITEM.stream()
                .filter(item -> net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(MODID))
                .forEach(item -> LOGGER.debug("registered item {}", net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item)));
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    /** True when client-only classes are present (no FML API dependency). */
    private static boolean isClient() {
        try {
            Class.forName("net.minecraft.client.Minecraft", false, LoliPickaxe.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
