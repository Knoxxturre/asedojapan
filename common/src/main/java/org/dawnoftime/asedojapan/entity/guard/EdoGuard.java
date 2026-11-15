package org.dawnoftime.asedojapan.entity.guard;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import org.dawnoftime.asedojapan.Constants;
import org.dawnoftime.asedojapan.entity.guard.ai.GuardCrossbowAttackGoal;
import org.dawnoftime.asedojapan.entity.guard.ai.GuardKickGoal;
import org.dawnoftime.asedojapan.entity.guard.ai.GuardMeleeGoal;
import org.dawnoftime.asedojapan.entity.guard.ai.GuardRaiseShieldGoal;

/**
 * Edo Japan guard.
 *
 * Adapted from the Guard entity of Guard Villagers by seymourimadeit (TallestEgg)
 * (https://github.com/seymourimadeit/guardvillagers), MIT License (see licenses/guardvillagers-LICENSE.txt).
 * Trimmed down: no inventory screen, orders, following, gossip, reputation or config.
 * Guards keep a post (their spawn position) and defend the area around it.
 */
public class EdoGuard extends PathfinderMob implements CrossbowAttackMob {

    private static final EntityDataAccessor<String> GUARD_VARIANT = SynchedEntityData.defineId(EdoGuard.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> DATA_CHARGING_STATE = SynchedEntityData.defineId(EdoGuard.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> KICKING = SynchedEntityData.defineId(EdoGuard.class, EntityDataSerializers.BOOLEAN);

    private static final ResourceLocation SHIELD_SPEED_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "guard_shield_speed_penalty");
    private static final AttributeModifier SHIELD_SPEED_PENALTY = new AttributeModifier(SHIELD_SPEED_MODIFIER_ID, -0.25D, AttributeModifier.Operation.ADD_VALUE);

    /** Radius (in blocks) of the area a guard defends around its post. */
    public static final int POST_RADIUS = 16;
    /** A saved post further than this from the guard is treated as stale (e.g. copied from a structure template). */
    private static final int MAX_POST_DRIFT = 32;
    /** Extra horizontal melee reach on top of vanilla's, matching the 1.20.1 guards (about 2.3 blocks centre to centre). */
    private static final double EXTRA_MELEE_REACH = 0.85D;
    private static final String DEFAULT_VARIANT = "plains";

    private final GuardKit kit;
    public int kickTicks;
    public int kickCoolDown;
    public int shieldCoolDown;

    public EdoGuard(EntityType<? extends EdoGuard> type, Level level, GuardKit kit) {
        super(type, level);
        this.kit = kit;
        this.setPersistenceRequired();
        ((GroundPathNavigation) this.getNavigation()).setCanOpenDoors(true);
        this.setPathfindingMalus(PathType.POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(PathType.DANGER_POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(PathType.DAMAGE_OTHER, -1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 20.0D);
    }

    public GuardKit getKit() {
        return this.kit;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(GUARD_VARIANT, DEFAULT_VARIANT);
        builder.define(DATA_CHARGING_STATE, false);
        builder.define(KICKING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new GuardKickGoal(this));
        this.goalSelector.addGoal(0, new GuardRaiseShieldGoal(this));
        this.goalSelector.addGoal(2, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(3, new GuardCrossbowAttackGoal(this, 1.0D, 8.0F));
        this.goalSelector.addGoal(3, new GuardMeleeGoal(this, 0.8D, true));
        this.goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 0.5D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.5D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, AbstractVillager.class, 8.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(2, new HurtByTargetGoal(this, EdoGuard.class, IronGolem.class).setAlertOthers());
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Mob.class, 5, true, true, EdoGuard::isHostileTarget));
    }

    private static boolean isHostileTarget(LivingEntity entity) {
        return entity instanceof Enemy && !(entity instanceof Creeper) && !(entity instanceof EnderMan);
    }

    // ---------------------------------------------------------------- Spawning

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        SpawnGroupData spawnData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, reason, spawnData);
        this.setPersistenceRequired();
        this.setGuardVariant(variantFromBiome(level, this.blockPosition()));
        this.populateDefaultEquipmentSlots(level.getRandom(), difficulty);
        this.setPost(this.blockPosition());
        return data;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        this.kit.equip(this);
    }

