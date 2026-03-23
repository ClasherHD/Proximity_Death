package dev.ClasherHD.deathproximity.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class DeathMessageMixin {

    @Inject(method = "broadcastSystemMessage", at = @At("HEAD"), cancellable = true)
    private void deathproximity$suppressGlobalDeathMessage(Component message, boolean bypassHiddenChat, CallbackInfo ci) {
        if (message.getContents() instanceof TranslatableContents contents) {
            if (contents.getKey().startsWith("death.")) {
                ci.cancel();
            }
        }
    }
}
