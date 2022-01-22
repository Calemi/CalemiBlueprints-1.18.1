package com.tm.calemiblueprints.block;

import com.tm.calemiblueprints.config.CBConfig;
import com.tm.calemiblueprints.init.InitItems;
import com.tm.calemicore.util.BlockScanner;
import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.helper.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;

/**
 * The class for Iron Scaffolding Blocks.
 */
public class BlockIronScaffolding extends Block {

    /**
     * Creates an Iron Scaffolding Block.
     */
    public BlockIronScaffolding() {
        super(Block.Properties.of(Material.METAL)
                .noOcclusion()
                .color(MaterialColor.METAL)
                .sound(SoundType.METAL)
                .strength(0.25F)
                .dynamicShape());
    }

    @Override
    public void appendHoverText (ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        LoreHelper.addInformationLoreFirst(tooltip, new TranslatableComponent("cb.lore.iron_scaffolding"));
        LoreHelper.addControlsLoreFirst(tooltip, new TranslatableComponent("cb.lore.iron_scaffolding.use-open-hand"), LoreHelper.ControlType.USE_OPEN_HAND);
        LoreHelper.addControlsLore(tooltip, new TranslatableComponent("cb.lore.iron_scaffolding.sneak-break-block"), LoreHelper.ControlType.SNEAK_BREAK_BLOCK);
        LoreHelper.addControlsLore(tooltip, new TranslatableComponent("cb.lore.iron_scaffolding.left-click-block"), LoreHelper.ControlType.LEFT_CLICK_BLOCK);
        LoreHelper.addControlsLore(tooltip, new TranslatableComponent("cb.lore.iron_scaffolding.sneak-left-click-block"), LoreHelper.ControlType.SNEAK_LEFT_CLICK_BLOCK);
    }

    /**
     * Handles teleportation.
     */
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {

        ItemStack heldStack = player.getMainHandItem();

        //Checks if the held stack is not a Block.
        if (!(heldStack.getItem() instanceof BlockItem)) {

            Location clickedLocation = new Location(level, pos);

            if (CBConfig.server.scaffoldMaxHeightTp.get() == 0) {
                return InteractionResult.FAIL;
            }

            //Handles the teleportation up.
            if (result.getDirection() != Direction.UP) {

                //Iterates through all Blocks above the Scaffold. The size is controlled by the config option.
                for (int i = 0; i < CBConfig.server.scaffoldMaxHeightTp.get(); i++) {

                    Location nextLocation = new Location(clickedLocation, Direction.UP, i);
                    Location nextLocationUp = new Location(clickedLocation, Direction.UP, i + 1);

                    //If the current Location is not a Scaffold, stop all iterating and check further.
                    if (nextLocation.getBlock() != this) {

                        //Checks if the current Location is safe to teleport to.

                        if (PlayerTeleportHelper.canTeleportAt(nextLocation)) {
                            if (!level.isClientSide()) PlayerTeleportHelper.teleportPlayer((ServerPlayer) player, nextLocation, player.getYRot(), player.getXRot());
                            return InteractionResult.SUCCESS;
                        }

                        return InteractionResult.FAIL;
                    }
                }

                return InteractionResult.FAIL;
            }

            //Handles the teleportation down.
            else {

                //Iterates through all Blocks below the Scaffold. The size is controlled by the config option.
                for (int i = 1; i < CBConfig.server.scaffoldMaxHeightTp.get(); i++) {

                    Location nextLocation = new Location(clickedLocation, Direction.DOWN, i);
                    Location nextLocationDown = new Location(clickedLocation, Direction.DOWN, i + 1);

                    //If the current Location is not a Scaffold, stop all iterating and check further.
                    if (nextLocationDown.getBlock() != this) {

                        //Iterates through all the horizontal Locations two times.
                        //Once for the bottom, and once for one above that.
                        for (int yOffset = 0; yOffset < 2; yOffset++) {

                            for (Direction dir : DirectionHelper.HORIZONTAL_DIRECTIONS) {

                                Location sqrLocation = new Location(nextLocation, dir);
                                sqrLocation.translate(Direction.UP, yOffset);

                                //If the current Location is not a Scaffold, stop all iterating and check further.
                                if (sqrLocation.getBlock() != this) {

                                    if (PlayerTeleportHelper.canTeleportAt(sqrLocation)) {
                                        if (!level.isClientSide()) PlayerTeleportHelper.teleportPlayer((ServerPlayer) player, sqrLocation, player.getYRot(), player.getXRot());
                                        return InteractionResult.SUCCESS;
                                    }
                                }
                            }
                        }

                        return InteractionResult.FAIL;
                    }
                }
            }
        }

        return InteractionResult.FAIL;
    }



    /**
     * Handles the Scaffold placement system.
     */
    @Override
    public void attack(BlockState state, Level world, BlockPos pos, Player player) {

        //If the Player is crouching, then place the block horizontally.
        if (!player.isCrouching()) {
            BlockHelper.placeBlockRow(world, player, this, pos, DirectionHelper.getPlayerHorizontalDirection(player));
        }

        //If the Player is not crouching, then place the block downwards.
        else {
            BlockHelper.placeBlockRow(world, player, this, pos, Direction.DOWN);
        }
    }

    /**
     * Handles the Scaffold breaking system.
     */
    @Override
    public void playerWillDestroy (Level level, BlockPos pos, BlockState state, Player player) {

        //Prevents client side.
        if (level.isClientSide()) {
            return;
        }

        Location origin = new Location(level, pos);

        //If the Player is Crouching, then break all connected Scaffolds.
        if (player.isCrouching()) {

            //Starts a scan of all connected Scaffolds.
            BlockScanner scanner = new BlockScanner(origin, defaultBlockState(), CBConfig.server.blockScannerMaxSize.get());
            scanner.startVeinScan();

            //Iterates through the Locations generated by the scan.
            for (Location nextLocation : scanner.buffer) {

                //Set the current Block at the Location to air.
                nextLocation.setBlockToAir();

                //If the Player is not in Creative Mode, then give them the drops of the Block.
                if (!player.isCreative()) {
                    ItemHelper.spawnStackAtEntity(level, player, new ItemStack(InitItems.IRON_SCAFFOLDING.get()));
                }
            }
        }

        //If the Player is not Crouching, then only break on Scaffold.
        else {

            //Set the current Block at the Location to air.
            origin.setBlockToAir();

            //If the Player is not in Creative Mode, then give them the drops of the Block.
            if (!player.isCreative()) {
                ItemHelper.spawnStackAtEntity(level, player, new ItemStack(InitItems.IRON_SCAFFOLDING.get()));
            }
        }
    }

    /*
        Methods for Blocks that are not full and solid cubes.
     */

    @Override
    public boolean propagatesSkylightDown (BlockState state, BlockGetter getter, BlockPos pos) {
        return true;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter getter, BlockPos pos) {
        return 1.0F;
    }
}
