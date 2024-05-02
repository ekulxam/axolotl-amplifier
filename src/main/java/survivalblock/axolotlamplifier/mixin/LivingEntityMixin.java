package survivalblock.axolotlamplifier.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import survivalblock.axolotlamplifier.common.component.ConduitAmplifierComponent;
import survivalblock.axolotlamplifier.common.init.AmplifierEntityComponents;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "drop", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;dropLoot(Lnet/minecraft/entity/damage/DamageSource;Z)V"))
    private void dropConduitOnDeath(DamageSource source, CallbackInfo ci){
        if ((LivingEntity) (Object) this instanceof AxolotlEntity axolotl) {
            AmplifierEntityComponents.CONDUIT_COMPONENT.get(axolotl).remove();
        }
    }
}
