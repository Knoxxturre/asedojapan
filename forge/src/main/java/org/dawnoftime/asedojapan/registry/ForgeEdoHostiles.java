package org.dawnoftime.asedojapan.registry;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.dawnoftime.asedojapan.Constants;
import org.dawnoftime.asedojapan.entity.hostile.EdoHostileTypes;
import org.dawnoftime.asedojapan.entity.hostile.HostileKit;
import org.dawnoftime.asedojapan.item.EdoSpawnEggItem;

/** Forge registration of the hostile Edo mob entity types, their attributes and spawn eggs. */
public final class ForgeEdoHostiles {

    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Constants.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);

    private static final Map<HostileKit, RegistryObject<EntityType<? extends Monster>>> TYPES = new EnumMap<>(HostileKit.class);
    private static final List<RegistryObject<Item>> SPAWN_EGGS = new ArrayList<>();

    static {
        for (HostileKit kit : HostileKit.values()) {
            RegistryObject<EntityType<? extends Monster>> type = ENTITY_TYPES.register(kit.getId(), () -> EdoHostileTypes.create(kit));
            TYPES.put(kit, type);
            SPAWN_EGGS.add(ITEMS.register(kit.getSpawnEggId(), () -> new EdoSpawnEggItem(type,
                    kit.getEggBackgroundColor(), kit.getEggSpotsColor(), new Item.Properties())));
        }
    }

    private ForgeEdoHostiles() {
    }

    public static void register(IEventBus modBus) {
        ENTITY_TYPES.register(modBus);
        ITEMS.register(modBus);
        modBus.addListener(ForgeEdoHostiles::onAttributeCreation);
        modBus.addListener(ForgeEdoHostiles::onBuildCreativeTabs);
    }

    public static EntityType<? extends Monster> getType(HostileKit kit) {
        return TYPES.get(kit).get();
    }

    private static void onAttributeCreation(EntityAttributeCreationEvent event) {
        TYPES.forEach((kit, type) -> event.put(type.get(), kit.getBase().createAttributes().build()));
    }

    private static void onBuildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            SPAWN_EGGS.forEach(egg -> event.accept(egg.get()));
        }
    }
}
