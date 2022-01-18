package com.tm.calemiblueprints.block;

import com.tm.calemiblueprints.init.InitItems;
import com.tm.calemiblueprints.main.CalemiBlueprints;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class BlockItemBlueprint extends BlockItem {

    public BlockItemBlueprint() {
        super(InitItems.BLUEPRINT.get(), new Item.Properties().tab(CalemiBlueprints.TAB) );
    }
}
