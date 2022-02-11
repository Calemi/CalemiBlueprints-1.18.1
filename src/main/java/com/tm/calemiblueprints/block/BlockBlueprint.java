package com.tm.calemiblueprints.block;

import com.tm.calemiblueprints.config.CBConfig;
import com.tm.calemiblueprints.item.ItemPencil;
import com.tm.calemiblueprints.packet.CBPacketHandler;
import com.tm.calemiblueprints.packet.PacketPencilSetColor;
import com.tm.calemicore.util.BlockScanner;
import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.UnitMessenger;
import com.tm.calemicore.util.helper.ContainerHelper;
import com.tm.calemicore.util.helper.ItemHelper;
import com.tm.calemicore.util.helper.SoundHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;

/**
 * The class for Blueprint Blocks.
 */
public class BlockBlueprint extends Block {

    private static final UnitMessenger MESSENGER = new UnitMessenger("blueprint");

    /**
     * The specific color of the Blueprint.
     * Can be 16 different colors. Similar to Wool.
     */
    public static final EnumProperty<DyeColor> COLOR = CBBlockStates.COLOR;

    /**
     * Creates a Blueprint Block
     */
    public BlockBlueprint() {
        super(BlockBehaviour.Properties.of(Material.GLASS)
                .color(MaterialColor.COLOR_BLUE)
                .sound(SoundType.BAMBOO)
                .noOcclusion()
                .strength(0.1F));
        registerDefaultState(getStateDefinition().any().setValue(COLOR, DyeColor.BLUE));
    }

    /**
     * Replaces a single Blueprint with the placer's held item.
     * @param location The Location of the replacement.
     * @param placer The player who replaced the Blueprint.
     * @param blockToReplace The block that will replace the Blueprint
     */
    private void replaceBlueprint(Location location, Player placer, Block blockToReplace) {

        if (!location.level.isClientSide()) {

            BlockHitResult hitResult = new BlockHitResult(location.getVector(), placer.getDirection(), location.getBlockPos(), false);
            BlockPlaceContext context = new BlockPlaceContext(placer, InteractionHand.MAIN_HAND, placer.getMainHandItem(), hitResult);

            location.setBlock(context, blockToReplace);
        }
    }

    /**
     * Replaces all scanned Locations with a given held stack.
     * @param origin  The Location of the clicked Blueprint
     * @param placer  The player who clicked the Blueprint.
     * @param scanner Used for its list of scanned Locations.
     */
    private void replaceAllBlueprint(Location origin, Player placer, BlockScanner scanner) {

        ItemStack heldStack = placer.getMainHandItem();
        Block heldBlock = Block.byItem(heldStack.getItem());
        BlockState heldBlockState = heldBlock.defaultBlockState();

        //Checks if the held Block State can be placed within a Blueprint.
        if (canBlockReplaceBlueprint(origin.level, heldBlockState)) {

            //Handles replacing only one Block.
            if (placer.isCrouching()) {
                replaceBlueprint(origin, placer, heldBlock);
                ContainerHelper.consumeItems(placer.getInventory(), heldStack, 1, false);
                SoundHelper.playBlockPlace(origin, heldBlockState);
            }

            //Handles replacing every block in list.
            else {

                int itemCount = ContainerHelper.countItems(placer.getInventory(), heldStack, false);

                if (itemCount >= scanner.buffer.size()) {

                    int amountToConsume = 0;

                    for (Location nextLocation : scanner.buffer) {
                        amountToConsume++;
                        replaceBlueprint(nextLocation, placer, heldBlock);
                    }

                    if (amountToConsume > 0) {

                        SoundHelper.playSimple(placer, SoundEvents.EXPERIENCE_ORB_PICKUP);
                        SoundHelper.playBlockPlace(origin, heldBlockState);

                        if (!origin.level.isClientSide()) {
                            MESSENGER.sendMessage(MESSENGER.getMessage("place").append(" ").append(ItemHelper.countByStacks(amountToConsume)), placer);
                        }

                        ContainerHelper.consumeItems(placer.getInventory(), heldStack, amountToConsume, false);
                    }
                }

                else if (!origin.level.isClientSide()) {

                    MESSENGER.sendErrorMessage(MESSENGER.getMessage("error.notenough"), placer);
                    MESSENGER.sendErrorMessage(MESSENGER.getMessage("error.notenough.missing")
                            .append(" ").append(ItemHelper.countByStacks((scanner.buffer.size() - itemCount))), placer);
                }
            }
        }
    }

