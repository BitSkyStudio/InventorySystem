package com.github.industrialcraft.inventorysystem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Inventory {
    ItemStack items[];
    ItemOverflowHandler overflowHandler;
    Object data;
    public Inventory(int size, ItemOverflowHandler handler, Object data) {
        this.items = new ItemStack[size];
        for(int i = 0;i < size;i++){
            setAt(i, null);
        }
        this.overflowHandler = handler;
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
    public void putAt(int index, ItemStack is, EItemPutMode mode){
        putAt(index, is, mode.isOverwrite(), mode.isDump());
    }
    public void putAt(int index, ItemStack is, boolean overwrite, boolean dump){
        ItemStack item = is;
        if(item.getCount() <= 0)
            item = null;
        if(overwrite) {
            if (canPut(index, item)) {
                if(getAt(index) != null && dump)
                    overflow(items[index]);

                setAt(index, item);
            }
        } else {
            if (canPut(index, item)) {
                if (getAt(index) == null) {
                    setAt(index, is);
                    return;
                }
            }
            if (dump)
                overflow(is);
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
    public void addItem(ItemStack item){
        int toAdd = item.getCount();
        for(int i = 0;i < items.length;i++){
            if(getAt(i)!=null&&item.stacks(items[i]) && canPut(i, item)){
                int toSupply = Math.min(getStackSize(item)-items[i].getCount(), toAdd);
                if(toSupply > 0) {
                    toAdd -= toSupply;
                    items[i].addCount(toSupply);
                    setAt(i, items[i]);
                    if(toAdd <= 0)
                        return;
                }
            }
        }
        for(int i = 0;i < items.length;i++){
            if(getAt(i) == null && canPut(i, item)){
                setAt(i, item.clone(toAdd));
                return;
            }
        }
        overflow(item.clone(toAdd));
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

    public void overflow(ItemStack is){
        if(is != null)
            this.overflowHandler.onOverflow(this, is);
    }

    public void clear(){
        for(int i = 0;i < items.length;i++){
            setAt(i, null);
        }
    }
    public void dropAll(){
        for(ItemStack is : items){
            if(is != null && is.getCount() > 0)
                overflow(is);
        }
        clear();
    }
    public void drop(int slot){
        ItemStack is = getAt(slot);
        if(is == null)
            return;
        overflow(is);
        setAt(slot, null);
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
        for(int i = 0;i < content.stacks.length;i++){
            if(i < items.length)
                setAt(i, content.stacks[i]==null?null:content.stacks[i].clone());
            else
                overflow(content.stacks[i]==null?null:content.stacks[i].clone());
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
