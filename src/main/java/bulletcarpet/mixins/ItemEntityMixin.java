package bulletcarpet.mixins;

import bulletcarpet.utils.ModUtils;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @Inject(method = "onPlayerCollision",
            at = @At("HEAD")
    )
    private void enableShulkerStacking(PlayerEntity player, CallbackInfo ci) {
        ModUtils.playerInventoryShulkerStacking = true;
    }

    @Inject(method = "onPlayerCollision",
            at = @At("TAIL")
    )
    private void disableShulkerStackin(PlayerEntity player, CallbackInfo ci) {
        ModUtils.playerInventoryShulkerStacking = false;
    }
}
