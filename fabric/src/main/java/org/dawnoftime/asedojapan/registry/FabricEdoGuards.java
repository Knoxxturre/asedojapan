package org.dawnoftime.asedojapan.registry;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import org.dawnoftime.asedojapan.Constants;
import org.dawnoftime.asedojapan.entity.guard.EdoGuard;
import org.dawnoftime.asedojapan.entity.guard.EdoGuardTypes;
import org.dawnoftime.asedojapan.entity.guard.GuardKit;
import org.dawnoftime.asedojapan.item.EdoGuardSpawnEggItem;

/** Fabric registration of the Edo guard entity types, their attributes and spawn eggs. */
public final class FabricEdoGuards {

    private static final Map<GuardKit, EntityType<EdoGuard>> TYPES = new EnumMap<>(GuardKit.class);

    private FabricEdoGuards() {
    }

    public static void register() {
        List<Item> spawnEggs = new ArrayList<>();
        for (GuardKit kit : GuardKit.values()) {
            EntityType<EdoGuard> type = Registry.register(BuiltInRegistries.ENTITY_TYPE,
                    new ResourceLocation(Constants.MOD_ID, kit.getId()), EdoGuardTypes.create(kit));
            FabricDefaultAttributeRegistry.register(type, EdoGuard.createAttributes());
            TYPES.put(kit, type);
            spawnEggs.add(Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Constants.MOD_ID, kit.getSpawnEggId()),
                    new EdoGuardSpawnEggItem(type, new Item.Properties())));
        }
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(entries -> spawnEggs.forEach(entries::accept));
    }

    public static EntityType<EdoGuard> getType(GuardKit kit) {
        return TYPES.get(kit);
    }
}
