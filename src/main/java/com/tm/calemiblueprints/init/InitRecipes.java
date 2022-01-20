package com.tm.calemiblueprints.init;

import com.tm.calemiblueprints.main.CBReference;
import com.tm.calemiblueprints.recipe.BlueprintRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class InitRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CBReference.MOD_ID);
    public static final RegistryObject<RecipeSerializer<BlueprintRecipe>> BLUEPRINT = RECIPES.register("blueprint", () -> new SimpleRecipeSerializer<>(BlueprintRecipe::new));

}
