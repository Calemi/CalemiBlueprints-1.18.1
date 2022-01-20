package com.tm.calemiblueprints.screen;

import com.tm.calemiblueprints.init.InitItems;
import com.tm.calemiblueprints.main.CBReference;
import com.tm.calemicore.util.helper.ItemHelper;
import com.tm.calemicore.util.screen.ItemStackButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PencilButton extends ItemStackButton {

    private final int colorId;

    /**
     * A colored Pencil button. Used to set the color of a Pencil.
     * @param colorId Used to determine what color of Pencil to render.
     * @param pressable Called when the button is pressed.
     */
    public PencilButton(int colorId, int x, int y, ItemRenderer itemRender, Button.OnPress pressable) {
        super(x, y, new ResourceLocation(CBReference.MOD_ID + ":textures/gui/tooltip.png"), itemRender, pressable);
        this.colorId = colorId;
    }

    @Override
    public ItemStack getRenderedStack() {
        ItemStack stack = new ItemStack(InitItems.PENCIL.get());
        stack.getOrCreateTag().putInt("color", colorId);
        return stack;
    }

    @Override
    public TranslatableComponent[] getTooltip() {
        return new TranslatableComponent[] {new TranslatableComponent("cb.color." + DyeColor.byId(colorId).getName())};
    }
}
