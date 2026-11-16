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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import org.dawnoftime.asedojapan.Constants;
import org.dawnoftime.asedojapan.entity.hostile.EdoHostileTypes;
import org.dawnoftime.asedojapan.entity.hostile.HostileKit;
import org.dawnoftime.asedojapan.item.EdoSpawnEggItem;

/** Fabric registration of the hostile Edo mob entity types, their attributes and spawn eggs. */
public final class FabricEdoHostiles {

    private static final Map<HostileKit, EntityType<? extends Monster>> TYPES = new EnumMap<>(HostileKit.class);

    private FabricEdoHostiles() {
    }

    public static void register() {
        List<Item> spawnEggs = new ArrayList<>();
        for (HostileKit kit : HostileKit.values()) {
            EntityType<? extends Monster> type = Registry.register(BuiltInRegistries.ENTITY_TYPE,
                    ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, kit.getId()), EdoHostileTypes.create(kit));
            FabricDefaultAttributeRegistry.register(type, kit.getBase().createAttributes());
            TYPES.put(kit, type);
            spawnEggs.add(Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, kit.getSpawnEggId()),
                    new EdoSpawnEggItem(type, kit.getEggBackgroundColor(), kit.getEggSpotsColor(), new Item.Properties())));
        }
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(entries -> spawnEggs.forEach(entries::accept));
    }

    public static EntityType<? extends Monster> getType(HostileKit kit) {
        return TYPES.get(kit);
    }
}
