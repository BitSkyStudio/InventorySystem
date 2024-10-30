package com.github.industrialcraft.inventorysystem;

public abstract class ItemData {
    private final ItemStack is;
    public ItemData(ItemStack is) {
        this.is = is;
    }
    public ItemStack getItemStack() {
        return is;
    }
    public abstract ItemData clone(ItemStack is);
    public boolean stacks(ItemData itemData){
        return true;
    }
}
