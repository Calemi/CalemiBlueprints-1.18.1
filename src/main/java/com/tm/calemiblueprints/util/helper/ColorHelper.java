package com.tm.calemiblueprints.util.helper;

import net.minecraft.world.item.DyeColor;

public class ColorHelper {

    /**
     * Searches all colors for a matching name. Returns BLUE as default.
     * @param name The name of the color.
     */
    public static DyeColor getColorFromString(String name) {

        if (name != null) {

            for (DyeColor color : DyeColor.values()) {

                if (name.equalsIgnoreCase(color.getName())) {
                    return color;
                }
            }
        }

        return DyeColor.BLUE;
    }
}