package org.dawnoftime.asedojapan.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.dawnoftime.asedojapan.Constants;
import org.dawnoftime.asedojapan.client.guard.EdoGuardArmorModel;
import org.dawnoftime.asedojapan.client.guard.EdoGuardModel;
import org.dawnoftime.asedojapan.client.guard.EdoGuardModelLayers;
import org.dawnoftime.asedojapan.client.guard.EdoGuardRenderer;
import org.dawnoftime.asedojapan.entity.guard.GuardKit;
import org.dawnoftime.asedojapan.registry.NeoForgeEdoGuards;

/** Client-side NeoForge registration of the Edo guard models and renderers. */
@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class NeoForgeEdoGuardsClient {

    private NeoForgeEdoGuardsClient() {
    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(EdoGuardModelLayers.GUARD, EdoGuardModel::createBodyLayer);
        event.registerLayerDefinition(EdoGuardModelLayers.GUARD_INNER_ARMOR, EdoGuardArmorModel::createInnerArmorLayer);
        event.registerLayerDefinition(EdoGuardModelLayers.GUARD_OUTER_ARMOR, EdoGuardArmorModel::createOuterArmorLayer);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        for (GuardKit kit : GuardKit.values()) {
            event.registerEntityRenderer(NeoForgeEdoGuards.getType(kit), EdoGuardRenderer::new);
        }
    }
}
