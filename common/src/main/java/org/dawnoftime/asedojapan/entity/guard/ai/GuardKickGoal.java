package org.dawnoftime.asedojapan.entity.guard.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import org.dawnoftime.asedojapan.entity.guard.EdoGuard;

/**
 * Crossbow guards kick targets that get too close to push them back.
 *
 * Adapted from KickGoal of Guard Villagers by seymourimadeit (TallestEgg), MIT License (see licenses/guardvillagers-LICENSE.txt).
 */
public class GuardKickGoal extends Goal {

    private final EdoGuard guard;

    public GuardKickGoal(EdoGuard guard) {
        this.guard = guard;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.guard.getTarget();
        return target != null
                && target.distanceTo(this.guard) <= 2.5F
                && this.guard.getMainHandItem().getItem().useOnRelease(this.guard.getMainHandItem())
                && !this.guard.isBlocking()
                && this.guard.kickCoolDown == 0;
    }

    @Override
    public void start() {
        this.guard.setKicking(true);
        if (this.guard.kickTicks <= 0) {
            this.guard.kickTicks = 10;
        }
        LivingEntity target = this.guard.getTarget();
        if (target != null) {
            this.guard.doHurtTarget(target);
        }
    }

    @Override
    public void stop() {
        this.guard.setKicking(false);
        this.guard.kickCoolDown = 50;
    }
}
