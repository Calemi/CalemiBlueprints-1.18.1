package com.tm.calemiblueprints.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.world.item.DyeColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DyeColorArgument implements ArgumentType<String> {

    private static final Collection<String> EXAMPLES = Arrays.asList("red", "green");

    public static DyeColorArgument color() {
        return new DyeColorArgument();
    }

    public static String getColor (CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public String parse (StringReader reader) {

        String colorArg = reader.readUnquotedString();

        for (DyeColor color : DyeColor.values()) {

            if (colorArg.equalsIgnoreCase(color.getName())) {
                return color.getName();
            }
        }

        return "blue";
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions (CommandContext<S> context, SuggestionsBuilder builder) {

        List<String> collection = new ArrayList<>();

        for (DyeColor color : DyeColor.values()) {
            collection.add(color.getName());
        }

        return SharedSuggestionProvider.suggest(collection, builder);
    }

    @Override
    public Collection<String> getExamples () {
        return EXAMPLES;
    }
}
