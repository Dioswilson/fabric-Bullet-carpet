package bulletcarpet.mixins;

import bulletcarpet.BulletCarpetSettings;
import bulletcarpet.utils.ModUtils;
import bulletcarpet.utils.ToolItems;
import carpet.helpers.InventoryHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {


    @Inject(method = "postMine", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;incrementStat(Lnet/minecraft/stat/Stat;)V"))
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

    @Inject(method = "getMaxCount", at = @At("RETURN"), cancellable = true)
    public void getMaxCount(CallbackInfoReturnable<Integer> cir) {
        if (BulletCarpetSettings.stackableShulkersPlayerInventory && ModUtils.playerInventoryShulkerStacking) {
            ItemStack self = (ItemStack) (Object) this;
            Item item = self.getItem();

            boolean isShulker = item instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock;
            if (!self.hasNbt() && isShulker) {
                if (!InventoryHelper.shulkerBoxHasItems(self)) {
                    self.removeSubNbt("BlockEntityTag");
                    cir.setReturnValue(64);
                }
            }
        }
    }

    @Inject(method = "isStackable", at = @At("HEAD"))
    private void testing(CallbackInfoReturnable<Boolean> cir) {
        ItemStack self = (ItemStack) (Object) this;
        int count = self.getMaxCount();
        boolean damaged = self.isDamaged();
        boolean damageable = self.isDamageable();
        System.out.println("Watch vars: " + self.getName().getString() + " count: " + count + " damaged: " + damaged + " damageable: " + damageable);
    }

}