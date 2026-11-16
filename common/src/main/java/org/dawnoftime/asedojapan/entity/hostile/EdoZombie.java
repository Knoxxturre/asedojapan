package org.dawnoftime.asedojapan.entity.hostile;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

/** Zombie that always wears its {@link HostileKit}. Always spawns as an adult. */
public class EdoZombie extends Zombie {

    private final HostileKit kit;

    public EdoZombie(EntityType<? extends EdoZombie> type, Level level, HostileKit kit) {
        super(type, level);
        this.kit = kit;
        this.setPersistenceRequired();
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        // Vanilla rolls for a baby (and chicken jockey) only when no group data is passed in.
        if (spawnData == null) {
            spawnData = new ZombieGroupData(false, false);
        }
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
        this.kit.finishSpawn(this, level, difficulty, reason);
        return data;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        this.kit.equip(this);
    }

    /** Drops the same loot as a vanilla zombie. */
    @Override
    protected ResourceLocation getDefaultLootTable() {
        return EntityType.ZOMBIE.getDefaultLootTable();
    }

    /** Never burns in daylight, and the helmet takes no sun damage either. */
    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    /** Stays a kitted zombie underwater instead of turning into a vanilla drowned. */
    @Override
    protected boolean convertsInWater() {
        return false;
    }
}
