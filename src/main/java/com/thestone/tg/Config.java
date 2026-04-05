package com.thestone.tg;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;


public class Config {
    public static final ModConfigSpec COMMON_SPEC;
    public static final CommonConfig COMMON;

    static {
        final Pair<CommonConfig, ModConfigSpec> specPair =
                new ModConfigSpec.Builder().configure(CommonConfig::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static class CommonConfig {
        public final ModConfigSpec.DoubleValue BASE_DRAIN;
        public final ModConfigSpec.DoubleValue EXHAUSTION_MULTIPLIER;
        public final ModConfigSpec.BooleanValue HUNGER_IN_PEACEFUL;
        public final ModConfigSpec.IntValue MAX_GHOUL_HUNGER;
        public final ModConfigSpec.DoubleValue HUNGER_DRAIN_MULTIPLIER;

        public CommonConfig(ModConfigSpec.Builder builder) {
            builder.push("ghoul_hunger");

            BASE_DRAIN = builder
                    .comment("Базовая трата истощения за тик. 0.00375 = ванильный темп покоя. Увеличьте для более быстрой траты.")
                    .defineInRange("baseDrain", 0.00375, 0.001, 0.02);

            EXHAUSTION_MULTIPLIER = builder
                    .comment("Глобальный множитель. 1.0 = стандарт, 0.5 = в 2 раза медленнее, 2.0 = в 2 раза быстрее")
                    .defineInRange("exhaustionMultiplier", 0.8, 0.1, 5.0);

            HUNGER_IN_PEACEFUL = builder
                    .comment("Should ghoul hunger drain in Peaceful difficulty?")
                    .define("hungerInPeaceful", true);

            MAX_GHOUL_HUNGER = builder
                    .comment("Maximum ghoul hunger value (default: 20)")
                    .defineInRange("maxHunger", 20, 10, 40);

            HUNGER_DRAIN_MULTIPLIER = builder
                    .comment("Multiplier for hunger exhaustion (lower = slower drain)")
                    .defineInRange("drainMultiplier", 0.5, 0.1, 2.0);

            builder.pop();
        }
    }
    public static ModConfigSpec.DoubleValue BASE_DRAIN = COMMON.BASE_DRAIN;
    public static ModConfigSpec.DoubleValue EXHAUSTION_MULTIPLIER = COMMON.EXHAUSTION_MULTIPLIER;
    public static ModConfigSpec.BooleanValue HUNGER_IN_PEACEFUL = COMMON.HUNGER_IN_PEACEFUL;


}
