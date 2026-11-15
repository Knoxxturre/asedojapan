package org.dawnoftime.asedojapan.entity.guard.ai;

import java.util.EnumSet;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.dawnoftime.asedojapan.entity.guard.EdoGuard;

/**
 * Crossbow attack for guards (vanilla's version only works for monsters).
 *
 * Adapted from RangedCrossbowAttackPassiveGoal of Guard Villagers by seymourimadeit (TallestEgg), MIT License (see licenses/guardvillagers-LICENSE.txt).
 * Guards move to a new spot instead of shooting when a friendly stands in the line of fire.
 */
public class GuardCrossbowAttackGoal extends Goal {

    private static final UniformInt PATHFINDING_DELAY_RANGE = TimeUtil.rangeOfSeconds(1, 2);
    /** Dot product below which a friendly counts as standing in front of the guard (about 25 degrees). */
    private static final double FRIENDLY_FIRE_DOT_THRESHOLD = -0.9D;

    private final EdoGuard guard;
    private final double speedModifier;
    private final float attackRadiusSqr;
    private CrossbowState crossbowState = CrossbowState.UNCHARGED;
    private int seeTime;
    private int attackDelay;
    private int updatePathDelay;

    public GuardCrossbowAttackGoal(EdoGuard guard, double speedModifier, float attackRadius) {
        this.guard = guard;
        this.speedModifier = speedModifier;
        this.attackRadiusSqr = attackRadius * attackRadius;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return this.isValidTarget() && this.isHoldingCrossbow();
    }

    @Override
    public boolean canContinueToUse() {
        return this.isValidTarget() && (this.canUse() || !this.guard.getNavigation().isDone()) && this.isHoldingCrossbow();
    }

    private boolean isHoldingCrossbow() {
        return this.guard.getMainHandItem().getItem() instanceof CrossbowItem;
    }

    private boolean isValidTarget() {
        return this.guard.getTarget() != null && this.guard.getTarget().isAlive();
    }

    @Override
    public void start() {
        this.guard.setAggressive(true);
    }

    @Override
    public void stop() {
        super.stop();
        this.guard.setAggressive(false);
        this.guard.setTarget(null);
        this.seeTime = 0;
        if (this.guard.isUsingItem()) {
            this.guard.stopUsingItem();
            this.guard.setChargingCrossbow(false);
        }
        this.guard.setGuardCrouching(false);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = this.guard.getTarget();
        if (target == null) {
            return;
        }

        boolean canSee = this.guard.getSensing().hasLineOfSight(target);
        boolean sawRecently = this.seeTime > 0;
        if (canSee != sawRecently) {
            this.seeTime = 0;
        }
        if (canSee) {
            this.seeTime++;
        } else {
            this.seeTime--;
        }

        double distanceSqr = this.guard.distanceToSqr(target);
        if (target.distanceTo(this.guard) <= 4.0F) {
            // Back off from targets that get too close.
            this.guard.getMoveControl().strafe(this.guard.isUsingItem() ? -0.5F : -3.0F, 0.0F);
            this.guard.lookAt(target, 30.0F, 30.0F);
        }

        if (this.guard.getRandom().nextInt(50) == 0) {
            this.guard.setGuardCrouching(!this.guard.isCrouching());
        }

        boolean shouldMove = (distanceSqr > (double) this.attackRadiusSqr || this.seeTime < 5) && this.attackDelay == 0;
        if (shouldMove) {
            this.updatePathDelay--;
            if (this.updatePathDelay <= 0) {
                this.guard.getNavigation().moveTo(target, this.canRun() ? this.speedModifier : this.speedModifier * 0.5D);
                this.updatePathDelay = PATHFINDING_DELAY_RANGE.sample(this.guard.getRandom());
            }
        } else {
            this.updatePathDelay = 0;
            this.guard.getNavigation().stop();
        }

        this.guard.lookAt(target, 30.0F, 30.0F);
        this.guard.getLookControl().setLookAt(target, 30.0F, 30.0F);

        switch (this.crossbowState) {
            case FIND_NEW_POSITION -> {
                this.guard.stopUsingItem();
                this.guard.setChargingCrossbow(false);
                Vec3 pos = DefaultRandomPos.getPos(this.guard, 16, 7);
                if (pos != null) {
                    this.guard.getNavigation().moveTo(pos.x, pos.y, pos.z, this.guard.isCrouching() ? 0.5D : 1.0D);
                }
                this.crossbowState = CrossbowState.UNCHARGED;
            }
            case UNCHARGED -> {
                if (!shouldMove) {
                    this.guard.startUsingItem(this.crossbowHand());
                    this.crossbowState = CrossbowState.CHARGING;
                    this.guard.setChargingCrossbow(true);
                }
            }
            case CHARGING -> {
                if (!this.guard.isUsingItem()) {
                    this.crossbowState = CrossbowState.UNCHARGED;
                }
                ItemStack crossbow = this.guard.getUseItem();
                if (this.guard.getTicksUsingItem() >= CrossbowItem.getChargeDuration(crossbow, this.guard) || CrossbowItem.isCharged(crossbow)) {
                    this.guard.releaseUsingItem();
                    this.crossbowState = CrossbowState.CHARGED;
                    this.attackDelay = 10 + this.guard.getRandom().nextInt(5);
                    this.guard.setChargingCrossbow(false);
                }
            }
            case CHARGED -> {
                this.attackDelay--;
                if (this.attackDelay == 0) {
                    this.crossbowState = CrossbowState.READY_TO_ATTACK;
                }
            }
            case READY_TO_ATTACK -> {
                if (canSee) {
                    if (friendlyInLineOfSight(this.guard)) {
                        this.crossbowState = CrossbowState.FIND_NEW_POSITION;
                    } else {
                        // Shooting empties the crossbow's charged projectiles, which un-charges it.
                        this.guard.performRangedAttack(target, 1.0F);
                        this.crossbowState = CrossbowState.UNCHARGED;
                    }
                }
            }
        }
    }

    private InteractionHand crossbowHand() {
        return this.guard.getMainHandItem().getItem() instanceof CrossbowItem ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
    }

    private boolean canRun() {
        return this.crossbowState == CrossbowState.UNCHARGED;
    }

    /** True when a villager, golem, guard or player (other than the target) stands between the guard and its aim. */
    public static boolean friendlyInLineOfSight(EdoGuard guard) {
        Vec3 look = guard.getViewVector(1.0F);
        AABB area = guard.getBoundingBox().expandTowards(look.scale(6.0D)).inflate(1.0D);
        for (Entity entity : guard.level().getEntities(guard, area)) {
            if (entity == guard.getTarget() || !isFriendly(entity)) {
                continue;
            }
            Vec3 toGuard = entity.position().vectorTo(guard.position()).normalize();
            if (toGuard.dot(guard.getLookAngle()) < FRIENDLY_FIRE_DOT_THRESHOLD && guard.hasLineOfSight(entity)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isFriendly(Entity entity) {
        if (entity instanceof Player player) {
            return !player.isSpectator();
        }
        return entity instanceof AbstractVillager || entity instanceof IronGolem || entity instanceof EdoGuard;
    }

    private enum CrossbowState {
        UNCHARGED,
        CHARGING,
        CHARGED,
        READY_TO_ATTACK,
        FIND_NEW_POSITION
    }
}
