package com.pug523.shelf.gui.widget.option;

import java.util.Locale;
import java.util.function.IntUnaryOperator;

//#if MC >= 12111
import com.mojang.blaze3d.platform.cursor.CursorTypes;
//#endif
import com.pug523.shelf.compat.ComponentCompat;
import com.pug523.shelf.compat.GuiCompat;
import com.pug523.shelf.compat.ScreenCompat;
import com.pug523.shelf.config.Option;
import com.pug523.shelf.gui.ConfigScreen;
import com.pug523.shelf.gui.controller.OverlayController;
import com.pug523.shelf.gui.layout.Bounds;
import com.pug523.shelf.gui.layout.LayoutConfig;
import com.pug523.shelf.gui.layout.LayoutEngine;
import com.pug523.shelf.gui.widget.overlay.ColorPickerOverlay;

import com.pug523.shelf.gui.widget.TextInputFieldWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ColorPickerOptionWidget extends OptionWidget<Integer> {
    private Bounds cachedSquareBounds;
    private boolean textUseColor = false;

    private final TextInputFieldWidget<String> textField;
    private LayoutConfig cachedLayoutConfig = null;

    private static final Component HASH_COMPONENT = ComponentCompat.literal("#");
    private static int hashWidth = -1;

    public ColorPickerOptionWidget(Option<Integer> option) {
        super(option);

        int maxLen = maxLength();
        this.textField = new TextInputFieldWidget<>(
            false,
            text -> text.matches("^[0-9a-fA-F]{0," + maxLen + "}$"),
            text -> text.matches("^[0-9a-fA-F]{" + maxLen + "}$"),
            text -> {
                if (text.matches("^[0-9a-fA-F]{" + maxLen + "}$")) {
                    try {
                        long parsed = Long.parseLong(text, 16);
                        this.setPendingValue((int) parsed);
                    } catch (NumberFormatException ignored) {
                    }
                }
            },
            this::formatText,
            String.format(Locale.ROOT, "%08X", this.getPendingValue())
        );
        this.textField.setMaxLength(maxLen);
    }

    private int maxLength() {
        final int DEFAULT_MAX_LENGTH = 8;
        return cachedLayoutConfig != null ? cachedLayoutConfig.colorPickerMaxLength : DEFAULT_MAX_LENGTH;
    }

    private Component formatText(String rawText) {
        int maxLen = maxLength();

        if (rawText == null || cachedLayoutConfig == null || !rawText.matches("^[0-9a-fA-F]{" + maxLen + "}$") || !this.textUseColor) {
            return ComponentCompat.literal(rawText != null ? rawText : "");
        }

        try {
            int a = Integer.parseInt(rawText.substring(0, 2), 16);
            int r = Integer.parseInt(rawText.substring(2, 4), 16);
            int g = Integer.parseInt(rawText.substring(4, 6), 16);
            int b = Integer.parseInt(rawText.substring(6, 8), 16);

            int clampedA = Math.max(a, cachedLayoutConfig.colorPickerMinAlphaClamp);
            float alphaFactor = clampedA / 255.0f;

            IntUnaryOperator clampRGB = val -> Math.max(cachedLayoutConfig.colorPickerMinRGBClamp, Math.min(val, cachedLayoutConfig.colorPickerMaxRGBClamp));

            int baseR = clampRGB.applyAsInt(r);
            int baseG = clampRGB.applyAsInt(g);
            int baseB = clampRGB.applyAsInt(b);
            int baseA = clampRGB.applyAsInt(a);

            int colorA = (0xFF << 24) | ((int) (baseA * alphaFactor) << 16) | ((int) (baseA * alphaFactor) << 8) | (int) (baseA * alphaFactor);
            int colorR = (0xFF << 24) | ((int) (baseR * alphaFactor) << 16);
            int colorG = (0xFF << 24) | ((int) (baseG * alphaFactor) << 8);
            int colorB = (0xFF << 24) | (int) (baseB * alphaFactor);

            MutableComponent result = ComponentCompat.empty();
            result.append(ComponentCompat.literal(rawText.substring(0, 2)).withColor(colorA));
            result.append(ComponentCompat.literal(rawText.substring(2, 4)).withColor(colorR));
            result.append(ComponentCompat.literal(rawText.substring(4, 6)).withColor(colorG));
            result.append(ComponentCompat.literal(rawText.substring(6, 8)).withColor(colorB));
            return result;
        } catch (Exception e) {
            return ComponentCompat.literal(rawText);
        }
    }

    @Override
    public void render(Font font, GuiCompat gui, LayoutEngine layout, int x, int y, int width, int height, int mouseX, int mouseY) {
        int argb = this.getPendingValue();
        LayoutConfig cfg = layout.getConfig();
        this.cachedLayoutConfig = cfg;

        int squareSize = cfg.colorPickerSquareSize;
        int textWidth = cfg.colorPickerTextWidth;

        int textX = x + width - layout.optionWidgetRightMargin - textWidth;
        int textY = y + (height - font.lineHeight) / 2 + cfg.colorPickerTextOffsetY;
        int squareX = textX - squareSize - cfg.colorPickerSquareRightPadding;
        int squareY = y + (height - squareSize) / 2;

        this.cachedSquareBounds = new Bounds(squareX, squareY, squareSize, squareSize);
        this.textUseColor = cfg.colorPickerTextUseColor;

        boolean isSquareHovered = cachedSquareBounds.contains(mouseX, mouseY);
        int borderColor;
        if (isSquareHovered) {
            borderColor = cfg.colorColorPickerBorderHover;
            //#if MC >= 12111
            gui.requestCursor(CursorTypes.POINTING_HAND);
            //#endif
        } else {
            borderColor = cfg.colorColorPickerBorderDefault;
        }

        gui.fill(squareX, squareY, squareX + squareSize, squareY + squareSize, borderColor);
        gui.fill(squareX + 1, squareY + 1, squareX + squareSize - 1, squareY + squareSize - 1, argb);

        if (hashWidth == -1) {
            hashWidth = ComponentCompat.width(font, HASH_COMPONENT);
        }
        gui.text(font, HASH_COMPONENT, textX, textY, cfg.colorColorPickerHashText);

        if (!this.textField.isFocused()) {
            String hexString = String.format(Locale.ROOT, "%08X", argb);
            this.textField.setText(hexString);
        }

        this.textField.render(font, gui, layout, textX + hashWidth, textY, textWidth - hashWidth, font.lineHeight, mouseX, mouseY);
    }

    @Override
    public void resetPendingToDefault() {
        super.resetPendingToDefault();
        this.textField.setText(String.format(Locale.ROOT, "%08X", this.getPendingValue()));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int modifiers, LayoutEngine layout) {
        if (this.textField.mouseClicked(mouseX, mouseY, button, modifiers, layout)) {
            return true;
        }
        Screen screen = ScreenCompat.getScreen(Minecraft.getInstance());
        if (screen instanceof ConfigScreen && cachedSquareBounds != null && cachedSquareBounds.contains(mouseX, mouseY)) {
            OverlayController overlayController = ((ConfigScreen) screen).getOverlayController();
            overlayController.clear();
            ColorPickerOverlay pickerOverlay = new ColorPickerOverlay(this.option, (finalColor, overlay) -> overlayController.pop());
            overlayController.push(pickerOverlay);
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keycode, int scancode, int modifiers, LayoutEngine layout) {
        return this.textField.keyPressed(keycode, scancode, modifiers, layout);
    }

    @Override
    public boolean charTyped(int codepoint, int modifiers, LayoutEngine layout) {
        return this.textField.charTyped(codepoint, modifiers, layout);
    }

    @Override
    public void focusChanged(boolean focus, LayoutEngine layout) {
        this.textField.focusChanged(focus, layout);
    }
}
