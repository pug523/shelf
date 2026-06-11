package com.pug523.shelf.gui.widget;

import java.util.function.Function;

import com.pug523.shelf.compat.ComponentCompat;
import com.pug523.shelf.compat.GuiCompat;
import com.pug523.shelf.config.Option;
import com.pug523.shelf.gui.input.InputUtil;
import com.pug523.shelf.gui.layout.LayoutConfig;
import com.pug523.shelf.gui.layout.LayoutEngine;
import com.pug523.shelf.gui.renderer.RenderUtil;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class SliderOptionWidget<N extends Number & Comparable<N>> extends OptionWidget<N> {
    private final double min;
    private final double max;
    private final double step;
    private final boolean round;
    private final Function<Double, N> typeConverter;

    private int cachedX, cachedY, cachedWidth, cachedHeight;
    private LayoutConfig cachedConfig;
    private boolean isDragging = false;

    public SliderOptionWidget(Option<N> option, N min, N max, N step, boolean round,
            Function<Double, N> typeConverter) {
        super(option);
        this.min = min.doubleValue();
        this.max = max.doubleValue();
        this.step = step.doubleValue();
        this.round = round;
        this.typeConverter = typeConverter;
    }

    public static SliderOptionWidget<Integer> ofInt(Option<Integer> option, int min, int max, int step, boolean round) {
        return new SliderOptionWidget<Integer>(option, min, max, step, round, d -> (int) Math.round(d));
    }

    public static SliderOptionWidget<Double> ofDouble(Option<Double> option, double min, double max, double step,
            boolean round) {
        return new SliderOptionWidget<Double>(option, min, max, step, round, d -> d);
    }

    public static SliderOptionWidget<Float> ofFloat(Option<Float> option, float min, float max, float step,
            boolean round) {
        return new SliderOptionWidget<Float>(option, min, max, step, round, d -> (float) d.floatValue());
    }

    @Override
    public void render(Font font, GuiCompat gui, LayoutEngine layout, int x, int y, int width, int height, int mouseX,
            int mouseY) {
        this.cachedX = x;
        this.cachedY = y;
        this.cachedWidth = width;
        this.cachedHeight = height;
        this.cachedConfig = layout.getConfig();

        LayoutConfig cfg = layout.getConfig();

        int sliderX = x + width - cfg.sliderWidth - cfg.sliderPaddingX;
        int sliderY = y + (height - cfg.sliderHeight) / 2;

        double currentValue = option.getPendingValue().doubleValue();
        double progress = Mth.clamp((currentValue - min) / (max - min), 0.0, 1.0);

        // Track
        gui.fill(sliderX, sliderY, sliderX + cfg.sliderWidth, sliderY + cfg.sliderHeight, cfg.colorSliderTrack);

        // Progress
        int fillEnd = sliderX + (int) (cfg.sliderWidth * progress);
        gui.fill(sliderX, sliderY, fillEnd, sliderY + cfg.sliderHeight, cfg.colorSliderProgress);

        if (round) {
            int centerX = fillEnd;
            int centerY = sliderY + (cfg.sliderHeight / 2);
            int radius = cfg.sliderKnobSize / 2;
            RenderUtil.drawDynamicCircle(gui, centerX, centerY, radius, cfg.colorSliderKnob);
        } else {
            int knobX = fillEnd - (cfg.sliderKnobSize / 2);
            int knobY = sliderY + (cfg.sliderHeight / 2) - (cfg.sliderKnobSize / 2);
            gui.fill(knobX, knobY, knobX + cfg.sliderKnobSize, knobY + cfg.sliderKnobSize, cfg.colorSliderKnob);
        }

        // Text
        String valueText = formatValue(currentValue);
        Component textComponent = ComponentCompat.literal(valueText);
        int textWidth = font.width(textComponent);
        int textX = sliderX - textWidth - cfg.sliderTextPadding;
        int textY = y + (height - font.lineHeight) / 2 + 1;

        gui.text(font, textComponent, textX, textY, cfg.colorSliderText, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == InputUtil.LEFT_MOUSE_BUTTON && cachedConfig != null) {
            int sliderX = cachedX + cachedWidth - cachedConfig.sliderWidth - cachedConfig.sliderPaddingX;

            if (mouseX >= sliderX && mouseX <= sliderX + cachedConfig.sliderWidth && mouseY >= cachedY
                    && mouseY <= cachedY + cachedHeight) {
                updateValueFromMouse(mouseX, sliderX, cachedConfig.sliderWidth);
                this.isDragging = true;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && this.isDragging && cachedConfig != null) {
            int sliderX = cachedX + cachedWidth - cachedConfig.sliderWidth - cachedConfig.sliderPaddingX;
            updateValueFromMouse(mouseX, sliderX, cachedConfig.sliderWidth);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.isDragging = false;
        return true;
    }

    private void updateValueFromMouse(double mouseX, int sliderX, int sliderWidth) {
        double pct = (mouseX - sliderX) / (double) sliderWidth;
        pct = Mth.clamp(pct, 0.0, 1.0);

        double rawValue = min + (max - min) * pct;

        if (step > 0.0) {
            rawValue = Math.round(rawValue / step) * step;
        }

        rawValue = Mth.clamp(rawValue, min, max);

        N finalValue = typeConverter.apply(rawValue);
        option.setPendingValue(finalValue);
    }

    private String formatValue(double value) {
        if (step >= 1.0) {
            return String.valueOf((int) value);
        } else {
            return String.format("%.2f", value);
        }
    }
}
