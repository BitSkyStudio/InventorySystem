package com.github.industrialcraft.inventorysystem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Inventory {
    ItemStack items[];
    Object data;
    public Inventory(int size, Object data) {
        this.items = new ItemStack[size];
        for(int i = 0;i < size;i++){
            setAt(i, null);
        }
        this.data = data;
    }
    public ItemStack getAt(int index){
        ItemStack is = this.items[index];
        if(is != null && is.getCount() <= 0){
            setAt(index, null);
        }
        return items[index];
    }
    public ItemStack setAt(int index, ItemStack itemStack){
        if(itemStack != null && itemStack.getCount() <= 0) {
            this.items[index] = null;
            return null;
        } else {
            this.items[index] = itemStack;
            return itemStack;
        }
    }
    public ItemStack putAt(int index, ItemStack is){
        if(is == null || is.getCount() <= 0)
            return null;

        if (canPut(index, is)) {
            ItemStack current = getAt(index);
            if (current == null) {
                setAt(index, is);
                return null;
            } else {
                int toDeposit = Math.min(current.getItem().getStackSize()-current.getCount(), is.getCount());
                is.removeCount(toDeposit);
                current.addCount(toDeposit);
                if(is.getCount() <= 0)
                    return null;
                return is;
            }
        } else {
            return is;
        }
    }

    public int count(IItem item){
        int count = 0;
        for(ItemStack is : items){
            if(is == null)
                continue;
            if(is.getItem() == item){
                count += is.getCount();
            }
        }
        return count;
    }
    public int count(Predicate<ItemStack> p){
        int count = 0;
        for(ItemStack is : items){
            if(is == null)
                continue;
            if(p.test(is)){
                count += is.getCount();
            }
        }
        return count;
    }
    public int getRemainingSpaceFor(ItemStack item){
        int space = 0;
        for(int i = 0;i < items.length;i++){
            if(getAt(i)!=null){
                if(item.stacks(items[i]) && canPut(i, item)){
                    int toSupply = Math.max(getStackSize(item) - items[i].getCount(), 0);
                    space += toSupply;
                }
            } else {
                if(getAt(i) == null && canPut(i, item)){
                    space += getStackSize(item);
                }
            }
        }
        return space;
    }
    public ItemStack addItem(ItemStack item){
        int toAdd = item.getCount();
        for(int i = 0;i < items.length;i++){
            if(getAt(i)!=null&&item.stacks(items[i]) && canPut(i, item)){
                int toSupply = Math.min(getStackSize(item)-items[i].getCount(), toAdd);
                if(toSupply > 0) {
                    toAdd -= toSupply;
                    items[i].addCount(toSupply);
                    setAt(i, items[i]);
                    if(toAdd <= 0)
                        return null;
                }
            }
        }
        for(int i = 0;i < items.length;i++){
            if(getAt(i) == null && canPut(i, item)){
                setAt(i, item.clone(toAdd));
                return null;
            }
        }
        return item.clone(toAdd);
    }
    public List<ItemStack> removeItems(IItem item, int count){
        return removeItems(itemStack -> itemStack.getItem() == item, count);
    }
    public List<ItemStack> removeItems(Predicate<ItemStack> p, int count){
        if(count(p) < count)
            return null;
        ArrayList<ItemStack> removedItems = new ArrayList<>();
        int toRemove = count;
        for(int i = 0;i < items.length;i++){
            if(getAt(i)==null)
                continue;
            if(!p.test(items[i]))
                continue;
            int removed = Math.min(toRemove,items[i].getCount());
            removedItems.add(items[i].clone(removed));
            items[i].removeCount(removed);
            setAt(i, items[i]);
            toRemove -= removed;
            if(toRemove <= 0)
                break;
        }
        return removedItems;
    }

    public boolean canPut(int index, ItemStack is){
        return true;
    }

    public void clear(){
        for(int i = 0;i < items.length;i++){
            setAt(i, null);
        }
    }
    public ItemStack take(int slot, int count, boolean exact){
        ItemStack is = getAt(slot);
        if(is == null)
            return null;
        if(exact && is.getCount() < count)
            return null;
        int toRemove = Math.min(count, is.getCount());
        is.removeCount(toRemove);
        setAt(slot, is);
        return is.clone(toRemove);
    }
    public boolean isEmpty(){
        for(ItemStack is : items){
            if(is != null && is.getCount() > 0)
                return false;
        }
        return true;
    }
    public InventoryContent saveContent(){
        ItemStack[] contentItems = new ItemStack[items.length];
        for(int i = 0;i < items.length;i++){
            if(items[i] == null)
                contentItems[i] = null;
            else
                contentItems[i] = items[i].clone();
        }
        return new InventoryContent(contentItems);
    }
    public void loadContent(InventoryContent content){
        clear();
        for(int i = 0;i < Math.min(content.stacks.length, items.length);i++){
            setAt(i, content.stacks[i]==null?null:content.stacks[i].clone());
        }
    }
    public int getStackSize(ItemStack item){
        return item.getItem().getStackSize();
    }

    public <T> T getData() {
        return (T) data;
    }
    public int getSize() {
        return items.length;
    }
}
