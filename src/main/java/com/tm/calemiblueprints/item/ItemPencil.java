package com.tm.calemiblueprints.item;

import com.tm.calemiblueprints.block.BlockBlueprint;
import com.tm.calemiblueprints.init.InitItems;
import com.tm.calemiblueprints.main.CalemiBlueprints;
import com.tm.calemiblueprints.screen.ScreenPencil;
import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.helper.ItemHelper;
import com.tm.calemicore.util.helper.LoreHelper;
import com.tm.calemicore.util.helper.SoundHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ItemPencil extends Item {

    public ItemPencil() {
        super(new Item.Properties().tab(CalemiBlueprints.TAB).stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipList, TooltipFlag flag) {
        LoreHelper.addInformationLoreFirst(tooltipList, new TranslatableComponent("cb.lore.pencil"));
        LoreHelper.addControlsLoreFirst(tooltipList, new TranslatableComponent("cb.lore.pencil.use"), LoreHelper.ControlType.USE);
        LoreHelper.addControlsLore(tooltipList, new TranslatableComponent("cb.lore.pencil.sneak-use"), LoreHelper.ControlType.SNEAK_USE);
        LoreHelper.addBlankLine(tooltipList);
        tooltipList.add(new TranslatableComponent("cb.lore.pencil.color").withStyle(ChatFormatting.AQUA).append(" ").append(new TranslatableComponent("cb.color." + DyeColor.byId(getColorId(stack)))));
    }

    /**
     * Gets the color id of a given Pencil Stack.
     */
    public static int getColorId(ItemStack stack) {

        int meta = 11;

        if (ItemHelper.getNBT(stack).contains("color")) {
            meta = ItemHelper.getNBT(stack).getInt("color");
        }

        return meta;
    }

    /**
     * Sets the Pencil's color by id.
     */
    public static void setColorById(ItemStack stack, int meta) {
        ItemHelper.getNBT(stack).putInt("color", meta);
    }

    /**
     * Handles placing Blueprint & opening the GUI.
     */
    @Override
    public InteractionResult useOn(UseOnContext context) {

        BlockPlaceContext placeContext = new BlockPlaceContext(context);

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        Direction dir = context.getClickedFace();
        InteractionHand hand = context.getHand();

        BlockBlueprint BLUEPRINT = (BlockBlueprint) InitItems.BLUEPRINT.get();
        Location location = new Location(level, pos);

        //Checks if the Player exists.
        if (player != null) {

            //If the Player is crouching, open the GUI.
            if (player.isCrouching()) {

                if (level.isClientSide()) {
                    openGui(player, hand);
                }

                return InteractionResult.SUCCESS;
            }

            //If the Player is not crouching, check some things.

            //Checks if the clicked Location can be replaced.
            if (!location.getBlockState().getMaterial().isReplaceable()) {

                location = new Location(location, dir);

                if (!location.isBlockValidForPlacing()) return InteractionResult.FAIL;
            }

            //Checks if the Player is not at that location.
            if (location.isEntityAtLocation(player)) {
                return InteractionResult.FAIL;
            }

            //Checks if the Player can edit the Location.
            if (!placeContext.canPlace()) return InteractionResult.FAIL;

            else {

                if (location.isBlockValidForPlacing()) {
                    location.setBlock(BLUEPRINT.defaultBlockState().setValue(BlockBlueprint.COLOR, DyeColor.byId(getColorId(context.getItemInHand()))));
                    SoundHelper.playAtLocation(location, SoundEvents.BAMBOO_PLACE, SoundSource.BLOCKS, 1, 1);
                }

                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.FAIL;
    }

    /**
     * Handles opening the GUI.
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (world.isClientSide() && player.isCrouching()) {
            openGui(player, hand);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }

        return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
    }

    @OnlyIn(Dist.CLIENT)
    private void openGui(Player player, InteractionHand hand) {
        Minecraft.getInstance().setScreen(new ScreenPencil(player, hand));
    }
}
