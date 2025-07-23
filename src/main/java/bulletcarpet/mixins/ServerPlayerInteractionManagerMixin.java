package bulletcarpet.mixins;

import bulletcarpet.BulletCarpetSettings;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.mojang.text2speech.Narrator.LOGGER;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {

    @Redirect(method = "processBlockBreakingAction",
            at = @At(value = "INVOKE",
                    target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"
            )
    )
    private void silenceDestroyMismatchWarning(Logger instance, String s, Object miningPos, Object pos) {

        if (!BulletCarpetSettings.silenceMismatchDestroyBlock) {
            LOGGER.warn((String) "Mismatch in destroy block pos: {} {}", (Object) miningPos, (Object) pos);
        }

    }
}
