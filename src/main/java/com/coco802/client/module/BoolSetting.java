package com.coco802.client.module;

import com.coco802.client.Client802;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public final class BoolSetting extends Setting {
    private final boolean def;
    private final BooleanSupplier getter;
    private final Consumer<Boolean> setter;

    public BoolSetting(Module802 module, String key, String label, boolean def) {
        super(module, key, label);
        this.def = def;
        this.getter = null;
        this.setter = null;
    }

    /** Réglage branché sur un autre système (ex: Crystal Reflex). */
    public BoolSetting(Module802 module, String key, String label, BooleanSupplier getter, Consumer<Boolean> setter) {
        super(module, key, label);
        this.def = false;
        this.getter = getter;
        this.setter = setter;
    }

    public boolean get() { return getter != null ? getter.getAsBoolean() : Client802.CONFIG.bool(id(), def); }

    public void set(boolean v) {
        if (setter != null) setter.accept(v);
        else Client802.CONFIG.bools.put(id(), v);
    }
}