    public static String variantFromBiome(LevelAccessor level, BlockPos pos) {
        VillagerType type = VillagerType.byBiome(level.getBiome(pos));
        return BuiltInRegistries.VILLAGER_TYPE.getKey(type).getPath();
    }

    public String getGuardVariant() {
        String variant = this.entityData.get(GUARD_VARIANT);
        return variant.isEmpty() ? DEFAULT_VARIANT : variant;
    }

    public void setGuardVariant(String variant) {
        this.entityData.set(GUARD_VARIANT, variant);
    }

    private void setPost(BlockPos pos) {
        this.restrictTo(pos, POST_RADIUS);
    }

    // ---------------------------------------------------------------- Save data

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Variant", this.getGuardVariant());
        tag.putInt("KickTicks", this.kickTicks);
        tag.putInt("KickCooldown", this.kickCoolDown);
        tag.putInt("ShieldCooldown", this.shieldCoolDown);
        if (this.hasRestriction()) {
            tag.put("GuardPost", NbtUtils.writeBlockPos(this.getRestrictCenter()));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Variant", Tag.TAG_STRING)) {
            this.setGuardVariant(tag.getString("Variant"));
        }
        this.kickTicks = tag.getInt("KickTicks");
        this.kickCoolDown = tag.getInt("KickCooldown");
        this.shieldCoolDown = tag.getInt("ShieldCooldown");
        NbtUtils.readBlockPos(tag, "GuardPost").ifPresent(savedPost -> {
            // Position is already loaded at this point; a post far away comes from another world (structure template).
            BlockPos post = savedPost.distSqr(this.blockPosition()) > (double) (MAX_POST_DRIFT * MAX_POST_DRIFT)
                    ? this.blockPosition()
                    : savedPost;
            this.setPost(post);
        });
    }

    // ---------------------------------------------------------------- Ticking

    @Override
    public void aiStep() {
        if (this.kickTicks > 0) {
            this.kickTicks--;
        }
        if (this.kickCoolDown > 0) {
            this.kickCoolDown--;
        }
        if (this.shieldCoolDown > 0) {
            this.shieldCoolDown--;
        }
        if (!this.level().isClientSide) {
            if (!this.hasRestriction()) {
                this.setPost(this.blockPosition());
            }
            if (this.getHealth() < this.getMaxHealth() && this.tickCount % 200 == 0) {
                this.heal(1.0F);
            }
        }
        this.updateSwingTime();
        super.aiStep();
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            this.kickTicks = 10;
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    protected void doPush(Entity entity) {
        // Guards turn on mobs that bump into them while hunting villagers or other guards.
        if (entity instanceof PathfinderMob mob) {
            LivingEntity target = mob.getTarget();
            if (target instanceof Villager || target instanceof IronGolem || target instanceof EdoGuard) {
                this.setTarget(mob);
            }
        }
        super.doPush(entity);
    }

    // ---------------------------------------------------------------- Combat

    @Override
    public AABB getAttackBoundingBox() {
        return super.getAttackBoundingBox().inflate(EXTRA_MELEE_REACH, 0.0D, EXTRA_MELEE_REACH);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (this.isKicking() && target instanceof LivingEntity living) {
            living.knockback(1.0D, Mth.sin(this.getYRot() * Mth.DEG_TO_RAD), -Mth.cos(this.getYRot() * Mth.DEG_TO_RAD));
            this.kickTicks = 10;
            this.level().broadcastEntityEvent(this, (byte) 4);
            this.lookAt(target, 90.0F, 90.0F);
        }
        return super.doHurtTarget(target);
    }

    @Override
    protected void blockedByShield(LivingEntity defender) {
        if (this.isKicking()) {
            this.setKicking(false);
        }
        super.blockedByShield(defender);
    }

    @Override
    protected void blockUsingShield(LivingEntity attacker) {
        super.blockUsingShield(attacker);
        this.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 1.0F);
        if (attacker.canDisableShield()) {
            this.disableShield(true);
        }
    }

    // Public because Fabric API widens this method; still a valid override of the protected vanilla method on NeoForge.
    @Override
    public void hurtCurrentlyUsedShield(float damage) {
        if (this.useItem.getUseAnimation() == UseAnim.BLOCK && damage >= 3.0F) {
            int amount = 1 + Mth.floor(damage);
            InteractionHand hand = this.getUsedItemHand();
            this.useItem.hurtAndBreak(amount, this, LivingEntity.getSlotForHand(hand));
            if (this.useItem.isEmpty()) {
                this.setItemInHand(hand, ItemStack.EMPTY);
                this.useItem = ItemStack.EMPTY;
                this.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F);
            }
        }
    }

    public void disableShield(boolean increase) {
        float chance = increase ? 1.0F : 0.25F;
        if (this.random.nextFloat() < chance) {
            this.shieldCoolDown = 100;
            this.stopUsingItem();
            this.level().broadcastEntityEvent(this, (byte) 30);
        }
    }

    @Override
    public void startUsingItem(InteractionHand hand) {
        super.startUsingItem(hand);
        if (this.getItemInHand(hand).getUseAnimation() == UseAnim.BLOCK) {
            AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed != null) {
                speed.removeModifier(SHIELD_SPEED_MODIFIER_ID);
                speed.addTransientModifier(SHIELD_SPEED_PENALTY);
            }
        }
    }

    @Override
    public void stopUsingItem() {
        super.stopUsingItem();
        AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null && speed.hasModifier(SHIELD_SPEED_MODIFIER_ID)) {
            speed.removeModifier(SHIELD_SPEED_MODIFIER_ID);
        }
    }

    public boolean isKicking() {
        return this.entityData.get(KICKING);
    }

    public void setKicking(boolean kicking) {
        this.entityData.set(KICKING, kicking);
    }

    public int getKickTicks() {
        return this.kickTicks;
    }

    /** Public wrapper because {@link Entity#setPose(Pose)} is protected. */
    public void setGuardCrouching(boolean crouching) {
        this.setPose(crouching ? Pose.CROUCHING : Pose.STANDING);
    }

    // ---------------------------------------------------------------- Crossbow

    public boolean isCharging() {
        return this.entityData.get(DATA_CHARGING_STATE);
    }

    @Override
    public void setChargingCrossbow(boolean charging) {
        this.entityData.set(DATA_CHARGING_STATE, charging);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        this.shieldCoolDown = 8;
        if (this.getMainHandItem().getItem() instanceof CrossbowItem) {
            this.performCrossbowAttack(this, 1.6F);
        }
    }

    @Override
    public void performCrossbowAttack(LivingEntity user, float velocity) {
        ItemStack stack = user.getMainHandItem();
        if (stack.getItem() instanceof CrossbowItem crossbow) {
            crossbow.performShooting(user.level(), user, InteractionHand.MAIN_HAND, stack, velocity, 0.0F, this.getTarget());
        }
        this.onCrossbowAttackPerformed();
    }

    @Override
    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }

    @Override
    public ItemStack getProjectile(ItemStack weapon) {
        if (weapon.getItem() instanceof ProjectileWeaponItem projectileWeapon) {
            ItemStack held = ProjectileWeaponItem.getHeldProjectile(this, projectileWeapon.getSupportedHeldProjectiles());
            return held.isEmpty() ? new ItemStack(Items.ARROW) : held;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFireProjectileWeapon(ProjectileWeaponItem item) {
        return item instanceof CrossbowItem || super.canFireProjectileWeapon(item);
    }

    // ---------------------------------------------------------------- Misc

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }
}
