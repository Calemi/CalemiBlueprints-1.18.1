package com.tm.calemiblueprints.item;

import com.tm.calemiblueprints.init.InitItems;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

/**
 * Used by the Pencil to register its colors.
 */
public class ItemColorPencil implements ItemColor {

    @Override
    public int getColor (ItemStack stack, int tintLayer) {

        ItemPencil pencil = (ItemPencil) InitItems.PENCIL.get();

        if (tintLayer == 1) {
            int colorMeta = ItemPencil.getColorId(stack);
            DyeColor dye = DyeColor.byId(colorMeta);

            return dye.getTextColor();
        }

        return 0xFFFFFF;
    }
}
