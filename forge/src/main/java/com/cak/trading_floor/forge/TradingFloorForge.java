package com.cak.trading_floor.forge;

import com.cak.trading_floor.TradingFloor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TradingFloor.MOD_ID)
public class TradingFloorForge {
    
    public TradingFloorForge() {
        // registrate must be given the mod event bus on forge before registration
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        TFRegistry.REGISTRATE.registerEventListeners(eventBus);
        TradingFloor.init();
        TFRegistry.init();
    }
    
}
