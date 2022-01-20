package com.tm.calemiblueprints.item;

import com.tm.calemiblueprints.block.BlockBlueprint;
import com.tm.calemiblueprints.config.CBConfig;
import com.tm.calemiblueprints.main.CalemiBlueprints;
import com.tm.calemicore.util.BlockScanner;
import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.helper.LoreHelper;
import com.tm.calemicore.util.helper.SoundHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * The class for Eraser Items.
 */
public class ItemEraser extends Item {

    /**
     * Creates an Eraser Item.
     */
    public ItemEraser() {
        super(new Item.Properties().tab(CalemiBlueprints.TAB).stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipList, TooltipFlag flag) {
        LoreHelper.addInformationLoreFirst(tooltipList, new TranslatableComponent("cb.lore.eraser"));
        LoreHelper.addControlsLoreFirst(tooltipList, new TranslatableComponent("cb.lore.eraser.use"), LoreHelper.ControlType.USE);
        LoreHelper.addControlsLore(tooltipList, new TranslatableComponent("cb.lore.eraser.sneak-use"), LoreHelper.ControlType.SNEAK_USE);
    }

    /**
     * Handles erasing Blueprint Blocks.
     */
    @Override
    public InteractionResult useOn(UseOnContext context) {

        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();

        Location origin = new Location(level, pos);

        //Checks if the Player exists.
        if (player != null) {

            //Checks if the block clicked is a Blueprint.
            if (origin.getBlock() instanceof BlockBlueprint) {

                //If the Player is not crouching, remove only one Blueprint.
                if (!player.isCrouching()) {
                    origin.setBlockToAir();
                    SoundHelper.playAtPlayer(player, SoundEvents.SLIME_ATTACK, 0.9F, 1.0F);
                }

                //If the Player is crouching, remove multiple Blueprints.
                else {

                    //Starts a scan of all connected Blueprint.
                    BlockScanner scanner = new BlockScanner(origin, origin.getBlockState(), CBConfig.server.blockScannerMaxSize.get());
                    scanner.startVeinScan();

                    //Iterates through all scanned Blueprints and removes them.
                    for (Location nextLocation : scanner.buffer) {
                        nextLocation.setBlockToAir();
                    }

                    SoundHelper.playAtPlayer(player, SoundEvents.SLIME_ATTACK, 0.9F, 0.8F);
                }

                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.FAIL;
    }

    /**
     * Used to increase the mining speed on Blueprint.
     */
    @Override
    public float getDestroySpeed (ItemStack stack, BlockState state) {

        if (state.getBlock() instanceof BlockBlueprint) {
            return 9F;
        }

        return super.getDestroySpeed(stack, state);
    }
}
