package com.tm.calemiblueprints.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tm.calemiblueprints.block.BlockBlueprint;
import com.tm.calemiblueprints.config.CBConfig;
import com.tm.calemiblueprints.init.InitItems;
import com.tm.calemiblueprints.item.ItemBrush;
import com.tm.calemiblueprints.util.helper.ColorHelper;
import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.helper.ItemHelper;
import com.tm.calemicore.util.helper.SoundHelper;
import com.tm.calemicore.util.helper.StringHelper;
import com.tm.calemicore.util.helper.WorldEditHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Objects;

public class BrushCommand {

    /**
     * Registers all the commands.
     */
    public static void register (CommandDispatcher<CommandSourceStack> dispatcher) {

        LiteralArgumentBuilder<CommandSourceStack> cuCommand = Commands.literal("cbrush");

        cuCommand.requires(commandSource -> true)
                .then(help())
                .then(brushWithHollow("fill"))
                .then(recolor())
                .then(brush("walls"))
                .then(brushCircular("circle"))
                .then(brushCircular("cylinder"))
                .then(brushCircular("sphere"))
                .then(brushWithHollow("pyramid"));

        dispatcher.register(cuCommand);
    }

    /**
     * The help command.
     */
    private static ArgumentBuilder<CommandSourceStack, ?> help () {

        return Commands.literal("help").executes(ctx -> {

            Player player = ctx.getSource().getPlayerOrException();

            for (int i = 1; i <= 11; i++) {
                ItemBrush.MESSENGER.sendMessage(ItemBrush.MESSENGER.getMessage("help-line." + i), player);
            }

            return Command.SINGLE_SUCCESS;
        });
    }

    /**
     * The Brush commands that have a "hollow" argument.
     */
    private static ArgumentBuilder<CommandSourceStack, ?> brushWithHollow (String shape) {
        return Commands.literal(shape).executes(ctx -> 0)
                .then(Commands.argument("color", DyeColorArgument.color())
                        .executes(ctx -> executeBrush(
                                ctx.getSource().getPlayerOrException(),
                                shape,
                                DyeColorArgument.getColor(ctx, "color"),
                                null,
                                false,
                                1))
                        .then(Commands.literal("hollow")
                                .executes(ctx -> executeBrush(
                                        ctx.getSource().getPlayerOrException(),
                                        shape, DyeColorArgument.getColor(ctx, "color"),
                                        null,
                                        true,
                                        1))));
    }

    /**
     * The recolor command.
     */
    private static ArgumentBuilder<CommandSourceStack, ?> recolor () {
        return Commands.literal("recolor")
                .executes(ctx -> 0).then(Commands.argument("color1", DyeColorArgument.color())
                        .executes(ctx -> 0)
                        .then(Commands.argument("color2", DyeColorArgument.color())
                                .executes(ctx -> executeBrush(
                                        ctx.getSource().getPlayerOrException(),
                                        "recolor",
                                        DyeColorArgument.getColor(ctx, "color1"),
                                        DyeColorArgument.getColor(ctx, "color2"),
                                        false,
                                        1))));
    }

    /**
     * The Brush commands that have no extra arguments.
     */
    @SuppressWarnings("SameParameterValue")
    private static ArgumentBuilder<CommandSourceStack, ?> brush (String shape) {
        return Commands.literal(shape)
                .executes(ctx -> 0)
                .then(Commands.argument("color", DyeColorArgument.color())
                        .executes(ctx -> executeBrush(ctx.getSource().getPlayerOrException(),
                                shape,
                                DyeColorArgument.getColor(ctx, "color"),
                                null,
                                false,
                                1)));
    }

    /**
     * The circular Brush commands.
     */
    private static ArgumentBuilder<CommandSourceStack, ?> brushCircular (String shape) {
        return Commands.literal(shape)
                .executes(ctx -> 0)
                .then(Commands.argument("color", DyeColorArgument.color())
                        .executes(ctx -> executeBrush(
                                ctx.getSource().getPlayerOrException(),
                                shape,
                                DyeColorArgument.getColor(ctx, "color"),
                                null,
                                false,
                                1))
                        .then(Commands.literal("hollow")
                                .executes(ctx -> executeBrush(
                                        ctx.getSource().getPlayerOrException(),
                                        shape,
                                        DyeColorArgument.getColor(ctx, "color"),
                                        null,
                                        true,
                                        1))
                                .then(Commands.argument("thickness", IntegerArgumentType.integer(1, 128))
                                        .executes(ctx -> executeBrush(
                                                ctx.getSource().getPlayerOrException(),
                                                shape,
                                                DyeColorArgument.getColor(ctx, "color"),
                                                null,
                                                true,
                                                IntegerArgumentType.getInteger(ctx, "thickness"))))));
    }

