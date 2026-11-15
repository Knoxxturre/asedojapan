package org.dawnoftime.asedojapan.client.guard;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import org.dawnoftime.asedojapan.Constants;

/** Model layers used by the Edo guard renderer; each loader registers them with the matching layer definitions. */
public final class EdoGuardModelLayers {

    public static final ModelLayerLocation GUARD = layer("main");
    public static final ModelLayerLocation GUARD_INNER_ARMOR = layer("inner_armor");
    public static final ModelLayerLocation GUARD_OUTER_ARMOR = layer("outer_armor");

    private EdoGuardModelLayers() {
    }

    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "edo_guard"), name);
    }
}
