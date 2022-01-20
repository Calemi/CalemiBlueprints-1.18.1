package com.tm.calemiblueprints.init;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class InitBlockRenderTypes {

    public static void init() {
        ItemBlockRenderTypes.setRenderLayer(InitItems.BLUEPRINT.get(), RenderType.cutout());
        ItemProperties.register(InitItems.BLUEPRINT_ITEM.get(), new ResourceLocation("color"), (stack, level, player, damage) -> stack.getDamageValue());
        ItemBlockRenderTypes.setRenderLayer(InitItems.IRON_SCAFFOLDING.get(), RenderType.cutout());
    }
}
