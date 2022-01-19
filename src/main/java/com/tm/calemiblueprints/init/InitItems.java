package com.tm.calemiblueprints.init;

import com.tm.calemiblueprints.block.BlockBlueprint;
import com.tm.calemiblueprints.block.BlockItemBlueprint;
import com.tm.calemiblueprints.item.ItemPencil;
import com.tm.calemiblueprints.main.CBReference;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * Handles setting up the Items for the mod.
 */
public class InitItems {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CBReference.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CBReference.MOD_ID);

    /**
     * Called to initialize the Items.
     */
    public static void init() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<Block> BLUEPRINT = BLOCKS.register("blueprint", BlockBlueprint::new);
    public static final RegistryObject<Item> BLUEPRINT_ITEM = ITEMS.register("blueprint", BlockItemBlueprint::new);

    public static final RegistryObject<Item> PENCIL = ITEMS.register("pencil", ItemPencil::new);

    /**
     * Used to register an Item.
     * @param name The name of the Item.
     * @param sup The Item class.
     */
    public static RegistryObject<Item> regItem(String name, final Supplier<? extends Item> sup) {
        return ITEMS.register(name, sup);
    }
}
