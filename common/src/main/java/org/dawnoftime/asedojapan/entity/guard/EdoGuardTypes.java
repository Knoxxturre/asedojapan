package org.dawnoftime.asedojapan.entity.guard;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.dawnoftime.asedojapan.Constants;

/**
 * Loader-agnostic entity type factory. Each loader registers the returned types under {@link GuardKit#getId()}.
 */
public final class EdoGuardTypes {

    private EdoGuardTypes() {
    }

    public static EntityType<EdoGuard> create(GuardKit kit) {
        return EntityType.Builder.<EdoGuard>of((type, level) -> new EdoGuard(type, level, kit), MobCategory.MISC)
                .sized(0.6F, 1.95F)
                .clientTrackingRange(10)
                .build(Constants.MOD_ID + ":" + kit.getId());
    }
}
