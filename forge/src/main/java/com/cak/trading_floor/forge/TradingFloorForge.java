package com.cak.trading_floor.forge;

import com.cak.trading_floor.TradingFloor;
import com.cak.trading_floor.foundation.advancement.TFAdvancements;
import com.cak.trading_floor.forge.network.TFPackets;
import com.cak.trading_floor.registry.*;
import com.simibubi.create.foundation.ponder.PonderLocalization;
import com.simibubi.create.infrastructure.ponder.SharedText;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TradingFloor.MOD_ID)
@SuppressWarnings("unused")
public class TradingFloorForge {
    
    public TradingFloorForge() {
        // registrate must be given the mod event bus on forge before registration
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        TFRegistry.REGISTRATE.registerEventListeners(eventBus);
        TFRegistry.REGISTRATE.addDataGenerator(ProviderType.LANG, TradingFloorForge::addPostInitLang);
        
        TradingFloor.init();
        TFPackets.register();
        
        eventBus.addListener(TradingFloorData::gatherData);
        eventBus.addListener(TradingFloorForge::clientInit);
        
        TradingFloor.LOGGER.info("Finished Initialisation For Mod: " + TradingFloor.MOD_ID);
    }
    
    private static void addPostInitLang(RegistrateLangProvider registrateLangProvider) {
        TFPonderTags.register();
        TFPonderIndex.register();
        
        SharedText.gatherText();
        PonderLocalization.generateSceneLang();
        
        PonderLocalization.provideLang(TradingFloor.MOD_ID, registrateLangProvider::add);
        
        TFAdvancements.provideLang(registrateLangProvider::add);
    }
    
    public static void clientInit(final FMLClientSetupEvent event) {
        TradingFloor.clientInit();
    }
    
}
