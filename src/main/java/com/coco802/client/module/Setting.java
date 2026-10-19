package com.coco802.client.module;

public abstract class Setting {
    public final Module802 module;
    public final String key;
    public final String label;

    protected Setting(Module802 module, String key, String label) {
        this.module = module;
        this.key = key;
        this.label = label;
    }

    public String id() { return module.name + "." + key; }
}
