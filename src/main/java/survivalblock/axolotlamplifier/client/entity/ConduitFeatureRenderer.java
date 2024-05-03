package survivalblock.axolotlamplifier.client.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.AxolotlEntityModel;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import survivalblock.axolotlamplifier.common.component.ConduitAmplifierComponent;
import survivalblock.axolotlamplifier.common.init.AmplifierEntityComponents;
import survivalblock.axolotlamplifier.mixin.client.AxolotlEntityModelAccessor;
import survivalblock.axolotlamplifier.mixin.client.BuiltinModelItemRendererAccessor;
import survivalblock.axolotlamplifier.mixin.client.ItemRendererAccessor;

public class ConduitFeatureRenderer extends FeatureRenderer<AxolotlEntity, AxolotlEntityModel<AxolotlEntity>> {

    private float prevPitch;
    public ConduitFeatureRenderer(FeatureRendererContext<AxolotlEntity, AxolotlEntityModel<AxolotlEntity>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, AxolotlEntity axolotl, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (axolotl.isBaby()) return;
        boolean showBody = !axolotl.isInvisible();
        MinecraftClient client = MinecraftClient.getInstance();
        boolean translucent = !showBody && !axolotl.isInvisibleTo(client.player);
        boolean showOutline = client.hasOutline(axolotl);
        RenderLayer layer = getRenderLayer(showBody, translucent, showOutline, this.getTexture(axolotl), this.getContextModel());
        if (!showBody) {
            if (layer == null) return;
            VertexConsumerProvider finalVertexConsumerProvider = vertexConsumerProvider;
            vertexConsumerProvider = (layer1 -> finalVertexConsumerProvider.getBuffer(layer));
        }
        ConduitAmplifierComponent conduitComponent = AmplifierEntityComponents.CONDUIT_COMPONENT.get(axolotl);
        if (!conduitComponent.getHasConduit()) return;
        float pitch = MathHelper.lerp(tickDelta, prevPitch, ((AxolotlEntityModelAccessor) (this.getContextModel())).getBody().pitch);
        prevPitch = ((AxolotlEntityModelAccessor) (this.getContextModel())).getBody().pitch;
        matrixStack.push();
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotation(pitch));
        matrixStack.translate(-(axolotl.getBoundingBox().getXLength() / 2f) - 0.13f, -0.75, -0.3f);
        if (!conduitComponent.getRenderConduit().isActive()) matrixStack.translate(0, -0.68, 0);
        BuiltinModelItemRenderer builtinModelItemRenderer = ((ItemRendererAccessor) MinecraftClient.getInstance().getItemRenderer()).getBuiltinModelItemRenderer();
        ((BuiltinModelItemRendererAccessor) builtinModelItemRenderer).getBlockEntityRenderDispatcher().renderEntity(conduitComponent.getRenderConduit(), matrixStack, vertexConsumerProvider, light, LivingEntityRenderer.getOverlay(axolotl, 0.0f));
        matrixStack.pop();
    }

    public static RenderLayer getRenderLayer(boolean showBody, boolean translucent, boolean showOutline, Identifier texture, Model model) {
        if (showOutline) {
            return RenderLayer.getOutline(texture);
        }
        if (translucent) {
            return null;
        }
        if (showBody) {
            return model.getLayer(texture);
        }
        return null;
    }
}
