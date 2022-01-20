package com.tm.calemiblueprints.block;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

/**
 * General Properties for Blocks and Items
 */
public class CBBlockStates {

    /**
     * Determines the color of the Block.
     */
    public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);

    public static final BooleanProperty GRAVITY = BooleanProperty.create("gravity");
}
