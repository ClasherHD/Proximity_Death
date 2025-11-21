package com.example.deathproximity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod(DeathMessageProximityMod.MODID)
public class DeathMessageProximityMod {
    public static final String MODID = "deathproximity";

    private static final int RADIUS_CHUNKS = 8;
    private static final int RADIUS_BLOCKS = RADIUS_CHUNKS * 16;

    public DeathMessageProximityMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer)) return;
        ServerPlayer dead = (ServerPlayer) event.getEntity();
        ServerLevel world = (ServerLevel) dead.getCommandSenderWorld();
        MinecraftServer server = world.getServer();
        if (server == null) return;

        Component deathMsg = event.getEntity().getDeathMessage(event.getSource());
        if (deathMsg == null) return;

        BlockPos deathPos = dead.blockPosition();
        double radiusSq = (double) RADIUS_BLOCKS * (double) RADIUS_BLOCKS;

        boolean prevShow = server.getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_SHOW_DEATH_MESSAGES);
        try {
            server.getGameRules().get(net.minecraft.world.level.GameRules.RULE_SHOW_DEATH_MESSAGES).set(false, server);

            List<ServerPlayer> players = server.getPlayerList().getPlayers();
            for (ServerPlayer p : players) {
                try {
                    if (!p.getLevel().dimension().equals(world.dimension())) continue;

                    Vec3 pv = p.position();
                    double dx = pv.x() - (deathPos.getX() + 0.5);
                    double dy = pv.y() - (deathPos.getY() + 0.5);
                    double dz = pv.z() - (deathPos.getZ() + 0.5);
                    double distSq = dx * dx + dy * dy + dz * dz;

                    if (distSq <= radiusSq) {
                        p.sendSystemMessage(deathMsg, net.minecraft.server.level.ServerPlayer.NO_SIGNALS);
                    }
                } catch (Throwable t) { t.printStackTrace(); }
            }
        } finally {
            try { server.getGameRules().get(net.minecraft.world.level.GameRules.RULE_SHOW_DEATH_MESSAGES).set(prevShow, server); }
            catch (Throwable ignored) {}
        }
    }
}
