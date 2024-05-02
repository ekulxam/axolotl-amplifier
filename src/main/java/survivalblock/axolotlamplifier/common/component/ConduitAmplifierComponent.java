package survivalblock.axolotlamplifier.common.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import survivalblock.axolotlamplifier.access.ActiveConduitAccess;
import survivalblock.axolotlamplifier.common.init.AmplifierEntityComponents;

public class ConduitAmplifierComponent implements AutoSyncedComponent, CommonTickingComponent {

    private final AxolotlEntity axolotl;
    boolean hasConduit;
    boolean isConduitActive = false;
    private long nextAmbientSoundTime = 0;
    private final ConduitBlockEntity renderConduit = new ConduitBlockEntity(BlockPos.ORIGIN, Blocks.CONDUIT.getDefaultState());

    public ConduitAmplifierComponent(AxolotlEntity axolotl) {
        this.axolotl = axolotl;
    }
    @Override
    public void readFromNbt(NbtCompound tag) {
        this.hasConduit = tag.getBoolean("HasConduit");
        this.isConduitActive = tag.getBoolean("IsActivated");
        this.nextAmbientSoundTime = tag.getLong("NextAmbientSoundTime");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean("HasConduit", this.hasConduit);
        tag.putBoolean("IsActivated", this.isConduitActive);
        tag.putLong("NextAmbientSoundTime", this.nextAmbientSoundTime);
    }

    public void setHasConduit(boolean hasConduit) {
        this.hasConduit = hasConduit;
        sync();
    }

    public boolean getHasConduit(){
        return this.hasConduit;
    }

    public ConduitBlockEntity getRenderConduit() {
        return this.renderConduit;
    }

    @Override
    public void clientTick() {
        if (this.shouldActivate()) {
            ((ActiveConduitAccess) renderConduit).axolotl_amplifier$setTicks(this.axolotl.age);
            ((ActiveConduitAccess) renderConduit).axolotl_amplifier$setTicksActive(this.axolotl.age);
            ((ActiveConduitAccess) renderConduit).axolotl_amplifier$invokeUpdateTargetEntity(this.axolotl.getWorld(), this.axolotl.getBlockPos(), this.renderConduit);
        }
        CommonTickingComponent.super.clientTick();
    }

    @Override
    public void serverTick() {
        if (this.shouldActivate()) {
            World world = this.axolotl.getWorld();
            float soundVolume = 0.5f;
            BlockPos blockPos = this.axolotl.getBlockPos();
            long l = world.getTime();
            if (l % 40L == 0L) {
                if (this.isConduitActive != this.renderConduit.isActive()) {
                    SoundEvent soundEvent = this.isConduitActive ? SoundEvents.BLOCK_CONDUIT_ACTIVATE : SoundEvents.BLOCK_CONDUIT_DEACTIVATE;
                    world.playSound(null, blockPos, soundEvent, SoundCategory.BLOCKS, soundVolume, 1.0f);
                    this.isConduitActive = this.renderConduit.isActive();
                    sync();
                }
                if (this.isConduitActive) {
                    ((ActiveConduitAccess) renderConduit).axolotl_amplifier$invokeGivePlayersEffects(world, blockPos);
                    ((ActiveConduitAccess) renderConduit).axolotl_amplifier$invokeAttackHostileEntity(world, blockPos, this.renderConduit);
                }
            }
            if (this.isConduitActive) {
                if (l % 80L == 0L) {
                    world.playSound(null, blockPos, SoundEvents.BLOCK_CONDUIT_AMBIENT, SoundCategory.NEUTRAL, soundVolume, 1.0f);
                }
                if (l > this.nextAmbientSoundTime) {
                    this.nextAmbientSoundTime = l + 60L + (long)world.getRandom().nextInt(40);
                    sync();
                    world.playSound(null, blockPos, SoundEvents.BLOCK_CONDUIT_AMBIENT_SHORT, SoundCategory.NEUTRAL, soundVolume, 1.0f);
                }
            }
        }
        CommonTickingComponent.super.serverTick();
    }

    @Override
    public void tick() {
        if (this.shouldActivate() && this.axolotl.getWorld().getTime() % 40L == 0L) {
            ((ActiveConduitAccess) renderConduit).axolotl_amplifier$setActive(this.axolotl.isAlive());
            ((ActiveConduitAccess) renderConduit).axolotl_amplifier$invokeSetEyeOpen(this.axolotl.isAlive());
        }
    }

    private boolean shouldActivate(){
        return this.getHasConduit() && this.axolotl.isTouchingWaterOrRain();
    }

    private void sync(){
        AmplifierEntityComponents.CONDUIT_COMPONENT.sync(this.axolotl);
    }
}
