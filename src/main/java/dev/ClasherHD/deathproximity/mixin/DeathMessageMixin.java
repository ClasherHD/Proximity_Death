package dev.ClasherHD.deathproximity.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class DeathMessageMixin {

    @Inject(method = "die", at = @At("HEAD"))
    private void deathproximity$suppressDeathMessage(DamageSource source, CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        GameRules.BooleanValue rule = self.server.getGameRules().getRule(GameRules.RULE_SHOWDEATHMESSAGES);
        if (rule.get()) {
            rule.set(false, self.server);
        }
    }

    @Inject(method = "die", at = @At("TAIL"))
    private void deathproximity$restoreDeathMessage(DamageSource source, CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        GameRules.BooleanValue rule = self.server.getGameRules().getRule(GameRules.RULE_SHOWDEATHMESSAGES);
        if (!rule.get()) {
            rule.set(true, self.server);
        }
    }
}
