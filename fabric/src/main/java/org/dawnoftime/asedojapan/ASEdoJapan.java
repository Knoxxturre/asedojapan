package org.dawnoftime.asedojapan;

import net.fabricmc.api.ModInitializer;
import org.dawnoftime.asedojapan.registry.FabricEdoGuards;
import org.dawnoftime.asedojapan.registry.FabricEdoHostiles;

public class ASEdoJapan implements ModInitializer {

    @Override
    public void onInitialize() {
        FabricEdoGuards.register();
        FabricEdoHostiles.register();
    }
}
