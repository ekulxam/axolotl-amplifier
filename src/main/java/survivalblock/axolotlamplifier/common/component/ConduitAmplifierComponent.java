package survivalblock.axolotlamplifier.common.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import survivalblock.axolotlamplifier.access.ActiveConduitAccess;
import survivalblock.axolotlamplifier.common.init.AmplifierEntityComponents;

public class ConduitAmplifierComponent implements AutoSyncedComponent, CommonTickingComponent {

    private final AxolotlEntity axolotl;
    boolean hasConduit;
    boolean isConduitActive = false;
    private long nextAmbientSoundTime = 0;
    private static final float SOUND_VOLUME = 0.5f;
    private final ConduitBlockEntity renderConduit = new ConduitBlockEntity(BlockPos.ORIGIN, Blocks.CONDUIT.getDefaultState());
    private ItemStack conduitStack = ItemStack.EMPTY;

    public ConduitAmplifierComponent(AxolotlEntity axolotl) {
        this.axolotl = axolotl;
    }
    @Override
    public void readFromNbt(NbtCompound tag) {
        this.hasConduit = tag.getBoolean("HasConduit");
        this.isConduitActive = tag.getBoolean("IsActivated");
        this.nextAmbientSoundTime = tag.getLong("NextAmbientSoundTime");
        if (tag.contains("ConduitStack", NbtElement.COMPOUND_TYPE)) {
            this.conduitStack = ItemStack.fromNbt(tag.getCompound("ConduitStack"));
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean("HasConduit", this.hasConduit);
        tag.putBoolean("IsActivated", this.isConduitActive);
        tag.putLong("NextAmbientSoundTime", this.nextAmbientSoundTime);
        tag.put("ConduitStack", this.conduitStack.writeNbt(new NbtCompound()));
    }

    public void setConduitStack(ItemStack conduitStack) {
        this.conduitStack = conduitStack;
        sync();
    }

    public ItemStack getConduitStack() {
        return this.conduitStack.copy();
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
            BlockPos blockPos = this.axolotl.getBlockPos();
            long l = world.getTime();
            if (l % 40L == 0L) {
                if (this.isConduitActive != this.renderConduit.isActive()) {
                    SoundEvent soundEvent = this.isConduitActive ? SoundEvents.BLOCK_CONDUIT_ACTIVATE : SoundEvents.BLOCK_CONDUIT_DEACTIVATE;
                    world.playSound(null, blockPos, soundEvent, SoundCategory.NEUTRAL, SOUND_VOLUME, 1.0f);
                    this.isConduitActive = this.renderConduit.isActive();
                    sync();
                }
                if (this.isConduitActive) {
                    ((ActiveConduitAccess) renderConduit).axolotl_amplifier$invokeGivePlayersEffects(world, blockPos);
                    if (this.axolotl.getTarget() != null && this.axolotl.getTarget().isAlive()) {
                        ((ActiveConduitAccess) renderConduit).axolotl_amplifier$setTargetEntity(this.axolotl.getTarget());
                    }
                    ((ActiveConduitAccess) renderConduit).axolotl_amplifier$invokeAttackHostileEntity(world, blockPos, this.renderConduit);
                    LivingEntity target = ((ActiveConduitAccess) renderConduit).axolotl_amplifier$getTargetEntity();
                    if (this.axolotl.getWorld() instanceof ServerWorld serverWorld && target != null && target.isAlive() && target.isTouchingWaterOrRain()) {
                        Vec3d targetPos = target.getPos().add(0, target.getHeight() / 2, 0);
                        Vec3d direction = targetPos.subtract(this.axolotl.getPos());
                        Vec3d directionNormalized = direction.normalize();
                        for (float step = 0; step * step < this.axolotl.getPos().squaredDistanceTo(targetPos); step += 0.2f) {
                            Vec3d vec3d = this.axolotl.getPos().add(directionNormalized.multiply(step));
                            serverWorld.spawnParticles(ParticleTypes.NAUTILUS, vec3d.x, vec3d.y, vec3d.z,1, 0.1, 0.1, 0.1, 0.1);
                        }
                    }
                }
            }
            if (this.isConduitActive) {
                if (l % 80L == 0L) {
                    world.playSound(null, blockPos, SoundEvents.BLOCK_CONDUIT_AMBIENT, SoundCategory.NEUTRAL, SOUND_VOLUME, 1.0f);
                }
                if (l > this.nextAmbientSoundTime) {
                    this.nextAmbientSoundTime = l + 60L + (long)world.getRandom().nextInt(40);
                    sync();
                    world.playSound(null, blockPos, SoundEvents.BLOCK_CONDUIT_AMBIENT_SHORT, SoundCategory.NEUTRAL, SOUND_VOLUME, 1.0f);
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

    public void remove() {
        if (this.getConduitStack() != null && this.getConduitStack() != ItemStack.EMPTY) this.axolotl.dropStack(this.getConduitStack());
        this.setHasConduit(false);
        this.axolotl.getWorld().playSound(null, this.axolotl.getBlockPos(), SoundEvents.BLOCK_CONDUIT_DEACTIVATE, SoundCategory.NEUTRAL, SOUND_VOLUME, 1.0f);
        sync();
    }
}
