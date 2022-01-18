package com.tm.calemiblueprints.tab;

import com.tm.calemiblueprints.main.CBReference;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CBTab extends CreativeModeTab {

    public CBTab() {
        super(CBReference.MOD_ID + ".tabMain");
    }

    @Override
    public ItemStack makeIcon () {
        return new ItemStack(Items.APPLE);
    }
}
