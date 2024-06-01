package survivalblock.axolotlamplifier.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import survivalblock.axolotlamplifier.common.component.ConduitAmplifierComponent;
import survivalblock.axolotlamplifier.common.init.AmplifierEntityComponents;

@SuppressWarnings("UnreachableCode")
@Mixin(AxolotlEntity.class)
public abstract class AxolotlEntityMixin extends AnimalEntity {

    @Shadow public abstract boolean isPlayingDead();

    protected AxolotlEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "createChild", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/AxolotlEntity;setPersistent()V"))
    private void addConduitToChild(ServerWorld world, PassiveEntity entity, CallbackInfoReturnable<PassiveEntity> cir, @Local(ordinal = 1) AxolotlEntity child){
        if (entity instanceof AxolotlEntity partner) {
            ConduitAmplifierComponent myConduitComponent = AmplifierEntityComponents.CONDUIT_COMPONENT.get((AxolotlEntity) (Object) this);
            ConduitAmplifierComponent partnerConduitComponent = AmplifierEntityComponents.CONDUIT_COMPONENT.get(partner);
            ConduitAmplifierComponent childConduitComponent = AmplifierEntityComponents.CONDUIT_COMPONENT.get(child);
            childConduitComponent.setHasConduit(myConduitComponent.getHasConduit() && partnerConduitComponent.getHasConduit());
            childConduitComponent.setConduitStack(ItemStack.EMPTY);
        }
    }

    @ModifyVariable(method = "damage", at = @At(value = "HEAD"), index = 2, argsOnly = true)
    private float reduceDamage(float value){
        ConduitAmplifierComponent conduitComponent = AmplifierEntityComponents.CONDUIT_COMPONENT.get((AxolotlEntity) (Object) this);
        if (!conduitComponent.getHasConduit()) {
            return value;
        }
        if (!conduitComponent.getRenderConduit().isActive()) {
            return value;
        }
        if (this.isPlayingDead()) {
            conduitComponent.particleRing();
            return value * 0.05f;
        }
        return value * 0.8f;
    }
}
