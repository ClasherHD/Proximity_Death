package dev.ClasherHD.deathproximity;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.core.BlockPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@Mod(DeathMessageProximityMod.MODID)
public class DeathMessageProximityMod {
    public static final String MODID = "deathproximity";
    private static final int RADIUS_CHUNKS = 12;
    private static final int RADIUS_BLOCKS = RADIUS_CHUNKS * 16;

    public DeathMessageProximityMod() {
        // Registrierung beim NeoForge Event Bus
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        // Prüfen, ob es ein Spieler ist
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        MinecraftServer server = player.getServer();
        if (server == null) return;

        // Die Todesnachricht holen
        Component deathMessage = event.getSource().getLocalizedDeathMessage(player);

        // Position des Todes
        BlockPos deathPos = player.blockPosition();
        ServerLevel world = player.serverLevel();

        // Gamerule holen und Status speichern
        GameRules rules = server.getGameRules();
        GameRules.BooleanValue showDeathMessages = rules.getRule(GameRules.RULE_SHOWDEATHMESSAGES);
        boolean wasEnabled = showDeathMessages.get();

        try {
            // Vanilla Nachricht temporär deaktivieren
            showDeathMessages.set(false, server);

            // Manuell an Spieler in der Nähe senden
            double radiusSq = RADIUS_BLOCKS * RADIUS_BLOCKS;

            for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                // Nur an Spieler in der gleichen Dimension senden
                if (p.serverLevel() != world) continue;

                // Distanz prüfen
                if (p.distanceToSqr(deathPos.getX(), deathPos.getY(), deathPos.getZ()) <= radiusSq) {
                    p.sendSystemMessage(deathMessage);
                }
            }

        } finally {
            // Gamerule zurücksetzen, damit wir nichts dauerhaft kaputt machen
            showDeathMessages.set(wasEnabled, server);
        }
    }
}