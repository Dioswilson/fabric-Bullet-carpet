package bulletcarpet.mixins;

import bulletcarpet.BulletCarpetSettings;
import bulletcarpet.utils.ModUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.fabricmc.fabric.impl.registry.sync.packet.RegistryPacketHandler;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;
import java.util.Map.Entry;

@Mixin(RegistrySyncManager.class)
public class RegistrySyncManagerMixin {
    //Note: Might be troublesome using this mixin, but some users get kicked at login

    @Inject(method = "sendPacket(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/fabricmc/fabric/impl/registry/sync/packet/RegistryPacketHandler;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/fabricmc/fabric/impl/registry/sync/packet/RegistryPacketHandler;sendPacket(Lnet/minecraft/server/network/ServerPlayerEntity;Ljava/util/Map;)V"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void intersectBulletsItems(ServerPlayerEntity player, RegistryPacketHandler handler, CallbackInfo ci, Map<Identifier, Object2IntMap<Identifier>> map) {
        for (Entry<Identifier, Object2IntMap<Identifier>> entry : map.entrySet()) {
            Object2IntMap<Identifier> value = entry.getValue();
            value.forEach((identifier, i) -> {
                if (identifier.getNamespace().equals(ModUtils.NAMESPACE)
                        || identifier.getPath().contains("pottery")) { //Note: Might cause an issue if FeatureFlag.UPDATE_1_20 is on
                    //Note: Pottery items are ignored to mantain compatibility with 1.20 clients with Viaversion
                    value.remove(identifier); //Note: Deprecated, but works
                }
            });
        }
    }
}
