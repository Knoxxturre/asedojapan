package org.dawnoftime.asedojapan.item;

import java.util.function.Supplier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

/** Spawn egg whose helmet and weapon marker layers keep their own colors instead of the egg tint. */
public class EdoSpawnEggItem extends DeferredSpawnEggItem {

    public EdoSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int spotsColor, Properties properties) {
        super(type, backgroundColor, spotsColor, properties);
    }

    @Override
    public int getColor(int tintIndex) {
        return EdoSpawnEggs.getColor(tintIndex, super.getColor(tintIndex));
    }
}
