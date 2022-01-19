package com.tm.calemiblueprints.main;

import com.tm.calemiblueprints.config.CBConfig;
import com.tm.calemiblueprints.init.InitBlockRenderTypes;
import com.tm.calemiblueprints.init.InitItems;
import com.tm.calemiblueprints.packet.CBPacketHandler;
import com.tm.calemiblueprints.tab.CBTab;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * The main class for Calemi's Blueprints
 */
@Mod(CBReference.MOD_ID)
public class CalemiBlueprints {

    /**
     * A reference to the instance of the mod.
     */
    public static CalemiBlueprints instance;

    /**
     * Used to register the client and common setup methods.
     */
    public static IEventBus MOD_EVENT_BUS;

    public static final CreativeModeTab TAB = new CBTab();

    /**
     * Everything starts here.
     */
    public CalemiBlueprints() {

        //Initializes the instance.
        instance = this;

        MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        MOD_EVENT_BUS.addListener(this::onCommonSetup);
        MOD_EVENT_BUS.addListener(this::onClientSetup);

        InitItems.init();
        CBConfig.init();
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        CBPacketHandler.init();
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        InitBlockRenderTypes.init();
    }
}