    /**
     * Checks if the given Block State can be placed without any errors.
     * @param state The checked Block State.
     */
    private boolean canBlockReplaceBlueprint (Level level, BlockState state) {

        if (!state.canSurvive(level, new BlockPos(0, 300, 0))) return false;
        if (state.getBlock() instanceof ShulkerBoxBlock) return false;
        if (state.getBlock() instanceof ChestBlock) return false;
        if (state.getBlock() instanceof BedBlock) return false;

        return state.getMaterial() != Material.PLANT &&
                state.getMaterial() != Material.BAMBOO &&
                state.getMaterial() != Material.BAMBOO_SAPLING &&
                state.getMaterial() != Material.REPLACEABLE_PLANT &&
                state.getMaterial() != Material.REPLACEABLE_FIREPROOF_PLANT &&
                state.getMaterial() != Material.REPLACEABLE_WATER_PLANT &&
                state.getMaterial() != Material.AIR &&
                state.getMaterial() != Material.CACTUS &&
                state.getMaterial() != Material.CAKE &&
                state.getMaterial() != Material.PORTAL;
    }

    /**
     * Called when a block is left-clicked.
     * Used to replace Blueprint.
     */
    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {

        Location origin = new Location(level, pos);
        ItemStack heldStack = player.getMainHandItem();

        //Creates a scanner which will search through multiple Blueprints.
        BlockScanner scanner = new BlockScanner(origin, origin.getBlockState(), CBConfig.server.blockScannerMaxSize.get(), false);
        scanner.startVeinScan();

        //Checking if the held stack exists and that it's not a Blueprint.
        if (!heldStack.isEmpty() && heldStack.getItem() != Item.BY_BLOCK.get(this)) {

            //Checking if the held stack is a Block.
            if (heldStack.getItem() instanceof BlockItem) {
                replaceAllBlueprint(origin, player, scanner);
            }
        }

        //Checking if the held stack is air and if the Player is crouching.
        else if (!level.isClientSide() && player.isCrouching() && heldStack.isEmpty()) {

            //Checking if the BlockScanner list exceeds the config option. If so, print the max scan size.
            if (scanner.buffer.size() >= CBConfig.server.blockScannerMaxSize.get()) {
                MESSENGER.sendErrorMessage(MESSENGER.getMessage("scan.max").append(" ").append(CBConfig.server.blockScannerMaxSize.get().toString()), player);
            }

            else MESSENGER.sendMessage(MESSENGER.getMessage("scan").append(" ").append(ItemHelper.countByStacks(scanner.buffer.size())), player);
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        level.setBlock(pos, state.setValue(COLOR, DyeColor.byId(stack.getDamageValue())), 0);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {

        if (player.getMainHandItem().getItem() instanceof ItemPencil) {
            CBPacketHandler.INSTANCE.sendToServer(new PacketPencilSetColor(state.getValue(COLOR).getId(), InteractionHand.MAIN_HAND));
        }

        else if (player.getOffhandItem().getItem() instanceof ItemPencil) {
            CBPacketHandler.INSTANCE.sendToServer(new PacketPencilSetColor(state.getValue(COLOR).getId(), InteractionHand.OFF_HAND));
        }

        ItemStack stack = new ItemStack(this);
        stack.setDamageValue(state.getValue(COLOR).getId());
        return stack;
    }

    /**
     * Registers the Block's properties.
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(COLOR);
    }

    // BLOCK METHODS FOR TRANSPARENCY \\

    @Override
    public boolean skipRendering(BlockState centerState, BlockState otherState, Direction dir) {
        return (otherState.getBlock() == this && centerState.getValue(COLOR).getId() == otherState.getValue(COLOR).getId()) || super.skipRendering(centerState, otherState, dir);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter getter, BlockPos pos) {
        return true;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter getter, BlockPos pos) {
        return 1.0F;
    }
}