    /**
     * Handles all of the Brush commands.
     * @param shape Used to determine what specific shape command was executed.
     * @param strColor1 Used to color the Blueprint placed.
     * @param strColor2 Used for the mask for the Blueprint to replace.
     * @param hollow Used to determine if the shape is hollow.
     * @param thickness The amount of thickness of the shape.
     */
    private static int executeBrush (ServerPlayer player, String shape, String strColor1, String strColor2, boolean hollow, int thickness) {

        DyeColor color1 = ColorHelper.getColorFromString(strColor1);
        DyeColor color2 = ColorHelper.getColorFromString(strColor2);

        //Determines which hand has a Brush
        ItemStack stackMainHand = player.getMainHandItem();
        ItemStack stackOffHand = player.getOffhandItem();

        InteractionHand hand = null;

        if (stackMainHand.getItem() instanceof ItemBrush) {
            hand = InteractionHand.MAIN_HAND;
        }

        else if (stackOffHand.getItem() instanceof ItemBrush) {
            hand = InteractionHand.OFF_HAND;
        }

        //Checks if there is a Brush held.
        if (hand == null) {
            ItemBrush.MESSENGER.sendErrorMessage(ItemBrush.MESSENGER.getMessage("error.held"), player);
            return 0;
        }

        Location pos1 = ItemBrush.getPosition(player.getLevel(), player.getItemInHand(hand), 1);
        Location pos2 = ItemBrush.getPosition(player.getLevel(), player.getItemInHand(hand), 2);

        //Checks if both Locations have been set.
        if (pos1 != null && pos2 != null) {

            ArrayList<Location> blocksToPlace = new ArrayList<>();
            BlockBlueprint BLUEPRINT = (BlockBlueprint) Objects.requireNonNull(InitItems.BLUEPRINT.get());

            if (shape.equalsIgnoreCase("fill")) {

                if (hollow) blocksToPlace = WorldEditHelper.selectHollowCubeFromTwoPoints(pos1, pos2);
                else blocksToPlace = WorldEditHelper.selectCubeFromTwoPoints(pos1, pos2);
            }

            else if (shape.equalsIgnoreCase("recolor")) {

                blocksToPlace = WorldEditHelper.selectCubeFromTwoPoints(pos1, pos2);
                generateBlocks(blocksToPlace, BLUEPRINT.defaultBlockState().setValue(BlockBlueprint.COLOR, color2), BLUEPRINT.defaultBlockState().setValue(BlockBlueprint.COLOR, color1), player);
                return 1;
            }

            else if (shape.equalsIgnoreCase("walls")) {

                int xzRad;

                if (pos1.x == pos2.x) {
                    xzRad = Math.abs(pos2.z - pos1.z);
                }

                else if (pos1.z == pos2.z) {
                    xzRad = Math.abs(pos2.x - pos1.x);
                }

                else {
                    ItemBrush.MESSENGER.sendErrorMessage(ItemBrush.MESSENGER.getMessage("error.walls"), player);
                    return 0;
                }

                blocksToPlace = WorldEditHelper.selectWallsFromRadius(pos1, xzRad, pos1.y, pos2.y);
            }

            else if (shape.equalsIgnoreCase("circle")) {
                blocksToPlace = WorldEditHelper.selectCircleFromTwoPoints(pos1, pos2, hollow, thickness);
            }

            else if (shape.equalsIgnoreCase("cylinder")) {
                blocksToPlace = WorldEditHelper.selectCylinderFromTwoPoints(pos1, pos2, hollow, thickness);
            }

            else if (shape.equalsIgnoreCase("sphere")) {
                blocksToPlace = WorldEditHelper.selectSphereFromTwoPoints(pos1, pos2, hollow, thickness);
            }

            else if (shape.equalsIgnoreCase("pyramid")) {

                int xyzRad;

                if (pos1.x == pos2.x && pos1.z == pos2.z) {
                    xyzRad = Math.abs(pos2.y - pos1.y);
                }

                else if (pos1.x == pos2.x) {
                    xyzRad = Math.abs(pos2.z - pos1.z);
                }

                else if (pos1.z == pos2.z) {
                    xyzRad = Math.abs(pos2.x - pos1.x);
                }

                else {
                    ItemBrush.MESSENGER.sendErrorMessage(ItemBrush.MESSENGER.getMessage("error.pyramid"), player);
                    return 0;
                }

                blocksToPlace = WorldEditHelper.selectPyramidFromRadius(pos1, xyzRad, hollow);
            }

            if (!blocksToPlace.isEmpty()) {
                generateBlocks(blocksToPlace, BLUEPRINT.defaultBlockState().setValue(BlockBlueprint.COLOR, color1), Blocks.AIR.defaultBlockState(), player);
            }

            return Command.SINGLE_SUCCESS;
        }

        else {
            ItemBrush.MESSENGER.sendErrorMessage(ItemBrush.MESSENGER.getMessage("error.nullpos"), player);
            return 0;
        }
    }

    private static void generateBlocks(ArrayList<Location> list, BlockState blockToPlace, BlockState mask, Player player) {

        if (CBConfig.server.worldEditMaxSize.get() == 0) {
            ItemBrush.MESSENGER.sendErrorMessage(ItemBrush.MESSENGER.getMessage("error.disabled"), player);
            return;
        }

        if (list.size() > CBConfig.server.worldEditMaxSize.get()) {
            ItemBrush.MESSENGER.sendErrorMessage(ItemBrush.MESSENGER.getMessage("error.too-much-fill"), player);
            ItemBrush.MESSENGER.sendErrorMessage(ItemBrush.MESSENGER.getMessage("error.too-much-fill.over", StringHelper.insertCommas(list.size() - CBConfig.server.worldEditMaxSize.get())), player);
            return;
        }

        int count = 0;

        for (Location nextLocation : list) {

            if (mask == Blocks.AIR.defaultBlockState() && nextLocation.isBlockValidForPlacing()) {

                count++;
                nextLocation.setBlock(blockToPlace);
            }

            else if (nextLocation.getBlockState() == mask) {

                count++;
                nextLocation.setBlock(blockToPlace);
            }
        }

        ItemBrush.MESSENGER.sendMessage(ItemBrush.MESSENGER.getMessage("placed").append(" ").append(ItemHelper.countByStacks(count)), player);
        SoundHelper.playSimple(player, SoundEvents.EXPERIENCE_ORB_PICKUP);
    }
}
