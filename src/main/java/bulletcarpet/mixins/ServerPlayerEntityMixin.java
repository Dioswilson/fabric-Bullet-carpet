package bulletcarpet.mixins;

import bulletcarpet.BulletCarpetSettings;
import bulletcarpet.helpers.StatsHelper;
import carpet.script.external.Carpet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "increaseStat", at = @At("TAIL"))
    private void increaseTotal(Stat<?> stat, int amount, CallbackInfo ci) {
        if (BulletCarpetSettings.scoreboardStats) {
            ((ServerPlayerEntity) (Object) this).getScoreboard().forEachScore(stat, StatsHelper.TOTAL_USER_NAME, (score) -> {
                score.incrementScore(amount);
            });
        }
    }

    @Inject(method = "increaseStat", at = @At("HEAD"), cancellable = true)
    private void skipStatsIfBot(Stat<?> stat, int amount, CallbackInfo ci) {
        if (BulletCarpetSettings.botsNoStats) {
            String playerType = Carpet.isModdedPlayer((ServerPlayerEntity) (Object) this);
            if (playerType != null) {
                if (playerType.equals("fake")) {
                    ci.cancel();
                }
            }
        }
    }
}
