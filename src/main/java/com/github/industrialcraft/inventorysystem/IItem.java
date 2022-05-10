package com.github.industrialcraft.inventorysystem;

public interface IItem {
    int getStackSize();
    default ItemData createData(ItemStack is){return null;}
}
