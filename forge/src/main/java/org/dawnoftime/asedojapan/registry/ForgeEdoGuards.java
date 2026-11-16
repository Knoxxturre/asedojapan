package org.dawnoftime.asedojapan.registry;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.dawnoftime.asedojapan.Constants;
import org.dawnoftime.asedojapan.entity.guard.EdoGuard;
import org.dawnoftime.asedojapan.entity.guard.EdoGuardTypes;
import org.dawnoftime.asedojapan.entity.guard.GuardKit;
import org.dawnoftime.asedojapan.item.EdoSpawnEggItem;

/** Forge registration of the Edo guard entity types, their attributes and spawn eggs. */
public final class ForgeEdoGuards {

    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Constants.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);

    private static final Map<GuardKit, RegistryObject<EntityType<EdoGuard>>> TYPES = new EnumMap<>(GuardKit.class);
    private static final List<RegistryObject<Item>> SPAWN_EGGS = new ArrayList<>();

    static {
        for (GuardKit kit : GuardKit.values()) {
            RegistryObject<EntityType<EdoGuard>> type = ENTITY_TYPES.register(kit.getId(), () -> EdoGuardTypes.create(kit));
            TYPES.put(kit, type);
            SPAWN_EGGS.add(ITEMS.register(kit.getSpawnEggId(), () -> new EdoSpawnEggItem(type, GuardKit.EGG_BACKGROUND_COLOR, GuardKit.EGG_SPOTS_COLOR, new Item.Properties())));
        }
    }

    private ForgeEdoGuards() {
    }

    public static void register(IEventBus modBus) {
        ENTITY_TYPES.register(modBus);
        ITEMS.register(modBus);
        modBus.addListener(ForgeEdoGuards::onAttributeCreation);
        modBus.addListener(ForgeEdoGuards::onBuildCreativeTabs);
    }

    public static EntityType<EdoGuard> getType(GuardKit kit) {
        return TYPES.get(kit).get();
    }

    private static void onAttributeCreation(EntityAttributeCreationEvent event) {
        for (RegistryObject<EntityType<EdoGuard>> type : TYPES.values()) {
            event.put(type.get(), EdoGuard.createAttributes().build());
        }
    }

    private static void onBuildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            SPAWN_EGGS.forEach(egg -> event.accept(egg.get()));
        }
    }
}
