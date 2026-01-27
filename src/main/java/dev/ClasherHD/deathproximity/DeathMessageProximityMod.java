package dev.ClasherHD.deathproximity;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.core.BlockPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@Mod(DeathMessageProximityMod.MODID)
public class DeathMessageProximityMod {
    public static final String MODID = "deathproximity";

    // Konstruktor mit ModContainer für die Config
    public DeathMessageProximityMod(ModContainer modContainer) {
        // Config registrieren
        modContainer.registerConfig(ModConfig.Type.SERVER, DeathProximityConfig.SERVER_SPEC);

        // Event Bus registrieren
        NeoForge.EVENT_BUS.register(this);
    }

    // Automatisch Gamerule setzen beim Serverstart
    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();
        GameRules rules = server.getGameRules();

        // Erzwingen 'false', damit Vanilla keine globalen Nachrichten sendet
        GameRules.BooleanValue showDeathMessages = rules.getRule(GameRules.RULE_SHOWDEATHMESSAGES);
        if (showDeathMessages.get()) {
            showDeathMessages.set(false, server);
            // Log-Ausgabe zur Bestätigung (Optional)
            // System.out.println("DeathProximity: Gamerule automatisch deaktiviert.");
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        MinecraftServer server = player.getServer();
        if (server == null) return;

        // Nachricht holen
        Component deathMessage = event.getSource().getLocalizedDeathMessage(player);

        BlockPos deathPos = player.blockPosition();
        ServerLevel world = player.serverLevel();

        // Radius aus der Config holen
        int chunkRadius = DeathProximityConfig.SERVER.radiusChunks.get();
        int blockRadius = chunkRadius * 16;
        double radiusSq = blockRadius * blockRadius;

        // An Spieler in der Nähe senden
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            // Prüft gleiche Dimension
            if (p.serverLevel() != world) continue;

            // Distanz checken (Kugel-Radius)
            if (p.distanceToSqr(deathPos.getX(), deathPos.getY(), deathPos.getZ()) <= radiusSq) {
                p.sendSystemMessage(deathMessage);
            }
        }
    }
}
