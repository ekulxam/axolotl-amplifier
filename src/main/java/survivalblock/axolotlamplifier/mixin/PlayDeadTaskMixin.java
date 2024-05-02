package survivalblock.axolotlamplifier.mixin;

import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.ai.brain.task.PlayDeadTask;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import survivalblock.axolotlamplifier.common.init.AmplifierEntityComponents;

import java.util.*;


@Mixin(value = PlayDeadTask.class)
public abstract class PlayDeadTaskMixin extends MultiTickTask<AxolotlEntity> {

    public PlayDeadTaskMixin(Map<MemoryModuleType<?>, MemoryModuleState> requiredMemoryState) {
        super(requiredMemoryState);
    }

    @Inject(method="run(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/AxolotlEntity;J)V", at = @At("TAIL"))
    private void addStrength(ServerWorld serverWorld, AxolotlEntity axolotlEntity, long l, CallbackInfo ci){
        if (!AmplifierEntityComponents.CONDUIT_COMPONENT.get(axolotlEntity).getHasConduit()) return;
        List<StatusEffectInstance> axolotlStatusEffectList = new ArrayList<>(axolotlEntity.getStatusEffects());
        for (Iterator<StatusEffectInstance> iterator = axolotlStatusEffectList.iterator(); iterator.hasNext(); ) {
            StatusEffect statusEffectType = iterator.next().getEffectType();
            if(statusEffectType.getCategory().equals(StatusEffectCategory.HARMFUL)){
                axolotlEntity.removeStatusEffect(statusEffectType);
                iterator.remove();
            }
        }
        axolotlEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 400, 1));
        axolotlEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 1200, 0, true, false));
        axolotlEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 1200, 2, true, false));
    }
}