package com.tm.calemiblueprints.event;

import com.tm.calemiblueprints.init.InitItems;
import com.tm.calemiblueprints.item.ItemColorPencil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class PencilColorEvent {

    /**
     * Registers the coloring of the Pencil.
     */
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onColorRegister(final ColorHandlerEvent.Item event) {
        event.getItemColors().register(new ItemColorPencil(), InitItems.PENCIL.get());
    }
}
