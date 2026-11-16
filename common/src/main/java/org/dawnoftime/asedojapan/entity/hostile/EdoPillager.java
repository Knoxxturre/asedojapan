package org.dawnoftime.asedojapan.entity.hostile;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

/** Pillager that always wears its {@link HostileKit}. Keeps the vanilla pillager AI and crossbow attack. */
public class EdoPillager extends Pillager {

    private final HostileKit kit;

    public EdoPillager(EntityType<? extends EdoPillager> type, Level level, HostileKit kit) {
        super(type, level);
        this.kit = kit;
        this.setPersistenceRequired();
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
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

    /** Drops the same loot as a vanilla pillager. */
    @Override
    protected ResourceLocation getDefaultLootTable() {
        return EntityType.PILLAGER.getDefaultLootTable();
    }
}
