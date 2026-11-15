package org.dawnoftime.asedojapan.entity.guard.ai;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.dawnoftime.asedojapan.entity.guard.EdoGuard;

/**
 * Melee attack with a longer reach that lowers the shield before striking.
 *
 * Adapted from GuardMeleeGoal of Guard Villagers by seymourimadeit (TallestEgg), MIT License (see licenses/guardvillagers-LICENSE.txt).
 */
public class GuardMeleeGoal extends MeleeAttackGoal {

    private final EdoGuard guard;

    public GuardMeleeGoal(EdoGuard guard, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(guard, speedModifier, followingTargetEvenIfNotSeen);
        this.guard = guard;
    }

    private boolean isHoldingRangedWeapon() {
        return this.guard.getMainHandItem().getItem() instanceof ProjectileWeaponItem;
    }

    @Override
    public boolean canUse() {
        return !this.isHoldingRangedWeapon() && this.guard.getTarget() != null && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return !this.isHoldingRangedWeapon() && this.guard.getTarget() != null && super.canContinueToUse();
    }

    @Override
    public void tick() {
        LivingEntity target = this.guard.getTarget();
        if (target == null) {
            return;
        }
        float distance = target.distanceTo(this.guard);
        if (distance <= 3.0F) {
            this.guard.getMoveControl().strafe(-2.0F, 0.0F);
            this.guard.lookAt(target, 30.0F, 30.0F);
        }
        if (distance <= 2.0F) {
            this.guard.getNavigation().stop();
        }
        super.tick();
    }

    @Override
    protected double getAttackReachSqr(LivingEntity target) {
        return super.getAttackReachSqr(target) * 2.55D;
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target, double distanceSqr) {
        if (distanceSqr <= this.getAttackReachSqr(target) && this.getTicksUntilNextAttack() <= 0) {
            this.resetAttackCooldown();
            this.guard.stopUsingItem();
            if (this.guard.shieldCoolDown == 0) {
                this.guard.shieldCoolDown = 8;
            }
            this.guard.swing(InteractionHand.MAIN_HAND);
            this.guard.doHurtTarget(target);
        }
    }
}
