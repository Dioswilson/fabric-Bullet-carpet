package bulletcarpet.mixins;

import bulletcarpet.BulletCarpetSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {

    @Inject(method = "calcBlockBreakingDelta",
            at = @At("HEAD"),
            cancellable = true
    )
    public void instaMineDeepslate(BlockState state, PlayerEntity player, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        float f = state.getHardness(world, pos);
        if (f != -1.0F) {
            float breakingSpeed = player.getBlockBreakingSpeed(state);
            if (BulletCarpetSettings.instamineDeepslate && state.getBlock().equals(Blocks.DEEPSLATE)) {
                if (breakingSpeed >= 48.9F) { //Netherite+eff5+haste2
                    cir.setReturnValue(1.1F);//>1 means instamine
                }
            }
        }
    }
}
