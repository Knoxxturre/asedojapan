package org.dawnoftime.asedojapan;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.dawnoftime.asedojapan.registry.ForgeEdoGuards;

@Mod(Constants.MOD_ID)
public class ASEdoJapan {

    public ASEdoJapan() {
        ForgeEdoGuards.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
