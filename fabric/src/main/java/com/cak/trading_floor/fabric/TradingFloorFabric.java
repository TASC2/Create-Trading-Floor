package com.cak.trading_floor.fabric;

import com.cak.trading_floor.TradingFloor;
import net.fabricmc.api.ModInitializer;

public class TradingFloorFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        TradingFloor.init();
//        ExampleMod.LOGGER.info(EnvExecutor.unsafeRunForDist(
//                () -> () -> "{} is accessing Porting Lib on a Fabric client!",
//                () -> () -> "{} is accessing Porting Lib on a Fabric server!"
//                ), ExampleMod.NAME);
//        // on fabric, Registrates must be explicitly finalized and registered.
//        ExampleBlocks.REGISTRATE.register();
    }
}