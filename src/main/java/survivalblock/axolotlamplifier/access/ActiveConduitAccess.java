package survivalblock.axolotlamplifier.access;

import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public interface ActiveConduitAccess {

    void axolotl_amplifier$setActive(boolean newValue);

    void axolotl_amplifier$setTicks(int ticks);
    void axolotl_amplifier$setTicksActive(int ticks);
    void axolotl_amplifier$invokeSetEyeOpen(boolean open);
    void axolotl_amplifier$invokeGivePlayersEffects(World world, BlockPos pos);
    void axolotl_amplifier$invokeAttackHostileEntity(World world, BlockPos pos, ConduitBlockEntity conduit);
    void axolotl_amplifier$invokeUpdateTargetEntity(World world, BlockPos pos, ConduitBlockEntity conduit);
    LivingEntity axolotl_amplifier$getTargetEntity();
    void axolotl_amplifier$setTargetEntity(LivingEntity living);
}
