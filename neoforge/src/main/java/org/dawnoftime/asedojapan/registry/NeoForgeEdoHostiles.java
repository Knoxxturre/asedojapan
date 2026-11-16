package org.dawnoftime.asedojapan.registry;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.dawnoftime.asedojapan.Constants;
import org.dawnoftime.asedojapan.entity.hostile.EdoHostileTypes;
import org.dawnoftime.asedojapan.entity.hostile.HostileKit;
import org.dawnoftime.asedojapan.item.EdoSpawnEggItem;

/** NeoForge registration of the hostile Edo mob entity types, their attributes and spawn eggs. */
public final class NeoForgeEdoHostiles {

    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, Constants.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Constants.MOD_ID);

    private static final Map<HostileKit, DeferredHolder<EntityType<?>, EntityType<? extends Monster>>> TYPES = new EnumMap<>(HostileKit.class);
    private static final List<DeferredItem<Item>> SPAWN_EGGS = new ArrayList<>();

    static {
        for (HostileKit kit : HostileKit.values()) {
            DeferredHolder<EntityType<?>, EntityType<? extends Monster>> type = ENTITY_TYPES.register(kit.getId(), () -> EdoHostileTypes.create(kit));
            TYPES.put(kit, type);
            SPAWN_EGGS.add(ITEMS.register(kit.getSpawnEggId(), () -> new EdoSpawnEggItem(type,
                    kit.getEggBackgroundColor(), kit.getEggSpotsColor(), new Item.Properties())));
        }
    }

    private NeoForgeEdoHostiles() {
    }

    public static void register(IEventBus modBus) {
        ENTITY_TYPES.register(modBus);
        ITEMS.register(modBus);
        modBus.addListener(NeoForgeEdoHostiles::onAttributeCreation);
        modBus.addListener(NeoForgeEdoHostiles::onBuildCreativeTabs);
    }

    public static EntityType<? extends Monster> getType(HostileKit kit) {
        return TYPES.get(kit).get();
    }

    private static void onAttributeCreation(EntityAttributeCreationEvent event) {
        TYPES.forEach((kit, type) -> event.put(type.get(), kit.getBase().createAttributes().build()));
    }

    private static void onBuildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            SPAWN_EGGS.forEach(event::accept);
        }
    }
}
