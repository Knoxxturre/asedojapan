package org.dawnoftime.asedojapan;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.dawnoftime.asedojapan.registry.NeoForgeEdoGuards;

@Mod(Constants.MOD_ID)
public class ASEdoJapanNeoForge {

    public ASEdoJapanNeoForge(IEventBus modBus) {
        NeoForgeEdoGuards.register(modBus);
    }
}
