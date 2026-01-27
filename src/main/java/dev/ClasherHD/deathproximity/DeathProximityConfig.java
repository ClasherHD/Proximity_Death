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

        public Server(ModConfigSpec.Builder builder) {
            builder.push("general");

            radiusChunks = builder
                    .comment("The radius in chunks in which death messages are visible")
                    .comment("Min: 4, Max: 32")
                    .defineInRange("radius", 12, 4, 32);

            builder.pop();
        }
    }
}