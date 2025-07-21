package bulletcarpet.mixins;

import bulletcarpet.BulletCarpetSettings;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Random.class)
public abstract class RandomMixin {
    @Shadow
    public abstract double nextDouble();

    @Inject(method = "nextTriangular",
            at = @At("HEAD"),
            cancellable = true
    )
    private void nextRectangular(double mode, double deviation, CallbackInfoReturnable<Double> cir) {
        if (BulletCarpetSettings.extremeBehaviours) {
            this.nextDouble(); //To keep the seed the same

            double rectangularResult = mode + deviation * (2 * this.nextDouble() - 1);

            cir.setReturnValue(rectangularResult);
        }
    }

}
