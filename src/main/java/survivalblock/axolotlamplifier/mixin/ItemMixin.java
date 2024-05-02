package survivalblock.axolotlamplifier.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import survivalblock.axolotlamplifier.common.component.ConduitAmplifierComponent;
import survivalblock.axolotlamplifier.common.init.AmplifierEntityComponents;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(at = @At("HEAD"), method = "useOnEntity", cancellable = true)
    private void axolotlStuff(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir){
        if (!stack.isOf(Items.CONDUIT) && !stack.isOf(Items.SPONGE)) return;
        if (entity instanceof AxolotlEntity axolotl && entity.isAlive()) {
            ConduitAmplifierComponent conduitComponent = AmplifierEntityComponents.CONDUIT_COMPONENT.get(axolotl);
            if (!axolotl.isBaby()) {
                if (!user.getWorld().isClient) {
                    entity.getWorld().emitGameEvent(entity, GameEvent.EQUIP, entity.getPos());
                    if (stack.isOf(Items.CONDUIT) && !conduitComponent.getHasConduit()) {
                        conduitComponent.setHasConduit(true);
                        conduitComponent.setConduitStack(stack.copyWithCount(1));
                        if (!user.isCreative()) stack.decrement(1);
                    } else if (stack.isOf(Items.SPONGE) && conduitComponent.getHasConduit()) {
                        entity.dropStack(conduitComponent.getConduitStack());
                        conduitComponent.setHasConduit(false);
                        ItemStack wetSpongeStack = Items.WET_SPONGE.getDefaultStack();
                        wetSpongeStack.setNbt(stack.getNbt());
                        user.getInventory().offerOrDrop(wetSpongeStack.copyWithCount(1));
                        if (!user.isCreative()) stack.decrement(1);
                    }
                }
                cir.setReturnValue(ActionResult.success(user.getWorld().isClient));
            }
        }
        cir.setReturnValue(ActionResult.PASS);
    }
}
