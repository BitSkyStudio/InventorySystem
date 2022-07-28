package com.github.industrialcraft.inventorysystem;

import java.util.ArrayList;

public class InventoryContent {
    public final ItemStack[] stacks;
    public InventoryContent(ItemStack[] stacks) {
        this.stacks = stacks;
        for(int i = 0;i < stacks.length;i++){
            if(stacks[i] != null && stacks[i].getCount() <= 0)
                stacks[i] = null;
        }
    }
}
