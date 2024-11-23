package com.cak.trading_floor.foundation;

import net.minecraft.world.item.ItemStack;

/**Backport util*/
public class ItemCopyWithCount {
    
    public static ItemStack of(ItemStack stack, int count) {
        ItemStack newStack = stack.copy();
        newStack.setCount(count);
        return newStack;
    }
    
}