package org.dawnoftime.asedojapan.client.hostile;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.WitherSkeletonRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.world.entity.monster.Monster;
import org.dawnoftime.asedojapan.entity.hostile.HostileKit;

/**
 * Renderers for the hostile Edo mobs: undead reuse the vanilla renderer of their base mob; illagers need
 * {@link EdoIllagerRenderer}, since the vanilla illager model cannot wear armor.
 */
public final class EdoHostileRenderers {

    private EdoHostileRenderers() {
    }

    /**
     * The unchecked cast is safe: {@link org.dawnoftime.asedojapan.entity.hostile.EdoHostileTypes} builds every kit's
     * entity from a subclass of its base mob, which is what the matching renderer expects.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static EntityRendererProvider<Monster> create(HostileKit kit) {
        EntityRendererProvider provider = switch (kit.getBase()) {
            case SKELETON -> SkeletonRenderer::new;
            case WITHER_SKELETON -> WitherSkeletonRenderer::new;
            case ZOMBIE -> ZombieRenderer::new;
            case PILLAGER -> context -> new EdoIllagerRenderer(context, EdoIllagerRenderer.PILLAGER_TEXTURE);
            case VINDICATOR -> context -> new EdoIllagerRenderer(context, EdoIllagerRenderer.VINDICATOR_TEXTURE);
        };
        return provider;
    }
}
