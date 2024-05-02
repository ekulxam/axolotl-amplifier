package survivalblock.axolotlamplifier.common.init;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.minecraft.entity.passive.AxolotlEntity;
import survivalblock.axolotlamplifier.common.AxolotlAmplifier;
import survivalblock.axolotlamplifier.common.component.ConduitAmplifierComponent;

public class AmplifierEntityComponents implements EntityComponentInitializer {

    public static final ComponentKey<ConduitAmplifierComponent> CONDUIT_COMPONENT = ComponentRegistry.getOrCreate(AxolotlAmplifier.id("conduit"), ConduitAmplifierComponent.class);
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(AxolotlEntity.class, CONDUIT_COMPONENT, ConduitAmplifierComponent::new);
    }
}
