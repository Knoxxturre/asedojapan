package org.dawnoftime.asedojapan.entity.hostile;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.loot.LootTable;

/** Wither skeleton that always wears its {@link HostileKit}. With a bow it shoots flaming arrows like any wither skeleton. */
public class EdoWitherSkeleton extends WitherSkeleton {

    private final HostileKit kit;

    public EdoWitherSkeleton(EntityType<? extends EdoWitherSkeleton> type, Level level, HostileKit kit) {
        super(type, level);
        this.kit = kit;
        this.setPersistenceRequired();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        SpawnGroupData spawnData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, reason, spawnData);
        this.kit.finishSpawn(this, level, difficulty, reason);
        return data;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        this.kit.equip(this);
    }

    /** Drops the same loot as a vanilla wither skeleton, skull chance included. */
    @Override
    protected ResourceKey<LootTable> getDefaultLootTable() {
        return EntityType.WITHER_SKELETON.getDefaultLootTable();
    }
}
