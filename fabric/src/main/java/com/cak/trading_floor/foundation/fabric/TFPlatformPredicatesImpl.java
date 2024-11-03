package com.cak.trading_floor.foundation.fabric;

import io.github.fabricators_of_create.porting_lib.fake_players.FakePlayer;
import net.minecraft.world.entity.LivingEntity;

public class TFPlatformPredicatesImpl {
    
    public static boolean isFakePlayer(LivingEntity player) {
        return !(player instanceof FakePlayer);
    }
    
}
