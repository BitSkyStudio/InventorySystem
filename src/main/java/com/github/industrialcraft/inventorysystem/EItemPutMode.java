package com.github.industrialcraft.inventorysystem;

public enum EItemPutMode {
    OVERWRITE_DUMP(true, true),
    OVERWRITE_DESTROY(true, false),
    KEEP_DUMP(false, true),
    KEEP_DESTROY(false, false);
    private boolean overwrite;
    private boolean dump;
    EItemPutMode(boolean overwrite, boolean dump) {
        this.overwrite = overwrite;
        this.dump = dump;
    }
    public boolean isOverwrite() {
        return overwrite;
    }
    public boolean isDump() {
        return dump;
    }
}
