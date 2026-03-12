package dev.ClasherHD.deathproximity;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@Mod(DeathMessageProximityMod.MODID)
public class DeathMessageProximityMod {
    public static final String MODID = "deathproximity";

    public DeathMessageProximityMod(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, DeathProximityConfig.SERVER_SPEC);
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        MinecraftServer server = player.getServer();
        if (server == null) return;

        Component deathMessage = event.getSource().getLocalizedDeathMessage(player);

        BlockPos deathPos = player.blockPosition();
        ServerLevel world = player.serverLevel();

        int chunkRadius = DeathProximityConfig.SERVER.radiusChunks.get();
        int blockRadius = chunkRadius * 16;
        double radiusSq = (double) blockRadius * blockRadius;

        boolean playSound = DeathProximityConfig.SERVER.enableDeathSound.get();
        float soundVolume = DeathProximityConfig.SERVER.deathSoundVolume.get().floatValue();

        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            if (p.serverLevel() != world) continue;

            if (p.distanceToSqr(deathPos.getX(), deathPos.getY(), deathPos.getZ()) <= radiusSq) {
                p.sendSystemMessage(deathMessage);

                if (playSound) {
                    p.playNotifySound(SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, soundVolume, 1.0f);
                }
            }
        }
    }
}