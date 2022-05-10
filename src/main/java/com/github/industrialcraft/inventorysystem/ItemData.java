package com.github.industrialcraft.inventorysystem;

public class ItemData {
    private final ItemStack is;
    public ItemData(ItemStack is) {
        this.is = is;
    }
    public ItemStack getItemStack() {
        return is;
    }
    public ItemData clone(){
        return new ItemData(is);
    }
    public boolean stacks(ItemData itemData){
        return true;
    }
}
