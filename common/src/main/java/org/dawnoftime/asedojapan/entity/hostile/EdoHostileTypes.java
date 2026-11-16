package org.dawnoftime.asedojapan.entity.hostile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.block.Blocks;
import org.dawnoftime.asedojapan.Constants;

/**
 * Loader-agnostic entity type factory for the hostile Edo mobs. Each loader registers the returned types under
 * {@link HostileKit#getId()}. Sizes, eye heights, riding offsets and immunities match the vanilla base mobs
 * (since 1.20.5 the riding offset lives on the entity type, so a skeleton without it floats above its horse).
 */
public final class EdoHostileTypes {

    private EdoHostileTypes() {
    }

    public static EntityType<? extends Monster> create(HostileKit kit) {
        String id = Constants.MOD_ID + ":" + kit.getId();
        return switch (kit.getBase()) {
            case SKELETON -> EntityType.Builder.<EdoSkeleton>of((type, level) -> new EdoSkeleton(type, level, kit), MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .eyeHeight(1.74F)
                    .ridingOffset(-0.7F)
                    .clientTrackingRange(8)
                    .build(id);
            case WITHER_SKELETON -> EntityType.Builder.<EdoWitherSkeleton>of((type, level) -> new EdoWitherSkeleton(type, level, kit), MobCategory.MONSTER)
                    .fireImmune()
                    .immuneTo(Blocks.WITHER_ROSE)
                    .sized(0.7F, 2.4F)
                    .eyeHeight(2.1F)
                    .ridingOffset(-0.875F)
                    .clientTrackingRange(8)
                    .build(id);
            case ZOMBIE -> EntityType.Builder.<EdoZombie>of((type, level) -> new EdoZombie(type, level, kit), MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .eyeHeight(1.74F)
                    .passengerAttachments(2.0125F)
                    .ridingOffset(-0.7F)
                    .clientTrackingRange(8)
                    .build(id);
            case PILLAGER -> EntityType.Builder.<EdoPillager>of((type, level) -> new EdoPillager(type, level, kit), MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .passengerAttachments(2.0F)
                    .ridingOffset(-0.6F)
                    .clientTrackingRange(8)
                    .build(id);
            case VINDICATOR -> EntityType.Builder.<EdoVindicator>of((type, level) -> new EdoVindicator(type, level, kit), MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .passengerAttachments(2.0F)
                    .ridingOffset(-0.6F)
                    .clientTrackingRange(8)
                    .build(id);
        };
    }
}
