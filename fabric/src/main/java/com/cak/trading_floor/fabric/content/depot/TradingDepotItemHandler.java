package com.cak.trading_floor.fabric.content.depot;

import com.cak.trading_floor.fabric.content.depot.behavior.TradingDepotBehaviour;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;

public class TradingDepotItemHandler implements Storage<ItemVariant> {
    
    TradingDepotBehaviour behaviour;
    
    public TradingDepotItemHandler(TradingDepotBehaviour behaviour) {
        this.behaviour = behaviour;
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        ItemStack currentStack = behaviour.getOfferStack();
        
        if (!resource.matches(currentStack) && !currentStack.isEmpty())
            return 0;
        
        int oldCount = currentStack.getCount();
        
        int resultCount = (int) Math.min(currentStack.getCount() + maxAmount, currentStack.getMaxStackSize());
        int stackChange = resultCount - currentStack.getCount();
        
        ItemStack resultStack = resource.toStack(resultCount);
        transaction.addCloseCallback((context, result) -> {
            if (result.wasCommitted()) {
                behaviour.setOfferStack(resultStack);
                if (resultCount != oldCount)
                    behaviour.spinOfferOrSomething();
                behaviour.blockEntity.sendData();
            }
        });
        
        return stackChange;
    }
    
    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        int targetIndex = -1;
        for (int i = 0; i < behaviour.getResults().size(); i++) {
            if (resource.matches(behaviour.getResults().get(i)))
                targetIndex = i;
            
            if (targetIndex != -1)
                break;
        }
        if (targetIndex == -1) return 0;
        
        ItemStack currentStack = behaviour.getResults().get(targetIndex);
        
        int extractedCount = (int) Math.min(currentStack.getCount(), maxAmount);
        
        ItemStack remainderStack = currentStack.copyWithCount(currentStack.getCount() - extractedCount);
        int finalTargetIndex = targetIndex;
        transaction.addCloseCallback((context, result) -> {
            if (result.wasCommitted()) {
                this.behaviour.getResults().set(finalTargetIndex, remainderStack);
                this.behaviour.queueResultStackPrune();
                behaviour.blockEntity.sendData();
            }
        });
        
        return extractedCount;
    }
    
    @Override
    public Iterator<StorageView<ItemVariant>> iterator() {
        ArrayList<StorageView<ItemVariant>> combined = new ArrayList<>();
        for (int i = 0; i < behaviour.getResults().size(); i++) {
            combined.add(new ResultsStorageView(i));
        }
        combined.add(new OfferStorageView());
        return combined.iterator();
    }
    
    private class OfferStorageView implements StorageView<ItemVariant> {
        
        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            return 0;
        }
        
        @Override
        public boolean isResourceBlank() {
            return behaviour.getOfferStack().isEmpty();
        }
        
        @Override
        public ItemVariant getResource() {
            return ItemVariant.of(behaviour.getOfferStack());
        }
        
        @Override
        public long getAmount() {
            return behaviour.getOfferStack().isEmpty() ? 0 : behaviour.getOfferStack().getCount();
        }
        
        @Override
        public long getCapacity() {
            return behaviour.getOfferStack().isEmpty() ? 64 : behaviour.getOfferStack().getMaxStackSize();
        }
        
    }
    
    private class ResultsStorageView implements StorageView<ItemVariant> {
        
        int pos;
        
        public ResultsStorageView(int pos) {
            this.pos = pos;
        }
        
        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transactionContext) {
            if (behaviour.getResults().size() - 1 < pos)
                return 0;
            
            ItemStack current = behaviour.getResults().get(pos);
            
            if (current.isEmpty())
                return 0;
            if (!resource.matches(current))
                return 0;
            
            int extracted = (int) Math.min(current.getCount(), maxAmount);
            
            transactionContext.addCloseCallback((transaction, result) -> {
                if (result.wasAborted()) return;
                int newCurrentCount = current.getCount() - extracted;
                ItemStack newCurrent = current.copyWithCount(newCurrentCount);
                behaviour.getResults().set(pos, newCurrent);
                behaviour.queueResultStackPrune();
                behaviour.blockEntity.sendData();
            });
            
            return extracted;
        }
        
        @Override
        public boolean isResourceBlank() {
            return getStackSafe().isEmpty();
        }
        
        @Override
        public ItemVariant getResource() {
            return ItemVariant.of(getStackSafe());
        }
        
        private ItemStack getStackSafe() {
            return behaviour.getResults().size() - 1 < pos ? ItemStack.EMPTY : behaviour.getResults().get(pos);
        }
        
        @Override
        public long getAmount() {
            return getStackSafe().isEmpty() ? 0 : getStackSafe().getCount();
        }
        
        @Override
        public long getCapacity() {
            return getStackSafe().isEmpty() ? 64 : getStackSafe().getMaxStackSize();
        }
        
    }
    
}
