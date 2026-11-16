package com.creamclient.module;

import java.util.Arrays;
import java.util.List;

public abstract class Setting {
    private final String name;
    protected Setting(String name) { this.name = name; }
    public String getName() { return name; }

    public static class BoolSetting extends Setting {
        private boolean value;
        public BoolSetting(String name, boolean defaultValue) { super(name); this.value = defaultValue; }
        public boolean isEnabled() { return value; }
        public void setEnabled(boolean value) { this.value = value; }
        public void toggle() { this.value = !this.value; }
    }

    public static class SliderSetting extends Setting {
        private double value;
        private final double min, max, step;
        public SliderSetting(String name, double defaultValue, double min, double max, double step) {
            super(name); this.value = defaultValue; this.min = min; this.max = max; this.step = step;
        }
        public double getValue() { return value; }
        public float getFloat() { return (float) value; }
        public void setValue(double v) {
            double clamped = Math.max(min, Math.min(max, v));
            double stepped = Math.round(clamped / step) * step;
            this.value = Math.round(stepped * 100.0) / 100.0;
        }
        public double getMin() { return min; }
        public double getMax() { return max; }
        public double getStep() { return step; }
    }

    public static class ModeSetting extends Setting {
        private final List<String> modes;
        private int index;
        public ModeSetting(String name, String defaultMode, String... modes) {
            super(name); this.modes = Arrays.asList(modes);
            this.index = Math.max(0, this.modes.indexOf(defaultMode));
        }
        public String getMode() { return modes.get(index); }
        public void setMode(String mode) {
            int idx = modes.indexOf(mode);
            if (idx >= 0) this.index = idx;
        }
        public void cycle() { index = (index + 1) % modes.size(); }
    }
}
