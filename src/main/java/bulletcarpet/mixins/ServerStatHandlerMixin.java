package bulletcarpet.mixins;

import bulletcarpet.BulletCarpetSettings;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerStatHandler.class)
public class ServerStatHandlerMixin {
    @Redirect(method = "sendStats",
            at = @At(
                    value = "INVOKE",
                    target = "Lit/unimi/dsi/fastutil/objects/Object2IntMap;put(Ljava/lang/Object;I)I"
            ))
    private int addVanillaStatsToSend(Object2IntMap instance, Object o, int i) {
        Stat<?> stat = (Stat<?>) o;
        if (isVanillaStat(stat)) {
            instance.put(o, i);
        }
        return 0;
    }

    private boolean isVanillaStat(Stat<?> stat) {
        return !(stat.getName().contains(BulletCarpetSettings.NAMESPACE) || stat.getName().contains("hours_played"));
    }

}
