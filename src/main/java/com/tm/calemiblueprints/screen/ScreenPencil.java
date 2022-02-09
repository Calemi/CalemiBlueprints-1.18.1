package com.tm.calemiblueprints.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tm.calemiblueprints.packet.CBPacketHandler;
import com.tm.calemiblueprints.packet.PacketPencilSetColor;
import com.tm.calemicore.util.helper.ScreenHelper;
import com.tm.calemicore.util.screen.ScreenBase;
import com.tm.calemicore.util.screen.ScreenRect;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenPencil extends ScreenBase {

    private final PencilButton[] buttons = new PencilButton[16];

    public ScreenPencil(Player player, InteractionHand hand) {
        super(player, hand);
    }

    @Override
    protected void init () {

        super.init();

        for (int i = 0; i < buttons.length; i++) {
            int id = i;

            addRenderableWidget(new PencilButton(id, getScreenX() + (i * 20) - 158, getScreenY() - 8, itemRenderer, (btn) -> {
                CBPacketHandler.INSTANCE.sendToServer(new PacketPencilSetColor(id, hand));
                player.closeContainer();
            }));
        }
    }

    @Override
    public boolean isPauseScreen () {
        return false;
    }

    @Override
    public int getGuiSizeX () {
        return 0;
    }

    @Override
    public int getGuiSizeY () {
        return 0;
    }

    @Override
    public void drawGuiBackground (PoseStack poseStack, int mouseX, int mouseY) {

        for (int i = 0; i < DyeColor.values().length; i++) {
            int color = DyeColor.byId(i).getTextColor();
            ScreenHelper.drawColoredRect(new ScreenRect(getScreenX() + (i * 20) - 160, 0, 20, this.height), 0, color, 0.4F);
        }

        ScreenHelper.drawCenteredString(poseStack, getScreenX(), getScreenY() - 25, 0, 0xFFFFFF, new TranslatableComponent("cb.gui.pencil.name"));
    }

    @Override
    public void drawGuiForeground (PoseStack poseStack, int mouseX, int mouseY) {}

    @Override
    public boolean canCloseWithInvKey () {
        return true;
    }
}
