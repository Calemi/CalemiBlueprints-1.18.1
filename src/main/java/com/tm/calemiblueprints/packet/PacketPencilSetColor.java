package com.tm.calemiblueprints.packet;

import com.tm.calemiblueprints.item.ItemPencil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketPencilSetColor {

    private int colorId;
    private boolean offHand;

    public PacketPencilSetColor() {}

    /**
     * Used to sync the color data of the Pencil.
     * @param colorId The color id from which a color can be created from.
     * @param hand The hand the Pencil is held.
     */
    public PacketPencilSetColor(int colorId, InteractionHand hand) {
        this.colorId = colorId;
        this.offHand = (hand != InteractionHand.MAIN_HAND);
    }

    public PacketPencilSetColor(FriendlyByteBuf buf) {
        colorId = buf.readInt();
        offHand = buf.readBoolean();
    }

    public void toBytes (FriendlyByteBuf buf) {
        buf.writeInt(colorId);
        buf.writeBoolean(offHand);
    }

    public void handle (Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            InteractionHand hand = InteractionHand.MAIN_HAND;
            if (offHand) hand = InteractionHand.OFF_HAND;

            ServerPlayer player = ctx.get().getSender();

            if (player != null) {

                final ItemStack stack = player.getItemInHand(hand);

                //Checks if the held item is a Pencil.
                if (stack.getItem() instanceof ItemPencil pencil) {
                    ItemPencil.setColorById(stack, colorId);
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
