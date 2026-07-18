package com.pug523.shelf.gui.widget;

import java.util.function.Consumer;

import com.mojang.blaze3d.platform.InputConstants;
import com.pug523.shelf.compat.GuiCompat;
import com.pug523.shelf.compat.JavaCompat;
import com.pug523.shelf.gui.layout.Bounds;
import com.pug523.shelf.gui.layout.LayoutEngine;
import com.pug523.shelf.gui.renderer.RenderUtil;

import net.minecraft.client.gui.Font;

public class SliderWidget implements ClickableWidget {
    public enum Orientation {
        HORIZONTAL, VERTICAL
    }

    private final double min;
    private final double max;
    private final double step;
    private final Consumer<Double> valueConsumer;

    private double value;
    private Bounds cachedWidgetBounds;
    private boolean isDragging = false;

    private Orientation orientation = Orientation.HORIZONTAL;
    private int barThickness = 4;
    private int knobSize = 8;
    private boolean rounded = true;

    private int colorTrackStart = 0xFF4B5563;
    private int colorTrackEnd = 0xFF4B5563;
    private int colorProgressStart = 0xFF3B82F6;
    private int colorProgressEnd = 0xFF3B82F6;

    private int colorKnob = 0xFFFFFFFF;

    public SliderWidget(double min, double max, double step, double initialValue, Consumer<Double> valueConsumer) {
        this.min = min;
        this.max = max;
        this.step = step;
        this.value = JavaCompat.clamp(initialValue, min, max);
        this.valueConsumer = valueConsumer;
    }

    public SliderWidget setOrientation(Orientation orientation) {
        this.orientation = orientation;
        return this;
    }

    public SliderWidget setBarThickness(int thickness) {
        this.barThickness = thickness;
        return this;
    }

    public SliderWidget setKnobSize(int knobSize) {
        this.knobSize = knobSize;
        return this;
    }

    public SliderWidget setRounded(boolean rounded) {
        this.rounded = rounded;
        return this;
    }

    public SliderWidget setColors(int track, int progress, int knob) {
        this.colorTrackStart = track;
        this.colorTrackEnd = track;
        this.colorProgressStart = progress;
        this.colorProgressEnd = progress;
        this.colorKnob = knob;
        return this;
    }

    public SliderWidget setGradientColors(int trackStart, int trackEnd, int progressStart, int progressEnd, int knob) {
        this.colorTrackStart = trackStart;
        this.colorTrackEnd = trackEnd;
        this.colorProgressStart = progressStart;
        this.colorProgressEnd = progressEnd;
        this.colorKnob = knob;
        return this;
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        this.value = JavaCompat.clamp(value, min, max);
    }

