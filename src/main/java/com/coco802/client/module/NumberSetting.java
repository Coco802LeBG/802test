package com.coco802.client.module;

import com.coco802.client.Client802;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

public final class NumberSetting extends Setting {
    public final double min, max, step;
    private final double def;
    private final DoubleSupplier getter;
    private final DoubleConsumer setter;

    public NumberSetting(Module802 module, String key, String label, double min, double max, double step, double def) {
        super(module, key, label);
        this.min = min;
        this.max = max;
        this.step = step;
        this.def = def;
        this.getter = null;
        this.setter = null;
    }

    /** Réglage branché sur un autre système (ex: Crystal Reflex). */
    public NumberSetting(Module802 module, String key, String label, double min, double max, double step,
                         DoubleSupplier getter, DoubleConsumer setter) {
        super(module, key, label);
        this.min = min;
        this.max = max;
        this.step = step;
        this.def = min;
        this.getter = getter;
        this.setter = setter;
    }

    public double get() { return getter != null ? getter.getAsDouble() : Client802.CONFIG.number(id(), def); }
    public int getInt() { return (int) Math.round(get()); }

    public void set(double v) {
        if (setter != null) setter.accept(v);
        else Client802.CONFIG.numbers.put(id(), v);
    }
}
