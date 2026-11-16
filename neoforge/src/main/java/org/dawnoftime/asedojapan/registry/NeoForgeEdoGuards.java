package org.dawnoftime.asedojapan.registry;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.dawnoftime.asedojapan.Constants;
import org.dawnoftime.asedojapan.entity.guard.EdoGuard;
import org.dawnoftime.asedojapan.entity.guard.EdoGuardTypes;
import org.dawnoftime.asedojapan.entity.guard.GuardKit;
import org.dawnoftime.asedojapan.item.EdoSpawnEggItem;

/** NeoForge registration of the Edo guard entity types, their attributes and spawn eggs. */
public final class NeoForgeEdoGuards {

    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, Constants.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Constants.MOD_ID);

    private static final Map<GuardKit, DeferredHolder<EntityType<?>, EntityType<EdoGuard>>> TYPES = new EnumMap<>(GuardKit.class);
    private static final List<DeferredItem<Item>> SPAWN_EGGS = new ArrayList<>();

    static {
        for (GuardKit kit : GuardKit.values()) {
            DeferredHolder<EntityType<?>, EntityType<EdoGuard>> type = ENTITY_TYPES.register(kit.getId(), () -> EdoGuardTypes.create(kit));
            TYPES.put(kit, type);
            SPAWN_EGGS.add(ITEMS.register(kit.getSpawnEggId(), () -> new EdoSpawnEggItem(type, GuardKit.EGG_BACKGROUND_COLOR, GuardKit.EGG_SPOTS_COLOR, new Item.Properties())));
        }
    }

    private NeoForgeEdoGuards() {
    }

    public static void register(IEventBus modBus) {
        ENTITY_TYPES.register(modBus);
        ITEMS.register(modBus);
        modBus.addListener(NeoForgeEdoGuards::onAttributeCreation);
        modBus.addListener(NeoForgeEdoGuards::onBuildCreativeTabs);
    }

    public static EntityType<EdoGuard> getType(GuardKit kit) {
        return TYPES.get(kit).get();
    }

    private static void onAttributeCreation(EntityAttributeCreationEvent event) {
        for (DeferredHolder<EntityType<?>, EntityType<EdoGuard>> type : TYPES.values()) {
            event.put(type.get(), EdoGuard.createAttributes().build());
        }
    }

    private static void onBuildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            SPAWN_EGGS.forEach(event::accept);
        }
    }
}
