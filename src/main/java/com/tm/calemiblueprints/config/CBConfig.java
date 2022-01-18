package com.tm.calemiblueprints.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class CBConfig {

    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

    public static final CategoryServer server = new CategoryServer(SERVER_BUILDER);

    public static void init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_BUILDER.build());
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_BUILDER.build());
    }

    public static class CategoryServer {

        public final ForgeConfigSpec.ConfigValue<Integer> blockScannerMaxSize;
        public final ForgeConfigSpec.ConfigValue<Integer> worldEditMaxSize;

        public CategoryServer (ForgeConfigSpec.Builder builder) {

            blockScannerMaxSize = builder.comment("Block Scanner Max Size",
                    "The Block Scanner is a system used by Blueprints & Scaffolds",
                    "It scans for blocks in a chain. The max size is how many chains will occur. Lower values run faster on servers.",
                    "2304 is the maximum count of blocks in a single Player inventory. There is no real reason to set it above, but the option is there")
                    .defineInRange("veinScanMaxSize", 2304, 0, 5000);

            worldEditMaxSize = builder.comment("Brush Max Size", "0 to Disable. The max size of blocks the Brush can place. Lower values run faster on servers.").defineInRange("worldEditMaxSize", 10000, 0, 20000);
        }
    }
}