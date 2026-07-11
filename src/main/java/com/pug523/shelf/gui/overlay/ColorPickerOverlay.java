package com.pug523.shelf.gui.overlay;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import com.pug523.shelf.compat.ComponentCompat;
import com.pug523.shelf.compat.GuiCompat;
import com.pug523.shelf.config.Option;
import com.pug523.shelf.gui.Colors;
import com.pug523.shelf.gui.layout.LayoutConfig;
import com.pug523.shelf.gui.layout.LayoutEngine;
import com.pug523.shelf.gui.layout.Bounds;
import com.pug523.shelf.gui.renderer.RenderUtil;
import com.pug523.shelf.gui.sound.SoundUtil;
import com.pug523.shelf.gui.text.TextUtil;
import com.pug523.shelf.gui.renderer.state.ColorGradientRenderState;

import com.pug523.shelf.gui.widget.ActionButtonWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class ColorPickerOverlay implements ScreenOverlay {
    private static final int COLOR_BG_OUTLINE = 0xAF11131E;
    private static final int COLOR_BG_INNER = 0xAF161923;
    private static final int COLOR_TEXT_MUTED = 0xFF6B7280;
    private static final int COLOR_TEXT_LABEL = 0xFF9CA3AF;
    private static final int COLOR_BTN_OK_BG = 0xFF2563EB;

    // TODO: i18n
    public static final Component TITLE_TEXT = ComponentCompat.literal("Select Color");
    public static final Component LABEL_HEX = ComponentCompat.literal("HEX");
    public static final Component LABEL_NEW = ComponentCompat.literal("New");
    public static final Component LABEL_CURRENT = ComponentCompat.literal("Current");
    public static final Component PRESET_TEXT = ComponentCompat.literal("Presets");
    public static final Component RECENT_TEXT = ComponentCompat.literal("Recent Colors");
    public static final Component BTN_CANCEL = ComponentCompat.literal("Cancel");
    public static final Component BTN_OK = ComponentCompat.literal("OK");
    public static final Component BTN_CLEAR = ComponentCompat.literal("Clear");
    public static final Component BTN_X = ComponentCompat.literal("✕").withStyle(ChatFormatting.RED, ChatFormatting.BOLD);
    public static final Component BTN_TOGGLE_HSV = ComponentCompat.literal("Mode: HSV");
    public static final Component BTN_TOGGLE_RGB = ComponentCompat.literal("Mode: RGB");

    private final Option<Integer> targetOption;
    private final Consumer<Integer> onConfirm;
    private final int originalColor;
    private LayoutConfig cachedConfig = null;

    private final ActionButtonWidget cancelButton = new ActionButtonWidget(BTN_CANCEL, (btn) -> cancel());
    private final ActionButtonWidget okButton = new ActionButtonWidget(BTN_OK, (btn) -> ok());
    private final ActionButtonWidget clearButton = new ActionButtonWidget(BTN_CLEAR, (btn) -> clear());
    private final ActionButtonWidget xButton = new ActionButtonWidget(BTN_X, (btn) -> x());
    private final ActionButtonWidget toggleModeButton = new ActionButtonWidget(BTN_TOGGLE_HSV, this::toggleMode);

    // TODO: load from shelf config
    private final List<Integer> presetColors = new ArrayList<>();
    private static final List<Integer> recentColors = new ArrayList<>();

    private float alpha = 1.0f;

    private int red = 255;
    private int green = 0;
    private int blue = 0;

    private float hue = 0.0f;
    private float saturation = 1.0f;
    private float brightness = 1.0f;

    private enum PickerMode {HSV, RGB}

    private PickerMode currentMode = PickerMode.HSV;

    private boolean isDraggingSBSpace = false;
    private boolean isDraggingHueSlider = false;
    private boolean isDraggingAlphaSlider = false;
    private boolean isDraggingRSlider = false;
    private boolean isDraggingGSlider = false;
    private boolean isDraggingBSlider = false;

    public ColorPickerOverlay(Option<Integer> targetOption, Consumer<Integer> onConfirm) {
        this.targetOption = targetOption;
        this.onConfirm = onConfirm;
        this.originalColor = targetOption.getPendingValue();

        initPresets();
        initColorFromRgb(originalColor);
    }

    private void initPresets() {
        int[] defaultPresets = {0xFF2563EB, 0xFF7C3AED, 0xFF8B5CF6, 0xFFEC4899, 0xFFEF4444, 0xFFF59E0B, 0xFFFBBF24,
            0xFF22C55E, 0xFF10B981, 0xFF60A5FA, 0xFFE5E7EB};
        for (int color : defaultPresets) {
            presetColors.add(color);
        }
    }

    private void initColorFromRgb(int argb) {
        this.red = (argb >> 16) & 0xFF;
        this.green = (argb >> 8) & 0xFF;
        this.blue = argb & 0xFF;
        this.alpha = ((argb >> 24) & 0xFF) / 255.0f;

        float[] hsb = Color.RGBtoHSB(red, green, blue, null);
        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];
    }

    private void updateFromHsb() {
        int rgb = Color.HSBtoRGB(hue, saturation, brightness);
        this.red = (rgb >> 16) & 0xFF;
        this.green = (rgb >> 8) & 0xFF;
        this.blue = rgb & 0xFF;
    }

    private void updateFromRgb() {
        float[] hsb = Color.RGBtoHSB(red, green, blue, null);
        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];
    }

    private int getCurrentColor() {
        int aInt = (int) (alpha * 255.0f) & 0xFF;
        return (aInt << 24) | (red << 16) | (green << 8) | blue;
    }

    @Override
    public void render(Font font, GuiCompat gui, int mouseX, int mouseY, float partialTicks, LayoutEngine layout) {
        updateDragStates(mouseX, mouseY, layout);

        LayoutConfig cfg = layout.getConfig();
        cachedConfig = cfg;
        int currentArgb = getCurrentColor();

        Bounds bound = layout.pickerDialog;
        gui.enableScissor(bound.x, bound.y, bound.maxX, bound.maxY);

        renderDialogFrame(font, gui, layout, cfg);
        if (currentMode == PickerMode.HSV) {
            renderSbSpace(gui, layout.pickerSbSpace, cfg);
            renderHueSlider(gui, layout.pickerHueSlider, cfg);
        } else {
            renderRgbSliders(gui, layout, cfg);
        }

        renderAlphaSlider(gui, layout.pickerAlphaSlider, cfg);

        renderColorPreviews(font, gui, layout, currentArgb);
        renderColorMetrics(font, gui, layout, cfg, currentArgb);
        renderColorPalettes(font, gui, layout, cfg);

        Bounds cancelB = layout.pickerCancelButton;
        cancelButton.render(font, gui, layout, cancelB.x, cancelB.y, cancelB.width, cancelB.height, mouseX, mouseY);

        Bounds okB = layout.pickerOkButton;
        okButton.renderWithBackground(font, gui, layout, okB.x, okB.y, okB.width, okB.height, mouseX, mouseY, COLOR_BTN_OK_BG);

        Bounds clearB = layout.pickerClearButton;
        clearButton.render(font, gui, layout, clearB.x, clearB.y, clearB.width, clearB.height, mouseX, mouseY);

        Bounds xB = layout.pickerCloseButton;
        xButton.render(font, gui, layout, xB.x, xB.y, xB.width, xB.height, mouseX, mouseY);

        Bounds b = layout.pickerModeToggleButton;
        toggleModeButton.render(font, gui, layout, b.x, b.y, b.width, b.height, mouseX, mouseY);

        gui.disableScissor();
    }

    private void renderDialogFrame(Font font, GuiCompat gui, LayoutEngine layout, LayoutConfig config) {
        Bounds bound = layout.pickerDialog;
        gui.fill(bound.x, bound.y, bound.maxX, bound.maxY, COLOR_BG_OUTLINE);
        gui.fill(bound.x + 1, bound.y + 1, bound.maxX - 1, bound.maxY - 1, COLOR_BG_INNER);

        int titleY = bound.y + config.pickerPaddingInner;
        gui.text(font, TITLE_TEXT, bound.x + config.pickerPaddingInner, titleY, Colors.WHITE, false);
    }

    private void renderSbSpace(GuiCompat gui, Bounds sb, LayoutConfig cfg) {
        int baseHueRgb = Color.HSBtoRGB(hue, 1.0f, 1.0f) | 0xFF000000;
        //#if MC >= 12106
        gui.getGraphics().guiRenderState.addGuiElement(new ColorGradientRenderState(gui, sb.x, sb.maxX, sb.y, sb.maxY,
            Colors.WHITE, Colors.BLACK, baseHueRgb, Colors.BLACK));
        //#endif

        int hX = sb.x + (int) (this.saturation * sb.width);
        int hY = sb.y + (int) ((1.0f - this.brightness) * sb.height);
        int d = cfg.pickerSbSpaceIndicatorSize;
        final int borderThickness = 1;
        RenderUtil.renderOutline(gui, hX - d, hY - d, d * 2, d * 2, borderThickness, 0xFFFFFFFF);
        RenderUtil.renderInner(gui, hX - d, hY - d, d * 2, d * 2, borderThickness, (0xFF << 24) | (red << 16) | (green << 8) | blue);
    }

    private void renderHueSlider(GuiCompat gui, Bounds hueB, LayoutConfig cfg) {
        for (int i = 0; i < hueB.height; i++) {
            gui.fill(hueB.x, hueB.y + i, hueB.maxX, hueB.y + i + 1,
                Color.HSBtoRGB(i / (float) hueB.height, 1.0f, 1.0f));
        }
        int hThumbY = hueB.y + (int) (this.hue * hueB.height);
        int d = cfg.pickerSliderIndicatorSize;
        gui.fill(hueB.x - d, hThumbY - d, hueB.maxX + d, hThumbY + d, Colors.WHITE);
    }


    private void renderRgbSliders(GuiCompat gui, LayoutEngine layout, LayoutConfig cfg) {
        Bounds rB = layout.pickerRSlider;
        for (int i = 0; i < rB.width; i++) {
            int rStep = (int) ((i / (float) rB.width) * 255.0f);
            gui.fill(rB.x + i, rB.y, rB.x + i + 1, rB.maxY, 0xFF000000 | (rStep << 16) | (green << 8) | blue);
        }
        int rThumbX = rB.x + (int) ((this.red / 255.0f) * rB.width);
        final int thumbW = (int) (rB.width * 0.04f);
        final int overflowY = (int) (rB.height * 0.175f);
        gui.fill(rThumbX - (thumbW / 2), rB.y - overflowY, rThumbX + (thumbW / 2) + 1, rB.maxY + overflowY, Colors.WHITE);

        Bounds gB = layout.pickerGSlider;
        for (int i = 0; i < gB.width; i++) {
            int gStep = (int) ((i / (float) gB.width) * 255.0f);
            gui.fill(gB.x + i, gB.y, gB.x + i + 1, gB.maxY, 0xFF000000 | (red << 16) | (gStep << 8) | blue);
        }
        int gThumbX = gB.x + (int) ((this.green / 255.0f) * gB.width);
        gui.fill(gThumbX - (thumbW / 2), gB.y - overflowY, gThumbX + (thumbW / 2) + 1, gB.maxY + overflowY, Colors.WHITE);

        Bounds bB = layout.pickerBSlider;
        for (int i = 0; i < bB.width; i++) {
            int bStep = (int) ((i / (float) bB.width) * 255.0f);
            gui.fill(bB.x + i, bB.y, bB.x + i + 1, bB.maxY, 0xFF000000 | (red << 16) | (green << 8) | bStep);
        }
        int bThumbX = bB.x + (int) ((this.blue / 255.0f) * bB.width);
        gui.fill(bThumbX - (thumbW / 2), bB.y - overflowY, bThumbX + (thumbW / 2) + 1, bB.maxY + overflowY, Colors.WHITE);

    }

    private void renderAlphaSlider(GuiCompat gui, Bounds alphaB, LayoutConfig cfg) {
        int cellSize = (int) (cfg.pickerSliderWidth / 3.0f);
        for (int y = alphaB.y; y < alphaB.maxY; y += cellSize) {
            int h = Math.min(cellSize, alphaB.maxY - y);
            for (int x = alphaB.x; x < alphaB.maxX; x += cellSize) {
                int w = Math.min(cellSize, alphaB.maxX - x);
                boolean isEven = ((x - alphaB.x) / cellSize + (y - alphaB.y) / cellSize) % 2 == 0;
                int gridColor = isEven ? 0xFFFFFFFF : 0xFFD0D0D0;
                gui.fill(x, y, x + w, y + h, gridColor);
            }
        }

        int colorTop = (255 << 24) | (red << 16) | (green << 8) | blue;
        int colorBottom = (0 << 24) | (red << 16) | (green << 8) | blue;

        gui.fillGradient(alphaB.x, alphaB.y, alphaB.maxX, alphaB.maxY, colorTop, colorBottom);

        int aThumbY = alphaB.y + (int) ((1.0f - this.alpha) * alphaB.height);
        int d = cfg.pickerSliderIndicatorSize;
        gui.fill(alphaB.x - d, aThumbY - d, alphaB.maxX + d, aThumbY + d, Colors.WHITE);
    }

    private void renderColorPreviews(Font font, GuiCompat gui, LayoutEngine layout, int currentArgb) {
        Bounds nSwat = layout.pickerNewColorSwat;
        Bounds cSwat = layout.pickerCurrentColorSwat;
        int labelY = layout.pickerHueSlider.y;

        gui.text(font, LABEL_NEW, nSwat.x, labelY, COLOR_TEXT_LABEL, false);
        gui.fill(nSwat.x, nSwat.y, nSwat.maxX, nSwat.maxY, currentArgb);

        gui.text(font, LABEL_CURRENT, cSwat.x, labelY, COLOR_TEXT_LABEL, false);
        gui.fill(cSwat.x, cSwat.y, cSwat.maxX, cSwat.maxY, originalColor);
    }

    private void renderColorMetrics(Font font, GuiCompat gui, LayoutEngine layout, LayoutConfig cfg, int currentArgb) {
        Bounds baseBounds = layout.pickerNewColorSwat;
        int metricsY = baseBounds.maxY + cfg.pickerMetricsOffsetY;

        gui.text(font, LABEL_HEX, baseBounds.x, metricsY, COLOR_TEXT_MUTED, false);
        int hexLabelWidth = TextUtil.width(font, LABEL_HEX) + 6;
        String hexText = String.format(Locale.ROOT, "#%08X", currentArgb);
        gui.text(font, ComponentCompat.literal(hexText), baseBounds.x + hexLabelWidth, metricsY, Colors.WHITE, false);

        int r = (currentArgb >> 16) & 0xFF;
        int g = (currentArgb >> 8) & 0xFF;
        int b = currentArgb & 0xFF;
        int a = (currentArgb >> 24) & 0xFF;

        String[] rgbLines = {
            String.format(Locale.ROOT, "A: %d", a),
            String.format(Locale.ROOT, "R: %d", r),
            String.format(Locale.ROOT, "G: %d", g),
            String.format(Locale.ROOT, "B: %d", b)
        };

        int startY = metricsY + cfg.pickerMetricsSpacingY;
        for (int i = 0; i < rgbLines.length; i++) {
            int lineY = startY + (i * cfg.pickerMetricsSpacingY);
            gui.text(font, ComponentCompat.literal(rgbLines[i]), baseBounds.x, lineY, COLOR_TEXT_LABEL, false);
        }

        String[] hsvLines = {
            String.format(Locale.ROOT, "H: %d°", (int) (hue * 360)),
            String.format(Locale.ROOT, "S: %d%%", (int) (saturation * 100)),
            String.format(Locale.ROOT, "V: %d%%", (int) (brightness * 100))
        };

        int rightColumnX = baseBounds.x + (baseBounds.width) + 4;

        for (int i = 0; i < hsvLines.length; i++) {
            int lineY = startY + (i * cfg.pickerMetricsSpacingY);
            gui.text(font, ComponentCompat.literal(hsvLines[i]), rightColumnX, lineY, COLOR_TEXT_LABEL, false);
        }
    }

    private void renderColorPalettes(Font font, GuiCompat gui, LayoutEngine layout, LayoutConfig config) {
        Bounds dialogBounds = layout.pickerDialog;

        if (!layout.pickerPresetBounds.isEmpty()) {
            Bounds firstPreset = layout.pickerPresetBounds.get(0);
            gui.text(font, PRESET_TEXT, dialogBounds.x + config.pickerPaddingInner,
                firstPreset.y - config.pickerPaletteTextSpacingY, COLOR_TEXT_MUTED, false);

            int limit = Math.min(presetColors.size(), layout.pickerPresetBounds.size());
            for (int i = 0; i < limit; i++) {
                Bounds b = layout.pickerPresetBounds.get(i);
                gui.fill(b.x, b.y, b.maxX, b.maxY, presetColors.get(i));
            }
        }

        if (!layout.pickerRecentBounds.isEmpty()) {
            Bounds firstRecent = layout.pickerRecentBounds.get(0);
            gui.text(font, RECENT_TEXT, dialogBounds.x + config.pickerPaddingInner,
                firstRecent.y - config.pickerPaletteTextSpacingY, COLOR_TEXT_MUTED, false);

            int limit = Math.min(recentColors.size(), layout.pickerRecentBounds.size());
            for (int i = 0; i < limit; i++) {
                Bounds b = layout.pickerRecentBounds.get(i);
                gui.fill(b.x, b.y, b.maxX, b.maxY, recentColors.get(i));
            }
        }
    }

    private void updateDragStates(int mouseX, int mouseY, LayoutEngine layout) {
        if (currentMode == PickerMode.HSV) {
            if (this.isDraggingSBSpace) {
                Bounds sb = layout.pickerSbSpace;
                if (sb.width > 0 && sb.height > 0) {
                    float pctX = Mth.clamp((mouseX - sb.x) / (float) sb.width, 0.0f, 1.0f);
                    float pctY = Mth.clamp(1.0f - ((mouseY - sb.y) / (float) sb.height), 0.0f, 1.0f);

                    this.saturation = pctX;
                    this.brightness = pctY;
                    updateFromHsb();
                    updatePendingValue();
                }
            }
            if (this.isDraggingHueSlider) {
                Bounds hueB = layout.pickerHueSlider;
                if (hueB.height > 0) {
                    float pctY = Mth.clamp((mouseY - hueB.y) / (float) hueB.height, 0.0f, 1.0f);
                    this.hue = pctY;
                    updateFromHsb();
                    updatePendingValue();
                }
            }
        } else {
            if (this.isDraggingRSlider) {
                Bounds rB = layout.pickerRSlider;
                if (rB.width > 0) {
                    this.red = (int) (Mth.clamp((mouseX - rB.x) / (float) rB.width, 0.0f, 1.0f) * 255.0f);
                    updateFromRgb();
                    updatePendingValue();
                }
            }
            if (this.isDraggingGSlider) {
                Bounds gB = layout.pickerGSlider;
                if (gB.width > 0) {
                    this.green = (int) (Mth.clamp((mouseX - gB.x) / (float) gB.width, 0.0f, 1.0f) * 255.0f);
                    updateFromRgb();
                    updatePendingValue();
                }
            }
            if (this.isDraggingBSlider) {
                Bounds bB = layout.pickerBSlider;
                if (bB.width > 0) {
                    this.blue = (int) (Mth.clamp((mouseX - bB.x) / (float) bB.width, 0.0f, 1.0f) * 255.0f);
                    updateFromRgb();
                    updatePendingValue();
                }
            }
        }
        if (this.isDraggingAlphaSlider) {
            Bounds alphaB = layout.pickerAlphaSlider;
            if (alphaB.height > 0) {
                this.alpha = Mth.clamp(1.0f - ((mouseY - alphaB.y) / (float) alphaB.height), 0.0f, 1.0f);
                updatePendingValue();
            }
        }
    }

    private void updatePendingValue() {
        this.targetOption.setPendingValue(getCurrentColor());
    }

    private void applyColor(int rgb) {
        initColorFromRgb(rgb);
        updatePendingValue();
        SoundUtil.clickSound();
    }

    private void cancel() {
        this.targetOption.setPendingValue(originalColor);
        this.onConfirm.accept(originalColor);
        SoundUtil.clickSound();
    }

    private void ok() {
        if (cachedConfig != null) {
            int finalColor = getCurrentColor();
            recentColors.remove((Integer) finalColor);
            recentColors.add(0, finalColor);

            if (recentColors.size() > cachedConfig.pickerMaxPaletteColors) {
                recentColors.remove(recentColors.size() - 1);
            }
            this.onConfirm.accept(finalColor);
            SoundUtil.clickSound();
        }
    }

    private void clear() {
        if (!recentColors.isEmpty()) {
            recentColors.clear();
            SoundUtil.clickSound();
        }
    }

    private void x() {
        cancel();
    }

    private void toggleMode(ActionButtonWidget btn) {
        this.currentMode = this.currentMode == PickerMode.HSV ? PickerMode.RGB : PickerMode.HSV;
        btn.setLabel(this.currentMode == PickerMode.HSV ? BTN_TOGGLE_HSV : BTN_TOGGLE_RGB);
        SoundUtil.clickSound();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, LayoutEngine layout) {
        if (cancelButton.mouseClicked(mouseX, mouseY, button, 0, layout) ||
            okButton.mouseClicked(mouseX, mouseY, button, 0, layout) ||
            clearButton.mouseClicked(mouseX, mouseY, button, 0, layout) ||
            xButton.mouseClicked(mouseX, mouseY, button, 0, layout) ||
            toggleModeButton.mouseClicked(mouseX, mouseY, button, 0, layout)) {
            return true;
        }

        if (currentMode == PickerMode.HSV) {
            if (layout.pickerSbSpace.contains(mouseX, mouseY)) {
                this.isDraggingSBSpace = true;
                SoundUtil.clickSound();
                return true;
            }
            if (layout.pickerHueSlider.contains(mouseX, mouseY)) {
                this.isDraggingHueSlider = true;
                SoundUtil.clickSound();
                return true;
            }
        } else {
            if (layout.pickerRSlider.contains(mouseX, mouseY)) {
                this.isDraggingRSlider = true;
                SoundUtil.clickSound();
                return true;
            }
            if (layout.pickerGSlider.contains(mouseX, mouseY)) {
                this.isDraggingGSlider = true;
                SoundUtil.clickSound();
                return true;
            }
            if (layout.pickerBSlider.contains(mouseX, mouseY)) {
                this.isDraggingBSlider = true;
                SoundUtil.clickSound();
                return true;
            }
        }

        if (layout.pickerAlphaSlider.contains(mouseX, mouseY)) {
            this.isDraggingAlphaSlider = true;
            SoundUtil.clickSound();
            return true;
        }
        int presetLimit = Math.min(presetColors.size(), layout.pickerPresetBounds.size());
        for (int i = 0; i < presetLimit; i++) {
            if (layout.pickerPresetBounds.get(i).contains(mouseX, mouseY)) {
                applyColor(presetColors.get(i));
                return true;
            }
        }

        int recentLimit = Math.min(recentColors.size(), layout.pickerRecentBounds.size());
        for (int i = 0; i < recentLimit; i++) {
            if (layout.pickerRecentBounds.get(i).contains(mouseX, mouseY)) {
                applyColor(recentColors.get(i));
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button, LayoutEngine layout) {
        this.isDraggingSBSpace = false;
        this.isDraggingHueSlider = false;
        this.isDraggingAlphaSlider = false;
        this.isDraggingRSlider = false;
        this.isDraggingGSlider = false;
        this.isDraggingBSlider = false;
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers, LayoutEngine layout) {
        return false;
    }

    @Override
    public boolean charTyped(int codePoint, int modifiers, LayoutEngine layout) {
        return false;
    }
}
