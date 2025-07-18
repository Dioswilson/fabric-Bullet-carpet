package bulletcarpet.mixins;

import bulletcarpet.BulletCarpetSettings;
import carpet.commands.TickCommand;
import carpet.helpers.TickSpeed;
import carpet.utils.Messenger;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TickCommand.class)
public class TickCommandMixin {
    @Redirect(method = "setWarp",
            at = @At(value = "INVOKE",
                    target = "Lcarpet/helpers/TickSpeed;tickrate_advance(Lnet/minecraft/server/network/ServerPlayerEntity;ILjava/lang/String;Lnet/minecraft/server/command/ServerCommandSource;)Lnet/minecraft/text/Text;"))
    private static Text warpWithoutTime(ServerPlayerEntity player, int advance, String callback, ServerCommandSource source) {
        long advancer = advance;

        if (BulletCarpetSettings.tickWarpNoTime) {
            if (advancer == 0) {
                if (TickSpeed.time_bias <= 0) {
                    advancer = Long.MAX_VALUE;
                }
            }

        }
        return tickrate_advance(player, advancer, callback, source);
    }

    //Changing tickrate_advance into long
    @Unique
    private static Text tickrate_advance(ServerPlayerEntity player, long advance, String callback, ServerCommandSource source) {
        if (0 == advance) {
            TickSpeed.tick_warp_callback = null;
            if (source != TickSpeed.tick_warp_sender) {
                TickSpeed.tick_warp_sender = null;
            }
            if (TickSpeed.time_bias > 0) {
                TickSpeed.finish_time_warp();
                TickSpeed.tick_warp_sender = null;
                return Messenger.c("gi Warp interrupted");
            }
            return Messenger.c("ri No warp in progress");
        }
        if (TickSpeed.time_bias > 0) {
            String who = "Another player";
            if (TickSpeed.time_advancerer != null) {
                who = TickSpeed.time_advancerer.getEntityName();
            }
            return Messenger.c("l " + who + " is already advancing time at the moment. Try later or ask them");
        }
        TickSpeed.time_advancerer = player;
        TickSpeed.time_warp_start_time = System.nanoTime();
        TickSpeed.time_warp_scheduled_ticks = advance;
        TickSpeed.time_bias = advance;
        TickSpeed.tick_warp_callback = callback;
        TickSpeed.tick_warp_sender = source;
        return Messenger.c("gi Warp speed ....");
    }
}
