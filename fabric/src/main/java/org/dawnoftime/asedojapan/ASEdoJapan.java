package org.dawnoftime.asedojapan;

import net.fabricmc.api.ModInitializer;
import org.dawnoftime.asedojapan.registry.FabricEdoGuards;

public class ASEdoJapan implements ModInitializer {

    @Override
    public void onInitialize() {
        FabricEdoGuards.register();
    }
}