    @Override
    public void render(Font font, GuiCompat gui, LayoutEngine layout, int x, int y, int width, int height, int mouseX,
            int mouseY) {
        this.cachedWidgetBounds = new Bounds(x, y, width, height);

        double progress = JavaCompat.clamp((this.value - min) / (max - min), 0.0, 1.0);

        if (orientation == Orientation.HORIZONTAL) {
            int sliderY = y + (height - barThickness) / 2;
            int progressLength = (int) (width * progress);
            int progressEnd = x + progressLength;
            int currentProgressEndColor = RenderUtil.linearInterpolateColors(colorProgressStart, colorProgressEnd, (float) progress);

            if (rounded) {
                RenderUtil.renderCapsuleHorizontal(gui, x, sliderY, width, barThickness, colorTrackStart, colorTrackEnd);
                if (progressLength > 0) {
                    RenderUtil.renderCapsuleHorizontal(gui, x, sliderY, progressLength, barThickness, colorProgressStart, currentProgressEndColor);
                }
                RenderUtil.renderCircle(gui, progressEnd, sliderY + (barThickness / 2.0f), knobSize / 2.25f, colorKnob);
            } else {
                RenderUtil.renderRectHorizontal(gui, x, sliderY, width, barThickness, colorTrackStart, colorTrackEnd);
                RenderUtil.renderRectHorizontal(gui, x, sliderY, progressEnd, barThickness, colorProgressStart, currentProgressEndColor);
                int knobX = progressEnd - (knobSize / 2);
                int knobY = sliderY + (barThickness / 2) - (knobSize / 2);
                gui.fill(knobX, knobY, knobX + knobSize, knobY + knobSize, colorKnob);
            }
        } else {
            int sliderX = x + (width - barThickness) / 2;
            int progressLength = (int) (height * progress);
            int progressStart = y + height - progressLength;
            int currentProgressStartColor = RenderUtil.linearInterpolateColors(colorProgressEnd, colorProgressStart, (float) progress);

            if (rounded) {
                RenderUtil.renderCapsuleVertical(gui, sliderX, y, barThickness, height, colorTrackStart, colorTrackEnd);
                if (progressLength > 0) {
                    RenderUtil.renderCapsuleVertical(gui, sliderX, progressStart, barThickness, progressLength, currentProgressStartColor, colorProgressEnd);
                }
                RenderUtil.renderCircle(gui, sliderX + (barThickness / 2.0f), progressStart, knobSize / 2.25f, colorKnob);
            } else {
                RenderUtil.renderRectVertical(gui, sliderX, y, barThickness, height, colorTrackStart, colorTrackEnd);
                if (progressLength > 0) {
                    RenderUtil.renderRectVertical(gui, sliderX, y, barThickness, height, currentProgressStartColor, colorProgressEnd);
                }
                int knobX = sliderX + (barThickness / 2) - (knobSize / 2);
                int knobY = progressStart - (knobSize / 2);
                gui.fill(knobX, knobY, knobX + knobSize, knobY + knobSize, colorKnob);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int modifiers, LayoutEngine layout) {
        if (button == InputConstants.MOUSE_BUTTON_LEFT && cachedWidgetBounds != null) {
            if (cachedWidgetBounds.contains(mouseX, mouseY)) {
                updateValueFromMouse(mouseX, mouseY);
                this.isDragging = true;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY,
            LayoutEngine layout) {
        if (button == InputConstants.MOUSE_BUTTON_LEFT && this.isDragging && cachedWidgetBounds != null) {
            updateValueFromMouse(mouseX, mouseY);
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button, LayoutEngine layout) {
        this.isDragging = false;
    }

    @Override
    public boolean keyPressed(int keycode, int scancode, int modifiers, LayoutEngine layout) {
        if (keycode == InputConstants.KEY_LEFT || keycode == InputConstants.KEY_RIGHT) {
            double changeAmount = (this.step > 0.0) ? this.step : (this.max - this.min) / 100.0;
            double direction = (keycode == InputConstants.KEY_LEFT) ? -1.0 : 1.0;
            double newValue = this.value + (changeAmount * direction);

            if (this.step > 0.0) {
                newValue = Math.round(newValue / this.step) * this.step;
            }

            newValue = JavaCompat.clamp(newValue, this.min, this.max);

            if (newValue != this.value) {
                this.value = newValue;
                this.valueConsumer.accept(this.value);
            }
            return true;
        }
        return false;
    }

    private void updateValueFromMouse(double mouseX, double mouseY) {
        double pct;
        if (orientation == Orientation.HORIZONTAL) {
            pct = JavaCompat.clamp((mouseX - cachedWidgetBounds.x) / (double) cachedWidgetBounds.width, 0.0, 1.0);
        } else {
            pct = JavaCompat.clamp(1.0 - ((mouseY - cachedWidgetBounds.y) / (double) cachedWidgetBounds.height), 0.0,
                    1.0);
        }

        double rawValue = min + (max - min) * pct;
        if (step > 0.0) {
            rawValue = Math.round(rawValue / step) * step;
        }
        rawValue = JavaCompat.clamp(rawValue, min, max);

        this.value = rawValue;
        this.valueConsumer.accept(rawValue);
    }
}
