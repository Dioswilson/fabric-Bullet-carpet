package bulletcarpet.mixins;

import bulletcarpet.BulletCarpetServer;
import bulletcarpet.utils.ModUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {
    @Inject(method = "internalOnSlotClick",
            at = @At("HEAD")
    )
    private void enableShulkerStacking(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        ModUtils.playerInventoryShulkerStacking = true;
    }

    @Inject(method = "internalOnSlotClick",
            at = @At("TAIL")
    )
    private void disableShulkerStacking(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        ModUtils.playerInventoryShulkerStacking = false;
    }
}
