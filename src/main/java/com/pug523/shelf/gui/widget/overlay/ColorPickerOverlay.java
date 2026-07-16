package com.pug523.shelf.gui.widget.overlay;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

import com.pug523.shelf.compat.ComponentCompat;
import com.pug523.shelf.compat.GuiCompat;
import com.pug523.shelf.config.Option;
import com.pug523.shelf.gui.Colors;
import com.pug523.shelf.gui.layout.LayoutConfig;
import com.pug523.shelf.gui.layout.LayoutEngine;
import com.pug523.shelf.gui.layout.Bounds;
import com.pug523.shelf.gui.renderer.RenderUtil;
import com.pug523.shelf.gui.sound.SoundUtil;
import com.pug523.shelf.gui.renderer.state.ColorGradientRenderState;

import com.pug523.shelf.gui.widget.ActionButtonWidget;
import com.pug523.shelf.gui.widget.SliderWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class ColorPickerOverlay extends WindowOverlay {
    private static final int COLOR_TEXT_MUTED = 0xFF6B7280;
    private static final int COLOR_TEXT_LABEL = 0xFF9CA3AF;

    // TODO: i18n
    public static final Component TITLE_TEXT = ComponentCompat.literal("Select Color");
    public static final Component LABEL_HEX = ComponentCompat.literal("HEX");
    public static final Component LABEL_NEW = ComponentCompat.literal("New");
    public static final Component LABEL_CURRENT = ComponentCompat.literal("Current");
    public static final Component PRESET_TEXT = ComponentCompat.literal("Presets");
    public static final Component RECENT_TEXT = ComponentCompat.literal("Recent Colors");
    public static final Component BTN_CANCEL = ComponentCompat.literal("Cancel");
    public static final Component BTN_OK = ComponentCompat.literal("OK");
    public static final Component BTN_TOGGLE_HSV = ComponentCompat.literal("Mode: HSV");
    public static final Component BTN_TOGGLE_RGB = ComponentCompat.literal("Mode: RGB");

    private final Option<Integer> targetOption;
    private final BiConsumer<Integer, ColorPickerOverlay> onConfirm;
    private final int originalColor;
    private LayoutConfig cachedConfig = null;

    private final ActionButtonWidget toggleModeButton = new ActionButtonWidget(BTN_TOGGLE_HSV, this::toggleMode);

    private final SliderWidget hueSlider;
    private final SliderWidget alphaSliderVertical;
    private final SliderWidget alphaSliderHorizontal;
    private final SliderWidget rSlider;
    private final SliderWidget gSlider;
    private final SliderWidget bSlider;

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

    public ColorPickerOverlay(Option<Integer> targetOption, BiConsumer<Integer, ColorPickerOverlay> onConfirm) {
        super(BTN_CANCEL, BTN_OK);
        this.targetOption = targetOption;
        this.onConfirm = onConfirm;
        this.originalColor = targetOption.getPendingValue();

        setCallbacks(this::handleOk, this::handleCancel);

        initPresets();
        initColorFromRgb(originalColor);

        this.hueSlider = new SliderWidget(0.0, 1.0, 0.0, this.hue, val -> {
            this.hue = val.floatValue();
            updateFromHsb();
            updatePendingValue();
        }).setOrientation(SliderWidget.Orientation.VERTICAL);

        this.alphaSliderVertical = new SliderWidget(0.0, 1.0, 0.0, this.alpha, val -> {
            this.alpha = val.floatValue();
            updatePendingValue();
        }).setOrientation(SliderWidget.Orientation.VERTICAL);

        this.alphaSliderHorizontal = new SliderWidget(0.0, 1.0, 0.0, this.alpha, val -> {
            this.alpha = val.floatValue();
            updatePendingValue();
        }).setOrientation(SliderWidget.Orientation.HORIZONTAL);

        this.rSlider = new SliderWidget(0.0, 255.0, 1.0, this.red, val -> {
            this.red = val.intValue();
            updateFromRgb();
            updatePendingValue();
        }).setOrientation(SliderWidget.Orientation.HORIZONTAL);

        this.gSlider = new SliderWidget(0.0, 255.0, 1.0, this.green, val -> {
            this.green = val.intValue();
            updateFromRgb();
            updatePendingValue();
        }).setOrientation(SliderWidget.Orientation.HORIZONTAL);

        this.bSlider = new SliderWidget(0.0, 255.0, 1.0, this.blue, val -> {
            this.blue = val.intValue();
            updateFromRgb();
            updatePendingValue();
        }).setOrientation(SliderWidget.Orientation.HORIZONTAL);
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

        syncSliders();
    }

    private void syncSliders() {
        if (this.hueSlider != null) this.hueSlider.setValue(this.hue);
        if (this.alphaSliderVertical != null) this.alphaSliderVertical.setValue(this.alpha);
        if (this.alphaSliderHorizontal != null) this.alphaSliderHorizontal.setValue(this.alpha);
        if (this.rSlider != null) this.rSlider.setValue(this.red);
        if (this.gSlider != null) this.gSlider.setValue(this.green);
        if (this.bSlider != null) this.bSlider.setValue(this.blue);
    }

    private void updateFromHsb() {
        int rgb = Color.HSBtoRGB(hue, saturation, brightness);
        this.red = (rgb >> 16) & 0xFF;
        this.green = (rgb >> 8) & 0xFF;
        this.blue = rgb & 0xFF;
        syncSliders();
    }

    private void updateFromRgb() {
        float[] hsb = Color.RGBtoHSB(red, green, blue, null);
        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];
        syncSliders();
    }

    private int getCurrentColor() {
        int aInt = (int) (alpha * 255.0f) & 0xFF;
        return (aInt << 24) | (red << 16) | (green << 8) | blue;
    }

    @Override
    public void render(Font font, GuiCompat gui, LayoutEngine layout, int x, int y, int width, int height, int mouseX,
                       int mouseY) {
        updateDragStates(mouseX, mouseY, layout);

        LayoutConfig cfg = layout.getConfig();
        cachedConfig = cfg;
        int currentArgb = getCurrentColor();

        Bounds bound = layout.pickerDialog;
        gui.enableScissor(bound.x, bound.y, bound.maxX, bound.maxY);

        renderDialogFrame(font, gui, bound, TITLE_TEXT, cfg);

        setupSliders(cfg);

        if (currentMode == PickerMode.HSV) {
            renderSbSpace(gui, layout.pickerSbSpace, cfg);

            renderHueBarBackground(gui, layout.pickerHueSlider, cfg);
            this.hueSlider.render(font, gui, layout, layout.pickerHueSlider.x, layout.pickerHueSlider.y, layout.pickerHueSlider.width, layout.pickerHueSlider.height, mouseX, mouseY);

            renderAlphaBarBackground(gui, layout.pickerAlphaSliderVertical, cfg, false);
            this.alphaSliderVertical.render(font, gui, layout, layout.pickerAlphaSliderVertical.x, layout.pickerAlphaSliderVertical.y, layout.pickerAlphaSliderVertical.width, layout.pickerAlphaSliderVertical.height, mouseX, mouseY);
        } else {
            renderRgbSliders(font, gui, layout, cfg, mouseX, mouseY);

            Bounds aB = layout.pickerAlphaSliderHorizontal;
            renderAlphaBarBackground(gui, aB, cfg, true);
            this.alphaSliderHorizontal.render(font, gui, layout, aB.x, aB.y, aB.width, aB.height, mouseX, mouseY);
        }

        renderColorPreviews(font, gui, layout, currentArgb);
        renderColorMetrics(font, gui, layout, cfg, currentArgb);
        renderColorPalettes(font, gui, layout, cfg);

        Bounds cancelB = layout.pickerCancelButton;
        cancelButton.render(font, gui, layout, cancelB.x, cancelB.y, cancelB.width, cancelB.height, mouseX, mouseY);

        Bounds okB = layout.pickerOkButton;
        okButton.renderWithBackground(font, gui, layout, okB.x, okB.y, okB.width, okB.height, mouseX, mouseY, COLOR_BTN_OK_BG);

        Bounds b = layout.pickerModeToggleButton;
        toggleModeButton.render(font, gui, layout, b.x, b.y, b.width, b.height, mouseX, mouseY);

        gui.disableScissor();
    }

    private void setupSliders(LayoutConfig cfg) {
        this.hueSlider
            .setBarThickness(cfg.pickerSliderWidth)
            .setKnobSize(cfg.pickerSliderIndicatorSize)
            .setRounded(true)
            .setColors(0x00000000, 0x00000000, Colors.WHITE);

        this.alphaSliderVertical
            .setBarThickness(cfg.pickerSliderWidth)
            .setKnobSize(cfg.pickerSliderIndicatorSize)
            .setRounded(true)
            .setColors(0x00000000, 0x00000000, Colors.WHITE);

        this.alphaSliderHorizontal
            .setBarThickness(cfg.pickerSliderWidth)
            .setKnobSize(cfg.pickerSliderIndicatorSize)
            .setRounded(true)
            .setColors(0x00000000, 0x00000000, Colors.WHITE);

        this.rSlider
            .setBarThickness(cfg.pickerSliderWidth)
            .setKnobSize(cfg.pickerSliderIndicatorSize)
            .setRounded(true)
            .setColors(0x00000000, 0x00000000, Colors.WHITE);

        this.gSlider
            .setBarThickness(cfg.pickerSliderWidth)
            .setKnobSize(cfg.pickerSliderIndicatorSize)
            .setRounded(true)
            .setColors(0x00000000, 0x00000000, Colors.WHITE);

        this.bSlider
            .setBarThickness(cfg.pickerSliderWidth)
            .setKnobSize(cfg.pickerSliderIndicatorSize)
            .setRounded(true)
            .setColors(0x00000000, 0x00000000, Colors.WHITE);
    }

    private void renderSbSpace(GuiCompat gui, Bounds sb, LayoutConfig cfg) {
        int baseHueRgb = Color.HSBtoRGB(hue, 1.0f, 1.0f) | 0xFF000000;
        RenderUtil.renderVanillaGuiElement(gui, new ColorGradientRenderState(gui, sb.x, sb.maxX, sb.y, sb.maxY,
            Colors.WHITE, Colors.BLACK, baseHueRgb, Colors.BLACK));

        int hX = sb.x + (int) (this.saturation * sb.width);
        int hY = sb.y + (int) ((1.0f - this.brightness) * sb.height);
        int d = cfg.pickerSbSpaceIndicatorSize;
        final int borderThickness = 1;
        RenderUtil.renderOutline(gui, hX - d, hY - d, d * 2, d * 2, borderThickness, 0xFFFFFFFF);
        RenderUtil.renderInner(gui, hX - d, hY - d, d * 2, d * 2, borderThickness, (0xFF << 24) | (red << 16) | (green << 8) | blue);
    }

    private void renderHueBarBackground(GuiCompat gui, Bounds hueB, LayoutConfig cfg) {
        for (int i = 0; i < hueB.height; i++) {
            gui.fill(hueB.x, hueB.y + i, hueB.maxX, hueB.y + i + 1,
                Color.HSBtoRGB(i / (float) hueB.height, 1.0f, 1.0f));
        }
    }

    private void renderRgbSliders(Font font, GuiCompat gui, LayoutEngine layout, LayoutConfig cfg, int mouseX, int mouseY) {
        Bounds rB = layout.pickerRSlider;
        for (int i = 0; i < rB.width; i++) {
            int rStep = (int) ((i / (float) rB.width) * 255.0f);
            gui.fill(rB.x + i, rB.y, rB.x + i + 1, rB.maxY, 0xFF000000 | (rStep << 16) | (green << 8) | blue);
        }
        this.rSlider.render(font, gui, layout, rB.x, rB.y, rB.width, rB.height, mouseX, mouseY);

        Bounds gB = layout.pickerGSlider;
        for (int i = 0; i < gB.width; i++) {
            int gStep = (int) ((i / (float) gB.width) * 255.0f);
            gui.fill(gB.x + i, gB.y, gB.x + i + 1, gB.maxY, 0xFF000000 | (red << 16) | (gStep << 8) | blue);
        }
        this.gSlider.render(font, gui, layout, gB.x, gB.y, gB.width, gB.height, mouseX, mouseY);

        Bounds bB = layout.pickerBSlider;
        for (int i = 0; i < bB.width; i++) {
            int bStep = (int) ((i / (float) bB.width) * 255.0f);
            gui.fill(bB.x + i, bB.y, bB.x + i + 1, bB.maxY, 0xFF000000 | (red << 16) | (green << 8) | bStep);
        }
        this.bSlider.render(font, gui, layout, bB.x, bB.y, bB.width, bB.height, mouseX, mouseY);
    }

    private void renderAlphaBarBackground(GuiCompat gui, Bounds alphaB, LayoutConfig cfg, boolean isHorizontal) {
        int cellSize = (int) (cfg.pickerSliderWidth / 3.0f);
        if (cellSize <= 0) cellSize = 4;
        for (int y = alphaB.y; y < alphaB.maxY; y += cellSize) {
            int h = Math.min(cellSize, alphaB.maxY - y);
            for (int x = alphaB.x; x < alphaB.maxX; x += cellSize) {
                int w = Math.min(cellSize, alphaB.maxX - x);
                boolean isEven = ((x - alphaB.x) / cellSize + (y - alphaB.y) / cellSize) % 2 == 0;
                int gridColor = isEven ? 0xFFFFFFFF : 0xFFD0D0D0;
                gui.fill(x, y, x + w, y + h, gridColor);
            }
        }

        if (isHorizontal) {
            for (int i = 0; i < alphaB.width; i++) {
                float ratio = i / (float) alphaB.width;
                int alphaVal = (int) (ratio * 255.0f) & 0xFF;
                int blendedColor = (alphaVal << 24) | (red << 16) | (green << 8) | blue;
                gui.fill(alphaB.x + i, alphaB.y, alphaB.x + i + 1, alphaB.maxY, blendedColor);
            }
        } else {
            int opaqueColor = (255 << 24) | (red << 16) | (green << 8) | blue;
            int transparentColor = (0 << 24) | (red << 16) | (green << 8) | blue;
            gui.fillGradient(alphaB.x, alphaB.y, alphaB.maxX, alphaB.maxY, opaqueColor, transparentColor);
        }
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
        int hexLabelWidth = ComponentCompat.width(font, LABEL_HEX) + 6;
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
        if (currentMode == PickerMode.HSV && this.isDraggingSBSpace) {
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
    }

    private void updatePendingValue() {
        this.targetOption.setPendingValue(getCurrentColor());
    }

    private void applyColor(int rgb) {
        initColorFromRgb(rgb);
        updatePendingValue();
        SoundUtil.clickSound();
    }

    private void handleCancel() {
        this.targetOption.setPendingValue(originalColor);
        this.onConfirm.accept(originalColor, this);
    }

    private void handleOk() {
        if (cachedConfig != null) {
            int finalColor = getCurrentColor();
            recentColors.remove((Integer) finalColor);
            recentColors.add(0, finalColor);

            if (recentColors.size() > cachedConfig.pickerMaxPaletteColors) {
                recentColors.remove(recentColors.size() - 1);
            }
            this.onConfirm.accept(finalColor, this);
        }
    }

    private void toggleMode(ActionButtonWidget btn) {
        this.currentMode = this.currentMode == PickerMode.HSV ? PickerMode.RGB : PickerMode.HSV;
        btn.setLabel(this.currentMode == PickerMode.HSV ? BTN_TOGGLE_HSV : BTN_TOGGLE_RGB);
        SoundUtil.clickSound();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int modifiers, LayoutEngine layout) {
        if (cancelButton.mouseClicked(mouseX, mouseY, button, 0, layout) ||
            okButton.mouseClicked(mouseX, mouseY, button, 0, layout) ||
            toggleModeButton.mouseClicked(mouseX, mouseY, button, 0, layout)) {
            return true;
        }

        if (currentMode == PickerMode.HSV) {
            if (layout.pickerSbSpace.contains(mouseX, mouseY)) {
                this.isDraggingSBSpace = true;
                SoundUtil.clickSound();
                return true;
            }
            if (this.hueSlider.mouseClicked(mouseX, mouseY, button, 0, layout)) {
                SoundUtil.clickSound();
                return true;
            }
            if (layout.pickerAlphaSliderVertical.contains(mouseX, mouseY) &&
                this.alphaSliderVertical.mouseClicked(mouseX, mouseY, button, 0, layout)) {
                SoundUtil.clickSound();
                return true;
            }
        } else {
            if (this.rSlider.mouseClicked(mouseX, mouseY, button, 0, layout) ||
                this.gSlider.mouseClicked(mouseX, mouseY, button, 0, layout) ||
                this.bSlider.mouseClicked(mouseX, mouseY, button, 0, layout)) {
                SoundUtil.clickSound();
                return true;
            }

            if (layout.pickerAlphaSliderHorizontal.contains(mouseX, mouseY) &&
                this.alphaSliderHorizontal.mouseClicked(mouseX, mouseY, button, 0, layout)) {
                SoundUtil.clickSound();
                return true;
            }
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

        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY, LayoutEngine layout) {
        if (currentMode == PickerMode.HSV) {
            if (this.hueSlider.mouseDragged(mouseX, mouseY, button, dragX, dragY, layout)) {
                return true;
            }
            if (this.alphaSliderVertical.mouseDragged(mouseX, mouseY, button, dragX, dragY, layout)) {
                return true;
            }
        } else {
            if (this.rSlider.mouseDragged(mouseX, mouseY, button, dragX, dragY, layout) ||
                this.gSlider.mouseDragged(mouseX, mouseY, button, dragX, dragY, layout) ||
                this.bSlider.mouseDragged(mouseX, mouseY, button, dragX, dragY, layout)) {
                return true;
            }
            if (this.alphaSliderHorizontal.mouseDragged(mouseX, mouseY, button, dragX, dragY, layout)) {
                return true;
            }
        }
        return true;
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button, LayoutEngine layout) {
        this.isDraggingSBSpace = false;
        this.hueSlider.mouseReleased(mouseX, mouseY, button, layout);
        this.alphaSliderVertical.mouseReleased(mouseX, mouseY, button, layout);
        this.alphaSliderHorizontal.mouseReleased(mouseX, mouseY, button, layout);
        this.rSlider.mouseReleased(mouseX, mouseY, button, layout);
        this.gSlider.mouseReleased(mouseX, mouseY, button, layout);
        this.bSlider.mouseReleased(mouseX, mouseY, button, layout);
    }
}
