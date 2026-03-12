package dev.ClasherHD.deathproximity;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class DeathProximityConfig {
    public static final ModConfigSpec SERVER_SPEC;
    public static final Server SERVER;

    static {
        final Pair<Server, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Server::new);
        SERVER_SPEC = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    public static class Server {
        public final ModConfigSpec.IntValue radiusChunks;
        public final ModConfigSpec.BooleanValue enableDeathSound;
        public final ModConfigSpec.DoubleValue deathSoundVolume;

        public Server(ModConfigSpec.Builder builder) {
            builder.push("general");

            radiusChunks = builder
                    .defineInRange("radius", 12, 4, 32);

            builder.pop();

            builder.push("sound");

            enableDeathSound = builder
                    .define("enableDeathSound", true);

            deathSoundVolume = builder
                    .defineInRange("deathSoundVolume", 1.0, 0.1, 2.0);

            builder.pop();
        }
    }
}