package com.pug523.shelf.gui.widget.option;

import java.util.function.Function;

import com.pug523.shelf.compat.ComponentCompat;
import com.pug523.shelf.compat.GuiCompat;
import com.pug523.shelf.config.Option;
import com.pug523.shelf.gui.layout.LayoutConfig;
import com.pug523.shelf.gui.layout.LayoutEngine;

import com.pug523.shelf.gui.widget.SliderWidget;
import com.pug523.shelf.gui.widget.TextInputFieldWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.util.Mth;

public class SliderOptionWidget<N extends Number & Comparable<N>> extends OptionWidget<N> {
    private final double min;
    private final double max;
    private final double step;
    private final Function<Double, N> typeConverter;

    private final SliderWidget slider;
    private final TextInputFieldWidget<String> textField;

    private int maxTextBoundsWidth = -1;

    public SliderOptionWidget(Option<N> option, N min, N max, N step, Function<Double, N> typeConverter) {
        super(option);
        this.min = min.doubleValue();
        this.max = max.doubleValue();
        this.step = step.doubleValue();
        this.typeConverter = typeConverter;

        this.slider = new SliderWidget(
            this.min,
            this.max,
            this.step,
            this.getPendingValue().doubleValue(),
            this::updateFromSlider
        );

        this.textField = new TextInputFieldWidget<>(
            true,
            str -> {
                if (str.isEmpty() || str.equals("-") || str.equals(".") || str.equals("-.")) {
                    return true;
                }
                try {
                    Double.parseDouble(str);
                    return true;
                } catch (NumberFormatException ignored) {
                    return false;
                }
            },
            str -> {
                try {
                    double rawValue = Double.parseDouble(str);
                    return rawValue >= this.min && rawValue <= this.max;
                } catch (NumberFormatException ignored) {
                    return false;
                }
            },
            str -> {
                try {
                    double rawValue = Double.parseDouble(str);
                    if (this.step > 0.0) {
                        rawValue = Math.round(rawValue / this.step) * this.step;
                    }
                    rawValue = Mth.clamp(rawValue, this.min, this.max);
                    this.setPendingValue(this.typeConverter.apply(rawValue));
                    this.slider.setValue(rawValue);
                } catch (NumberFormatException ignored) {
                }
            },
            null,
            formatValue(this.getPendingValue().doubleValue())
        );
    }

    public static SliderOptionWidget<Integer> ofInt(Option<Integer> option, int min, int max, int step) {
        return new SliderOptionWidget<>(option, min, max, step, d -> (int) Math.round(d));
    }

    public static SliderOptionWidget<Double> ofDouble(Option<Double> option, double min, double max, double step) {
        return new SliderOptionWidget<>(option, min, max, step, d -> d);
    }

    public static SliderOptionWidget<Float> ofFloat(Option<Float> option, float min, float max, float step) {
        return new SliderOptionWidget<>(option, min, max, step, Double::floatValue);
    }

    @Override
    public void render(Font font, GuiCompat gui, LayoutEngine layout, int x, int y, int width, int height, int mouseX, int mouseY) {
        if (!this.textField.isFocused()) {
            double currentValue = getPendingValue().doubleValue();
            String formatted = formatValue(currentValue);
            this.textField.setText(formatted);
            this.slider.setValue(currentValue);
        }

        LayoutConfig cfg = layout.getConfig();
        this.slider.setOrientation(SliderWidget.Orientation.HORIZONTAL)
                   .setBarThickness(cfg.sliderHeight)
                   .setKnobSize(cfg.sliderKnobSize)
                   .setRounded(cfg.roundedSlider)
                   .setColors(cfg.colorSliderTrack, cfg.colorSliderProgress, cfg.colorSliderKnob);

        int sliderX = x + width - cfg.sliderWidth - layout.optionWidgetRightMargin;

        this.slider.render(font, gui, layout, sliderX, y, cfg.sliderWidth, height, mouseX, mouseY);

        // Text
        if (this.maxTextBoundsWidth == -1) {
            String minStr = formatValue(this.min);
            String maxStr = formatValue(this.max);
            this.maxTextBoundsWidth = Math.max(ComponentCompat.width(font, minStr), ComponentCompat.width(font, maxStr));
        }

        String currentText = this.textField.getText();
        int activeTextWidth = ComponentCompat.width(font, currentText);

        if (this.textField.isFocused()) {
            activeTextWidth += ComponentCompat.width(font, "_");
        }

        int currentFieldWidth = Math.min(this.maxTextBoundsWidth, activeTextWidth);

        int textX = sliderX - currentFieldWidth - cfg.sliderTextPadding;
        int textY = y + (height - font.lineHeight) / 2 + 1;

        this.textField.render(font, gui, layout, textX, textY, currentFieldWidth, font.lineHeight, mouseX, mouseY);
    }

    private void updateFromSlider(double value) {
        this.setPendingValue(this.typeConverter.apply(value));
        if (!this.textField.isFocused()) {
            this.textField.setText(formatValue(value));
        }
    }

    @Override
    public void resetPendingToDefault() {
        super.resetPendingToDefault();
        double defaultValue = this.getPendingValue().doubleValue();
        this.textField.setText(this.formatValue(defaultValue));
        this.slider.setValue(defaultValue);
    }

    @Override
    public boolean isPendingModifiedFromDefault() {
        return Math.abs(this.getPendingValue().doubleValue() - this.getDefaultValue().doubleValue()) >= this.step;
    }

    @Override
    public boolean isPendingModifiedFromActual() {
        return Math.abs(this.getPendingValue().doubleValue() - this.getActualValue().doubleValue()) >= this.step;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int modifiers, LayoutEngine layout) {
        if (this.textField.mouseClicked(mouseX, mouseY, button, modifiers, layout)) {
            return true;
        }

        if (this.textField.isFocused()) {
            this.textField.setFocused(false);
        }

        return this.slider.mouseClicked(mouseX, mouseY, button, modifiers, layout);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY, LayoutEngine layout) {
        if (this.textField.isFocused()) {
            return false;
        }
        return this.slider.mouseDragged(mouseX, mouseY, button, dragX, dragY, layout);
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button, LayoutEngine layout) {
        this.slider.mouseReleased(mouseX, mouseY, button, layout);
    }

    @Override
    public boolean keyPressed(int keycode, int scancode, int modifiers, LayoutEngine layout) {
        if (this.textField.keyPressed(keycode, scancode, modifiers, layout)) {
            return true;
        }
        return this.slider.keyPressed(keycode, scancode, modifiers, layout);
    }

    @Override
    public boolean charTyped(int codepoint, int modifiers, LayoutEngine layout) {
        return this.textField.charTyped(codepoint, modifiers, layout);
    }

    @Override
    public void focusChanged(boolean focus, LayoutEngine layout) {
        if (!focus) {
            this.textField.setFocused(false);
        }
    }

    private String formatValue(double value) {
        return step >= 1.0 ? String.valueOf((int) value) : String.format("%.2f", value);
    }
}
