package org.dawnoftime.asedojapan.item;

import java.util.function.Supplier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import org.dawnoftime.asedojapan.entity.guard.GuardKit;

/** Guard spawn egg whose helmet and weapon layers keep their own colors instead of the egg tint. */
public class EdoGuardSpawnEggItem extends DeferredSpawnEggItem {

    public EdoGuardSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, Properties properties) {
        super(type, GuardKit.EGG_BACKGROUND_COLOR, GuardKit.EGG_SPOTS_COLOR, properties);
    }

    @Override
    public int getColor(int tintIndex) {
        return GuardKit.getEggColor(tintIndex, super.getColor(tintIndex));
    }
}
