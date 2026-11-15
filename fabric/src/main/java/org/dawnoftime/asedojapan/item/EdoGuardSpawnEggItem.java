package org.dawnoftime.asedojapan.item;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.SpawnEggItem;
import org.dawnoftime.asedojapan.entity.guard.GuardKit;

/** Guard spawn egg whose helmet layer keeps its own colors instead of the egg tint. */
public class EdoGuardSpawnEggItem extends SpawnEggItem {

    public EdoGuardSpawnEggItem(EntityType<? extends Mob> type, Properties properties) {
        super(type, GuardKit.EGG_BACKGROUND_COLOR, GuardKit.EGG_SPOTS_COLOR, properties);
    }

    @Override
    public int getColor(int tintIndex) {
        return GuardKit.getEggColor(tintIndex, super.getColor(tintIndex));
    }
}
