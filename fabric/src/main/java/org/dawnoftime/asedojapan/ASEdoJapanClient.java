package org.dawnoftime.asedojapan;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import org.dawnoftime.asedojapan.client.guard.EdoGuardArmorModel;
import org.dawnoftime.asedojapan.client.guard.EdoGuardModel;
import org.dawnoftime.asedojapan.client.guard.EdoGuardModelLayers;
import org.dawnoftime.asedojapan.client.guard.EdoGuardRenderer;
import org.dawnoftime.asedojapan.entity.guard.GuardKit;
import org.dawnoftime.asedojapan.registry.FabricEdoGuards;

public class ASEdoJapanClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(EdoGuardModelLayers.GUARD, EdoGuardModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(EdoGuardModelLayers.GUARD_INNER_ARMOR, EdoGuardArmorModel::createInnerArmorLayer);
        EntityModelLayerRegistry.registerModelLayer(EdoGuardModelLayers.GUARD_OUTER_ARMOR, EdoGuardArmorModel::createOuterArmorLayer);
        for (GuardKit kit : GuardKit.values()) {
            EntityRendererRegistry.register(FabricEdoGuards.getType(kit), EdoGuardRenderer::new);
        }
    }
}
