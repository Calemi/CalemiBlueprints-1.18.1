package com.tm.calemiblueprints.item;

import com.tm.calemiblueprints.main.CBReference;
import com.tm.calemiblueprints.main.CalemiBlueprints;
import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.UnitMessenger;
import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicore.util.helper.LoreHelper;
import com.tm.calemicore.util.helper.SoundHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * The class for Brush Items.
 */
public class ItemBrush extends Item {

    public static final UnitMessenger MESSENGER = new UnitMessenger("brush");

    /**
     * Creates a Brush Item.
     */
    public ItemBrush() {
        super(new Item.Properties().tab(CalemiBlueprints.TAB).stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipList, TooltipFlag flag) {
        LoreHelper.addInformationLoreFirst(tooltipList, new TranslatableComponent("cb.lore.brush.1"));
        LoreHelper.addInformationLore(tooltipList, new TranslatableComponent("cb.lore.brush.2"));
        LoreHelper.addControlsLoreFirst(tooltipList, new TranslatableComponent("cb.lore.brush.use"), LoreHelper.ControlType.USE);
        LoreHelper.addControlsLore(tooltipList, new TranslatableComponent("cb.lore.brush.sneak-use"), LoreHelper.ControlType.SNEAK_USE);
        LoreHelper.addBlankLine(tooltipList);

        Location position_1 = getPosition(level, stack, 1);
        Location position_2 = getPosition(level, stack, 2);

        tooltipList.add(new TranslatableComponent("cb.lore.brush.pos1").withStyle(ChatFormatting.GRAY).append(" ")
                .append(position_1 != null ? new TextComponent(position_1.toString()) : new TranslatableComponent("cb.lore.brush.not-set")).withStyle(ChatFormatting.AQUA));
        tooltipList.add(new TranslatableComponent("cb.lore.brush.pos2").withStyle(ChatFormatting.GRAY).append(" ")
                .append(position_2 != null ? new TextComponent(position_2.toString()) : new TranslatableComponent("cb.lore.brush.not-set")).withStyle(ChatFormatting.AQUA));
    }

    public static Location getPosition(Level level, ItemStack stack, int index) {
        CompoundTag tag = stack.getOrCreateTag();
        return Location.readFromNBT(level, tag.getCompound("pos" + index));
    }

    public static void setPosition(ItemStack stack, int index, Location location) {
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag pos = new CompoundTag();
        location.writeToNBT(pos);
        tag.put("pos" + index, pos);
        LogHelper.logCommon(CBReference.MOD_NAME, location.level, location);
        stack.setTag(tag);
    }

    /**
     * Handles setting the positions.
     */
    @Override
    public InteractionResult useOn(UseOnContext context) {

        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack heldStack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();

        //Checks if the Player exists.
        if (player != null) {

            //If the Player is not crouching, set the first position.
            if (!player.isCrouching()) {

                setPosition(heldStack, 1, new Location(level, pos));
                Location position_1 = getPosition(level, heldStack, 1);
                if (!level.isClientSide()) MESSENGER.sendMessage(MESSENGER.getMessage("pos1.set").append(" ").append(position_1.toString()), player);
            }

            //If the Player is crouching, set the second position.
            else {

                setPosition(heldStack, 2, new Location(level, pos));
                Location position_2 = getPosition(level, heldStack, 2);
                if (!level.isClientSide()) MESSENGER.sendMessage(MESSENGER.getMessage("pos2.set").append(" ").append(position_2.toString()), player);
            }

            SoundHelper.playSimple(player, SoundEvents.UI_BUTTON_CLICK);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }
}
