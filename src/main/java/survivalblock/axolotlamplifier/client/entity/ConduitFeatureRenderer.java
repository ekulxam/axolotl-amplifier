package survivalblock.axolotlamplifier.client.entity;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.AxolotlEntityModel;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;
import survivalblock.axolotlamplifier.access.ActiveConduitAccess;
import survivalblock.axolotlamplifier.common.component.ConduitAmplifierComponent;
import survivalblock.axolotlamplifier.common.init.AmplifierEntityComponents;
import survivalblock.axolotlamplifier.mixin.BuiltinModelItemRendererAccessor;
import survivalblock.axolotlamplifier.mixin.ItemRendererAccessor;

public class ConduitFeatureRenderer extends FeatureRenderer<AxolotlEntity, AxolotlEntityModel<AxolotlEntity>> {
    public ConduitFeatureRenderer(FeatureRendererContext<AxolotlEntity, AxolotlEntityModel<AxolotlEntity>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, AxolotlEntity axolotl, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (axolotl.isBaby()) return;
        ConduitAmplifierComponent conduitComponent = AmplifierEntityComponents.CONDUIT_COMPONENT.get(axolotl);
        if (!conduitComponent.getHasConduit()) return;
        float pitch = MathHelper.lerp(tickDelta, axolotl.prevPitch, axolotl.getPitch());
        matrixStack.push();
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pitch));
        matrixStack.translate(-(axolotl.getBoundingBox().getXLength() / 2f) - 0.13f, -1.0, -0.3f);
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-pitch));
        BuiltinModelItemRenderer builtinModelItemRenderer = ((ItemRendererAccessor) MinecraftClient.getInstance().getItemRenderer()).getBuiltinModelItemRenderer();
        ((BuiltinModelItemRendererAccessor) builtinModelItemRenderer).getBlockEntityRenderDispatcher().renderEntity(conduitComponent.getRenderConduit(), matrixStack, vertexConsumerProvider, light, LivingEntityRenderer.getOverlay(axolotl, 0.0f));
        matrixStack.pop();
    }
}
