package org.dawnoftime.asedojapan.entity.hostile;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.loot.LootTable;

/** Vindicator that always wears its {@link HostileKit}. Keeps the vanilla vindicator AI. */
public class EdoVindicator extends Vindicator {

    private final HostileKit kit;

    public EdoVindicator(EntityType<? extends EdoVindicator> type, Level level, HostileKit kit) {
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

    /** Never a patrol captain, so the ominous banner never replaces the helmet. */
    @Override
    public boolean canBeLeader() {
        return false;
    }

    /** Drops the same loot as a vanilla vindicator. */
    @Override
    protected ResourceKey<LootTable> getDefaultLootTable() {
        return EntityType.VINDICATOR.getDefaultLootTable();
    }
}
