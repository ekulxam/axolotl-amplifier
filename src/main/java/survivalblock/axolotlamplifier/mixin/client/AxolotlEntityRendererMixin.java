package survivalblock.axolotlamplifier.mixin.client;

import net.minecraft.client.render.entity.AxolotlEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.AxolotlEntityModel;
import net.minecraft.entity.passive.AxolotlEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import survivalblock.axolotlamplifier.client.entity.ConduitFeatureRenderer;

@Mixin(AxolotlEntityRenderer.class)
public abstract class AxolotlEntityRendererMixin extends MobEntityRenderer<AxolotlEntity, AxolotlEntityModel<AxolotlEntity>> {

    public AxolotlEntityRendererMixin(EntityRendererFactory.Context context, AxolotlEntityModel<AxolotlEntity> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void addConduitRenderer(EntityRendererFactory.Context context, CallbackInfo ci){
        this.addFeature(new ConduitFeatureRenderer((AxolotlEntityRenderer) (Object) this));
    }
}
