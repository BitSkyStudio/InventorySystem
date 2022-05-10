package com.github.industrialcraft.inventorysystem;

public interface ItemOverflowHandler {
    void onOverflow(Inventory inventory, ItemStack is);
}
