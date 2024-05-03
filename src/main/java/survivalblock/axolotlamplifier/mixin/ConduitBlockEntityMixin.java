package survivalblock.axolotlamplifier.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import survivalblock.axolotlamplifier.access.ActiveConduitAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(ConduitBlockEntity.class)
public abstract class ConduitBlockEntityMixin implements ActiveConduitAccess {
    @Shadow private boolean active;
    @Shadow public int ticks;
    @Shadow private float ticksActive;
    @Unique private static boolean axolotl_amplifier$shouldBypass;
    @Unique private final static List<BlockPos> axolotl_amplifier$DUMMY_LIST = new ArrayList<>();
    @Shadow protected abstract void setEyeOpen(boolean eyeOpen);

    @Shadow
    private static void givePlayersEffects(World world, BlockPos pos, List<BlockPos> activatingBlocks) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private static void attackHostileEntity(World world, BlockPos pos, BlockState state, List<BlockPos> activatingBlocks, ConduitBlockEntity blockEntity) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private static void updateTargetEntity(World world, BlockPos pos, ConduitBlockEntity blockEntity) {
        throw new UnsupportedOperationException();
    }

    @Shadow private @Nullable LivingEntity targetEntity;

    @Shadow private @Nullable UUID targetUuid;

    @Override
    public void axolotl_amplifier$setActive(boolean ticks) {
        this.active = ticks;
    }

    @Override
    public void axolotl_amplifier$setTicks(int ticks) {
        this.ticks = ticks;
    }

    @Override
    public void axolotl_amplifier$setTicksActive(int ticks) {
        this.ticksActive = ticks;
    }

    @Override
    public void axolotl_amplifier$invokeSetEyeOpen(boolean open) {
        this.setEyeOpen(open);
    }

    @Override
    public void axolotl_amplifier$invokeGivePlayersEffects(World world, BlockPos pos) {
        axolotl_amplifier$shouldBypass = true;
        givePlayersEffects(world, pos, axolotl_amplifier$DUMMY_LIST);
    }
    @Override
    public void axolotl_amplifier$invokeAttackHostileEntity(World world, BlockPos pos, ConduitBlockEntity conduit) {
        axolotl_amplifier$shouldBypass = true;
        attackHostileEntity(world, pos, Blocks.CONDUIT.getDefaultState(), axolotl_amplifier$DUMMY_LIST, conduit);
    }

    @Override
    public void axolotl_amplifier$invokeUpdateTargetEntity(World world, BlockPos pos, ConduitBlockEntity conduit) {
        updateTargetEntity(world, pos, conduit);
    }

    @ModifyExpressionValue(method = {"givePlayersEffects", "attackHostileEntity"}, at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"))
    private static int setToMaxSize(int original){
        if (axolotl_amplifier$shouldBypass) {
            axolotl_amplifier$shouldBypass = false;
            return Math.max(original, 42);
        }
        return original;
    }

    @Override
    public LivingEntity axolotl_amplifier$getTargetEntity() {
        return this.targetEntity;
    }

    @Override
    public void axolotl_amplifier$setTargetEntity(LivingEntity living) {
        if (living == null || !living.isAlive()) return;
        this.targetEntity = living;
        UUID uuid = living.getUuid();
        if (uuid != null) this.targetUuid = uuid;
    }
}
