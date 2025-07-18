package bulletcarpet.mixins;

import bulletcarpet.BulletCarpetSettings;
import carpet.patches.EntityPlayerMPFake;
import com.mojang.authlib.GameProfile;
import net.minecraft.registry.RegistryKey;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EntityPlayerMPFake.class)
public class EntityPlayerMPFakeMixin {

    @Inject(method = "createFake",
            at = @At("TAIL"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private static void addBotToTeamWhenSummoned(String username, MinecraftServer server, double d0, double d1, double d2, double yaw, double pitch,
                                                 RegistryKey<World> dimensionId, GameMode gamemode, boolean flying, CallbackInfoReturnable<EntityPlayerMPFake> cir,
                                                 ServerWorld worldIn, GameProfile gameprofile, EntityPlayerMPFake instance) {
        createAndAddFakeToTeamIfNeeded(instance);
    }

    @Inject(method = "createShadow",
            at = @At("TAIL"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private static void addBotToTeamWhenShadowed(MinecraftServer server, ServerPlayerEntity player, CallbackInfoReturnable<EntityPlayerMPFake> cir, ServerWorld worldIn, GameProfile gameprofile, EntityPlayerMPFake playerShadow) {
        createAndAddFakeToTeamIfNeeded(playerShadow);
    }

    private static void createAndAddFakeToTeamIfNeeded(EntityPlayerMPFake instance) {
        if (BulletCarpetSettings.addBotsToTeam) {
            createAndAddFakePlayerToTeamBot(instance);
        }
    }

    @Unique
    private static void createAndAddFakePlayerToTeamBot(EntityPlayerMPFake player) {
        Scoreboard scoreboard = player.getServer().getWorld(World.OVERWORLD).getScoreboard();
        if (!scoreboard.getTeamNames().contains("Bots")) {
            scoreboard.addTeam("Bots");

            Team scoreplayerteam = scoreboard.getTeam("Bots");
            if (scoreplayerteam != null) {
                Formatting textformatting = Formatting.DARK_GREEN;
                scoreplayerteam.setColor(textformatting);
                scoreplayerteam.setPrefix(Text.literal(textformatting.toString()));
                scoreplayerteam.setSuffix(Text.literal(Formatting.RESET.toString()));
            }
        }
        Team botTeam = scoreboard.getTeam("Bots");
        scoreboard.addPlayerToTeam(player.getName().getString(), botTeam);
    }
}
