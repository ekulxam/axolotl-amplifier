package survivalblock.axolotlamplifier.mixin.client;

import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BuiltinModelItemRenderer.class)
public interface BuiltinModelItemRendererAccessor {
    @Accessor("blockEntityRenderDispatcher")
    BlockEntityRenderDispatcher getBlockEntityRenderDispatcher();
}
