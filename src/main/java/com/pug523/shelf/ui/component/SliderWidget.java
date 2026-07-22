package com.pug523.shelf.ui.component;

import com.mojang.blaze3d.platform.InputConstants;
import com.pug523.shelf.common.compat.GuiCompat;
import com.pug523.shelf.common.compat.JavaCompat;
import com.pug523.shelf.core.geometry.MousePos;
import com.pug523.shelf.core.geometry.Rect;
import com.pug523.shelf.ui.Element;
import com.pug523.shelf.ui.Widget;
import com.pug523.shelf.ui.component.vm.SliderViewModel;
import net.minecraft.client.gui.Font;

public class SliderWidget implements Widget {
    private final SliderViewModel viewModel;
    private final Element track;
    private final Element progressBar;
    private final Element knob;

    private final Config config;

    public static class Config {
        public enum Orientation {
            HORIZONTAL,
            VERTICAL,
        }

        public Orientation orientation = Orientation.HORIZONTAL;

        public double barThicknessPercent = 0.65;
        public double knobSizePercent = 1.0;
    }

    private boolean isDragging = false;
    private boolean isFocused = false;

    public SliderWidget(SliderViewModel viewModel, Element track, Element progressBar, Element knob, Config config) {
        this.viewModel = viewModel;
        this.track = track;
        this.progressBar = progressBar;
        this.knob = knob;
        this.config = config;
    }

    @Override
    public void render(Font font, GuiCompat gui, Rect rect, MousePos mousePos) {
        double progress = viewModel.getProgress();

        int min = Math.min(rect.width, rect.height);
        int barThickness = (int) (min * config.barThicknessPercent);
        int knobSize = (int) (min * config.knobSizePercent);
        if (config.orientation == Config.Orientation.HORIZONTAL) {
            int sliderY = rect.y + (rect.height - barThickness) / 2 + 1;
            int progressLength = (int) (rect.width * progress);
            int progressEnd = rect.x + progressLength;

            Rect trackRect = new Rect(rect.x, sliderY, rect.width, barThickness);
            track.render(font, gui, trackRect, mousePos);
            Rect progressRect = new Rect(rect.x, sliderY, progressEnd, barThickness);
            progressBar.render(font, gui, progressRect, mousePos);
            int knobX = progressEnd - (knobSize / 2);
            int knobY = sliderY + (barThickness / 2) - (knobSize / 2);
            Rect knobRect = new Rect(knobX, knobY, knobSize, knobSize);
            knob.render(font, gui, knobRect, mousePos);
        } else {
            int sliderX = rect.x + (rect.width - barThickness) / 2;
            int progressLength = (int) (rect.height * progress);
            int progressStart = rect.y + rect.height - progressLength;

            Rect trackRect = new Rect(sliderX, rect.y, barThickness, rect.height);
            track.render(font, gui, trackRect, mousePos);
            Rect progressRect = new Rect(sliderX, rect.y, barThickness, progressLength);
            progressBar.render(font, gui, progressRect, mousePos);
            int knobX = sliderX + (barThickness / 2) - (knobSize / 2);
            int knobY = progressStart - (knobSize / 2);
            Rect knobRect = new Rect(knobX, knobY, knobSize, knobSize);
            knob.render(font, gui, knobRect, mousePos);
        }
    }

    @Override
    public boolean mouseClicked(Rect rect, MousePos mousePos, int button, int modifiers) {
        if (button == InputConstants.MOUSE_BUTTON_LEFT && mousePos.isHovering(rect)) {
            updateValueFromMouse(rect, mousePos);
            isDragging = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(Rect rect, MousePos mousePos, int button, double dragX, double dragY) {
        if (button == InputConstants.MOUSE_BUTTON_LEFT && isDragging) {
            updateValueFromMouse(rect, mousePos);
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(Rect rect, MousePos mousePos, int button) {
        isDragging = false;
    }

    @Override
    public void focusChanged(Rect rect, MousePos mousePos, boolean focus) {
        this.isFocused = focus;
    }

    @Override
    public boolean keyPressed(Rect rect, MousePos mousePos, int keycode, int scancode, int modifiers) {
        if (!isFocused && !mousePos.isHovering(rect)) {
            return false;
        }

        double direction = 0.0;

        if (config.orientation == Config.Orientation.HORIZONTAL) {
            if (keycode == InputConstants.KEY_LEFT) direction = -1.0;
            else if (keycode == InputConstants.KEY_RIGHT) direction = 1.0;
        } else {
            if (keycode == InputConstants.KEY_DOWN) direction = -1.0;
            else if (keycode == InputConstants.KEY_UP) direction = 1.0;
        }

        if (direction != 0.0) {
            viewModel.step(direction);
            return true;
        }

        return false;
    }

    private void updateValueFromMouse(Rect rect, MousePos mousePos) {
        double pct;
        if (config.orientation == Config.Orientation.HORIZONTAL) {
            pct = JavaCompat.clamp((mousePos.x - rect.x) / (double) rect.width, 0.0, 1.0);
        } else {
            pct = JavaCompat.clamp(1.0 - ((mousePos.y - rect.y) / (double) rect.height), 0.0, 1.0);
        }

        viewModel.setProgress(pct);
    }
}
