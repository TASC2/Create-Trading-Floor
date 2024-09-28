package com.cak.trading_floor.forge.mixin.item_listings;

import com.cak.trading_floor.forge.compat.jei.virtual_recipes.potential_villager_trade.PotentialMerchantOfferInfo;
import com.cak.trading_floor.forge.foundation.access.ResolvableItemListing;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.world.entity.npc.VillagerTrades$EnchantedItemForEmeralds")
public class EnchantedItemForEmeraldsAccessMixin implements ResolvableItemListing {
    
    @Shadow @Final private int baseEmeraldCost;
    
    @Shadow @Final private ItemStack itemStack;
    
    @Override
    public @Nullable PotentialMerchantOfferInfo create_trading_floor$resolve() {
        return new PotentialMerchantOfferInfo(
            Items.EMERALD.getDefaultInstance().copyWithCount(baseEmeraldCost),
            ItemStack.EMPTY,
            itemStack
        ).withImpliedEnchantVariants();
    }
    
}