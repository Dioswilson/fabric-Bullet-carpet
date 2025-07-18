package bulletcarpet.mixins;

import carpet.script.external.Carpet;
import net.minecraft.network.ClientConnection;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "onPlayerConnect",
            at = @At("TAIL")
    )
    private void removeBotTeam(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        String moddedPlayer = Carpet.isModdedPlayer(player);
        if (moddedPlayer == null) {
            Scoreboard scoreboard = player.getServer().getWorld(World.OVERWORLD).getScoreboard();
            if (scoreboard.getTeamNames().contains("Bots")) {
                Team botTeam = scoreboard.getTeam("Bots");
                if (Objects.equals(scoreboard.getPlayerTeam(player.getName().getString()), botTeam)) {
                    scoreboard.removePlayerFromTeam(player.getName().getString(), botTeam);
                }
            }
        }
    }
}
