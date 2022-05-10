package com.github.industrialcraft.inventorysystem;

public class ItemStack {
    private final IItem item;
    private int count;
    private ItemData data;
    public ItemStack(IItem item, int count) {
        this.item = item;
        this.count = count;
        this.data = item.createData(this);
    }
    public ItemStack(IItem item, int count, ItemData data) {
        this.item = item;
        this.count = count;
        this.data = data;
    }
    public IItem getItem() {
        return item;
    }
    public int getCount() {
        return count;
    }
    public int setCount(int count) {
        this.count = count;
        return fixCount();
    }
    public int addCount(int count){
        this.count += count;
        return fixCount();
    }
    public void removeCount(int count){
        this.count -= count;
        fixCount();
    }
    public ItemStack split(){
        ItemStack cloned = this.clone();
        float half = ((float)this.getCount())/2f;
        this.setCount((int) Math.ceil(half));
        cloned.setCount((int) Math.floor(half));
        return cloned;
    }
    public ItemStack split(int amount){
        if(amount >= this.getCount())
            throw new RuntimeException("attempting to split more than allowed");
        if(amount == 0)
            return null;
        ItemStack cloned = this.clone();
        this.removeCount(amount);
        cloned.setCount(amount);
        return cloned;
    }

    public ItemData getData() {
        return data;
    }

    public ItemStack clone(){
        return new ItemStack(item, count, data.clone());
    }
    public ItemStack clone(int newCount){
        return new ItemStack(item, newCount, data.clone());
    }

    public boolean stacks(ItemStack is){
        if(is == null)
            return false;
        if(is.getItem() != getItem())
            return false;
        return is.getData().stacks(is.getData());
    }

    private int fixCount(){
        if(count > item.getStackSize()){
            int retVal = count-getItem().getStackSize();
            count = getItem().getStackSize();
            return retVal;
        }
        if(count < 0)
            count = 0;
        return 0;
    }
}
