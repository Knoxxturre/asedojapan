package org.dawnoftime.asedojapan.entity.hostile;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

/** Skeleton that always wears its {@link HostileKit}. Picks bow or melee attacks from its weapon like any skeleton. */
public class EdoSkeleton extends Skeleton {

    private final HostileKit kit;

    public EdoSkeleton(EntityType<? extends EdoSkeleton> type, Level level, HostileKit kit) {
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

    /** Drops the same loot as a vanilla skeleton. */
    @Override
    protected ResourceLocation getDefaultLootTable() {
        return EntityType.SKELETON.getDefaultLootTable();
    }

    /** Never burns in daylight, and the helmet takes no sun damage either. */
    @Override
    protected boolean isSunBurnTick() {
        return false;
    }

    /** Stays a kitted skeleton in powder snow instead of turning into a vanilla stray. */
    @Override
    protected void doFreezeConversion() {
    }
}
