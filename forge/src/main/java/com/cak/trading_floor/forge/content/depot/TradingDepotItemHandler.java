package com.cak.trading_floor.forge.content.depot;

import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

public class TradingDepotItemHandler implements IItemHandler {
    
    TradingDepotBehaviour behaviour;
    
    public TradingDepotItemHandler(TradingDepotBehaviour behaviour) {
        this.behaviour = behaviour;
    }
    
    @Override
    public int getSlots() {
        return 1 + behaviour.result.size();
    }
    
    @Override
    public @NotNull ItemStack getStackInSlot(int i) {
        return i == 0 ? behaviour.getOfferStack() : behaviour.result.get(i - 1);
    }
    
    @Override
    public @NotNull ItemStack insertItem(int i, @NotNull ItemStack arg, boolean bl) {
        if (i != 0) return arg;
        
        if (!behaviour.getOfferStack().isEmpty() && !ItemHandlerHelper.canItemStacksStack(behaviour.getOfferStack(), arg)) return arg;
        
        ItemStack incomingStack = behaviour.getOfferStack();

        int newCount = Math.min(incomingStack.getMaxStackSize(), incomingStack.getCount() + arg.getCount());
        int added = newCount - incomingStack.getCount();
        int remaining = arg.getCount() - added;
        
        if (!bl) {
            behaviour.setOfferStack(new TransportedItemStack(arg.copyWithCount(newCount)));
            behaviour.blockEntity.sendData();
        }
        
        return arg.copyWithCount(remaining);
    }
    
    @Override
    public @NotNull ItemStack extractItem(int i, int j, boolean bl) {
        if (i == 0) return ItemStack.EMPTY;
        
        ItemStack currentStack = behaviour.result.get(i - 1);
        
        int extractedCount = Math.min(currentStack.getCount(), j);
        
        ItemStack resultStack = currentStack.copyWithCount(extractedCount);
        ItemStack remainderStack = currentStack.copyWithCount(currentStack.getCount() - extractedCount);
        
        if (!bl) {
            if (remainderStack.isEmpty())
                this.behaviour.result.remove(i - 1);
            else
                this.behaviour.result.set(i, remainderStack);
            behaviour.blockEntity.sendData();
        }
        
        return resultStack;
    }
    
    @Override
    public int getSlotLimit(int i) {
        return 64;
    }
    
    @Override
    public boolean isItemValid(int i, @NotNull ItemStack arg) {
        return true;
    }
    
    public ItemStack insertItem(TransportedItemStack transportedItemStack, Direction direction, boolean b) {
        return insertItem(0, transportedItemStack.stack, b);
    }
    
}