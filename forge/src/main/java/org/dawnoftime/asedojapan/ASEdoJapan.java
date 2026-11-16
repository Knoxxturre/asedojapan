package org.dawnoftime.asedojapan;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.dawnoftime.asedojapan.registry.ForgeEdoGuards;
import org.dawnoftime.asedojapan.registry.ForgeEdoHostiles;

@Mod(Constants.MOD_ID)
public class ASEdoJapan {

    public ASEdoJapan() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ForgeEdoGuards.register(modBus);
        ForgeEdoHostiles.register(modBus);
    }
}
