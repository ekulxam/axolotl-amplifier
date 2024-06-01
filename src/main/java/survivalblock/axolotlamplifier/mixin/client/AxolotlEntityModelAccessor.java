package survivalblock.axolotlamplifier.mixin.client;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AxolotlEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AxolotlEntityModel.class)
public interface AxolotlEntityModelAccessor {

    @Accessor("body")
    ModelPart axolotl_amplifier$getBody();
}
