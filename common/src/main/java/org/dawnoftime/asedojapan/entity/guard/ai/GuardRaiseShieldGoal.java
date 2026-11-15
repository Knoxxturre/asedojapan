package org.dawnoftime.asedojapan.entity.guard.ai;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.UseAnim;
import org.dawnoftime.asedojapan.entity.guard.EdoGuard;

/**
 * Raises the off-hand shield when a threat is close or shooting at the guard.
 *
 * Adapted from RaiseShieldGoal of Guard Villagers by seymourimadeit (TallestEgg), MIT License (see licenses/guardvillagers-LICENSE.txt).
 */
public class GuardRaiseShieldGoal extends Goal {

    private final EdoGuard guard;

    public GuardRaiseShieldGoal(EdoGuard guard) {
        this.guard = guard;
    }

    private boolean hasShield() {
        return this.guard.getOffhandItem().getUseAnimation() == UseAnim.BLOCK;
    }

    @Override
    public boolean canUse() {
        return !CrossbowItem.isCharged(this.guard.getMainHandItem())
                && this.hasShield()
                && this.guard.shieldCoolDown == 0
                && this.shouldRaiseShield();
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void start() {
        if (this.hasShield()) {
            this.guard.startUsingItem(InteractionHand.OFF_HAND);
        }
    }

    @Override
    public void stop() {
        this.guard.stopUsingItem();
    }

    private boolean shouldRaiseShield() {
        LivingEntity target = this.guard.getTarget();
        if (target == null) {
            return false;
        }
        boolean ranged = this.guard.getMainHandItem().getItem() instanceof ProjectileWeaponItem;
        return this.guard.distanceTo(target) <= 4.0F
                || target instanceof Creeper
                || target instanceof RangedAttackMob && target.distanceTo(this.guard) >= 5.0F && !ranged
                || target instanceof Ravager;
    }
}
