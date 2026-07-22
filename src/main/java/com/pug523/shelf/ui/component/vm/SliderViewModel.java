package com.pug523.shelf.ui.component.vm;

import com.pug523.shelf.common.compat.JavaCompat;

import java.util.function.Consumer;

public class SliderViewModel {
    private final double min;
    private final double max;
    private final double step;
    private final Consumer<Double> valueConsumer;

    private double value;

    public SliderViewModel(double min, double max, double step, double initialValue, Consumer<Double> valueConsumer) {
        this.min = min;
        this.max = max;
        this.step = step;
        this.valueConsumer = valueConsumer;

        this.value = clampAndSnap(initialValue);
    }

    public double getValue() {
        return value;
    }

    public void setValue(double newValue) {
        double clamped = clampAndSnap(newValue);
        if (this.value != clamped) {
            this.value = clamped;
            if (valueConsumer != null) {
                valueConsumer.accept(this.value);
            }
        }
    }

    public void setProgress(double progress) {
        double normalizedProgress = JavaCompat.clamp(progress, 0.0, 1.0);
        double rawValue = min + (max - min) * normalizedProgress;
        setValue(rawValue);
    }

    public double getProgress() {
        if (max <= min) return 0.0;
        return JavaCompat.clamp((value - min) / (max - min), 0.0, 1.0);
    }

    public void step(double direction) {
        double changeAmount = (step > 0.0) ? step : (max - min) / 100.0;
        setValue(this.value + (changeAmount * direction));
    }

    private double clampAndSnap(double val) {
        double clamped = JavaCompat.clamp(val, min, max);
        if (step > 0.0) {
            clamped = Math.round((clamped - min) / step) * step + min;
            clamped = JavaCompat.clamp(clamped, min, max);
        }
        return clamped;
    }
}
