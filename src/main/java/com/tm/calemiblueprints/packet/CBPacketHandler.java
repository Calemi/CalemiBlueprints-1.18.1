package com.tm.calemiblueprints.packet;

import com.tm.calemiblueprints.main.CBReference;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class CBPacketHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(CBReference.MOD_ID, CBReference.MOD_ID),
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals
    );

    public static void init() {
        int id = 0;
        CBPacketHandler.INSTANCE.registerMessage(++id, PacketPencilSetColor.class, PacketPencilSetColor::toBytes, PacketPencilSetColor::new, PacketPencilSetColor::handle);

    }
}
