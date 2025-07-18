package bulletcarpet.mixins;

import bulletcarpet.utils.ToolItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(
            method = "postMine",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;incrementStat(Lnet/minecraft/stat/Stat;)V"
            )
    )
    private void addToolUses(World world, BlockState state, BlockPos pos, PlayerEntity miner, CallbackInfo ci) {
        String itemName = ((ItemStack) (Object) this).getName().toString();
        Item targetItem = null;

        if (itemName.contains("pickaxe")) {
            targetItem = ToolItems.PICKAXE;
        }
        else if (itemName.contains("shovel")) {
            targetItem = ToolItems.SHOVEL;
        }
        else if (itemName.contains("axe")) {
            targetItem = ToolItems.AXE;
        }
        else if (itemName.contains("hoe")) {
            targetItem = ToolItems.HOE;
        }


        if (targetItem != null) {
            miner.incrementStat(Stats.USED.getOrCreateStat(targetItem));
            miner.incrementStat(Stats.USED.getOrCreateStat(ToolItems.ALL_TOOLS));
        }
    }
}
