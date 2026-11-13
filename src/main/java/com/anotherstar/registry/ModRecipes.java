package com.anotherstar.registry;

import com.anotherstar.LoliPickaxe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Custom recipe types.
 *
 * <p>1.12.2 used {@code IRecipe} implementations registered to {@code ForgeRegistries.RECIPES};
 * 1.21 recipes are codec based. The three original recipes (pickaxe, small pickaxe and
 * "superposition") are replaced by vanilla shaped recipes in
 * {@code data/lolipickaxe/recipe/} for now – the custom "nine addons make the next level"
 * logic is handled by {@link com.anotherstar.registry.ModRecipeLogic}.</p>
 */
public final class ModRecipes {

    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, LoliPickaxe.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, LoliPickaxe.MODID);

    public static void register(IEventBus modBus) {
        TYPES.register(modBus);
        SERIALIZERS.register(modBus);
    }

    private ModRecipes() {
    }
}
