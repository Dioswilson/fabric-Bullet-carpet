package bulletcarpet.commands.cameramode;

import bulletcarpet.BulletCarpetSettings;
import bulletcarpet.helpers.CameraModeHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

import static net.minecraft.server.command.CommandManager.literal;

public class CommandGMS {

    private static final String NAME = "s";

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {

        LiteralArgumentBuilder<ServerCommandSource> commandBuilder = literal(NAME).requires(source -> BulletCarpetSettings.commandCameramode && source.isExecutedByPlayer());

        commandBuilder.executes(c -> {
            ServerPlayerEntity player = c.getSource().getPlayer();

            if (player != null) {

                if (player.interactionManager.getGameMode() != GameMode.SURVIVAL) {
                    StatusEffect nightVisionEffect = Registries.STATUS_EFFECT.get(new Identifier("minecraft", "night_vision"));

                    if (player.hasStatusEffect(nightVisionEffect)) {
                        int effectLevel = player.getStatusEffect(nightVisionEffect).getAmplifier();
                        if (effectLevel >= 1) {
                            player.removeStatusEffect(nightVisionEffect);
                        }
                    }

                    if (BulletCarpetSettings.cameraModeRestoreLocation) {
                        CameraModeHelper.getInstance().restorePlayerPos(player);
                    }
                    player.changeGameMode(GameMode.SURVIVAL);
                }
            }
            return 0;
        });


        dispatcher.register(commandBuilder);
    }
}
