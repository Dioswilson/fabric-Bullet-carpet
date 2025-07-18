package bulletcarpet.mixins;

import bulletcarpet.helpers.CustomStats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;incrementStat(Lnet/minecraft/util/Identifier;)V",
                    ordinal = 0
            ))
    private void addHourStat(CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        if (self instanceof ServerPlayerEntity) {
            ServerStatHandler statHandler = ((ServerPlayerEntity) self).getStatHandler();
            //Hope is not too inaccurate
            if (statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.PLAY_TIME)) % 72000 == 0) {
                self.incrementStat(CustomStats.HOURS_PLAYED);
            }
        }
    }
}
