package com.tm.calemiblueprints.recipe;

import com.google.common.collect.Lists;
import com.tm.calemiblueprints.init.InitItems;
import com.tm.calemiblueprints.init.InitRecipes;
import com.tm.calemiblueprints.item.ItemPencil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.List;

public class BlueprintRecipe extends CustomRecipe {

    public BlueprintRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingContainer inv, Level level) {

        int emptyCount = 0;
        int pencilCount = 0;

        for (int i = 0; i < inv.getContainerSize(); i++) {

            ItemStack stackInSlot = inv.getItem(i);

            if (stackInSlot.getItem() instanceof ItemPencil) {
                pencilCount++;
            } else if (stackInSlot.isEmpty()) {
                emptyCount++;
            }
        }

        return pencilCount == 1 && (emptyCount == 3 || emptyCount == 8);
    }

    @Override
    public ItemStack assemble(CraftingContainer inv) {

        List<DyeItem> list = Lists.newArrayList();
        int colorID = 0;

        for (int i = 0; i < inv.getContainerSize(); i++) {

            ItemStack stackInSlot = inv.getItem(i);

            if (stackInSlot.getItem() instanceof ItemPencil) {
                colorID = ItemPencil.getColorId(stackInSlot);
            }
        }

        ItemStack result = new ItemStack(InitItems.BLUEPRINT_ITEM.get(), 64);
        result.setDamageValue(colorID);

        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return InitRecipes.BLUEPRINT.get();
    }
}