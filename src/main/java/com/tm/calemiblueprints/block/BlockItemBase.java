package com.tm.calemiblueprints.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;

public class BlockItemBase extends BlockItem {

    public BlockItemBase(Block block, CreativeModeTab tab) {
        super(block , new Properties().tab(tab));
    }

    public BlockItemBase(Block block) {
        super(block , new Properties());
    }
}
