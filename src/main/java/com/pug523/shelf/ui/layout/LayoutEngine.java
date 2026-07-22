package com.pug523.shelf.ui.layout;

import java.util.ArrayList;
import java.util.List;

import com.pug523.shelf.gui.layout.Bounds;
import com.pug523.shelf.gui.layout.OptionRowLayout;
import com.pug523.shelf.ui.screen.TabNode;
import com.pug523.shelf.ui.model.RenderableItem;

import net.minecraft.client.gui.Font;

// TODO: refactor
public class LayoutEngine {
    private final LayoutConfig config;

    private int width;
    private int height;
    private int fontLineHeight;

    public int optionWidgetRightMargin;
    public int totalOptionHeight;
    public int totalTabHeight;

    public Bounds tabArea;
    public Bounds optionArea;
    public Bounds descArea;
    public Bounds headerArea;
    public Bounds footerArea;

    public final List<Bounds> footerButtonBounds = new ArrayList<>();
    public final List<Bounds> tabItemBounds = new ArrayList<>();
    public final List<OptionRowLayout> optionRows = new ArrayList<>();

    public Bounds tabScrollbarTrack;
    public Bounds optionScrollbarTrack;

    // Color picker
    public Bounds pickerDialog;
    public Bounds pickerSbSpace;
    public Bounds pickerRSlider;
    public Bounds pickerGSlider;
    public Bounds pickerBSlider;
    public Bounds pickerAlphaSliderHorizontal;
    public Bounds pickerHueSlider;
    public Bounds pickerAlphaSliderVertical;
    public Bounds pickerNewColorSwat;
    public Bounds pickerCurrentColorSwat;
    public Bounds pickerOkButton;
    public Bounds pickerCancelButton;
    public Bounds pickerModeToggleButton;
    public final List<Bounds> pickerPresetBounds = new ArrayList<>();
    public final List<Bounds> pickerRecentBounds = new ArrayList<>();

    // Confirmation dialog
    public Bounds confirmDialog;
    public Bounds confirmYesButton;
    public Bounds confirmNoButton;

    public LayoutEngine(LayoutConfig config) {
        this.config = config;
    }

    public void rebuild(int width, int height, Font font, List<ActionButtonWidget> footerButtons, List<TabNode> flatTabs,
                        List<RenderableItem> optionItems) {
        this.width = width;
        this.height = height;
        this.fontLineHeight = font.lineHeight;
        this.optionWidgetRightMargin = config.resetButtonWidth + config.rightMarginFromResetButton;

        // Base structural dimensions
        this.headerArea = new Bounds(0, 0, width, config.topBarHeight);
        this.footerArea = new Bounds(0, height - config.bottomBarHeight, width, config.bottomBarHeight);

        int mainContentH = Math.max(0, height - config.topBarHeight - config.bottomBarHeight);

        int tabW = Math.max(config.engineMinTabAreaWidth, (int) (width * config.tabAreaWidthPercent));
        int optionW = Math.max(config.engineMinOptionAreaWidth, (int) (width * config.optionAreaWidthPercent));
        int descW = Math.max(0, width - tabW - optionW);

        if (descW < config.engineMinDescAreaWidthThreshold && width > (config.engineMinTabAreaWidth + config.engineMinOptionAreaWidth)) {
            descW = Math.max(0, width - tabW - config.engineMinOptionAreaWidth);
            optionW = width - tabW - descW;
        }

        this.tabArea = new Bounds(0, config.topBarHeight, tabW, mainContentH);
        this.optionArea = new Bounds(tabW, config.topBarHeight, optionW, mainContentH);
        this.descArea = new Bounds(tabW + optionW, config.topBarHeight, descW, mainContentH);

        // Footer buttons
        this.footerButtonBounds.clear();
        if (footerButtons != null && !footerButtons.isEmpty()) {
            int btnW = config.footerButtonWidth;
            int btnH = config.footerButtonHeight;
            int currentX = width - btnW - config.footerPaddingRight;
            int btnY = footerArea.y + (config.bottomBarHeight - btnH) / 2;

            for (int i = footerButtons.size() - 1; i >= 0; i--) {
                // Prevent drawing outside the screen on ultra-low widths
                if (currentX >= 0) {
                    footerButtonBounds.add(0, new Bounds(currentX, btnY, btnW, btnH));
                }
                currentX -= (btnW + config.footerButtonSpacing);
            }
        }

        performDynamicLayout(flatTabs, optionItems, config);

        this.tabScrollbarTrack = new Bounds(tabArea.maxX - config.scrollbarWidth, tabArea.y + config.engineTrackPaddingY,
            config.scrollbarWidth, Math.max(0, tabArea.height - config.engineTrackPaddingY));

        this.optionScrollbarTrack = new Bounds(optionArea.maxX - config.scrollbarWidth, optionArea.y + config.engineTrackPaddingY,
            config.scrollbarWidth, Math.max(0, optionArea.height - config.engineTrackPaddingY));

        rebuildConfirmationLayout();
        rebuildColorPickerLayout(font);
    }

    public void performDynamicLayout(List<TabNode> activeTabs, List<RenderableItem> activeOptions, LayoutConfig cfg) {
        this.tabItemBounds.clear();
        for (int i = 0; i < activeTabs.size(); i++) {
            int y = cfg.topBarHeight + cfg.tabItemStartOffsetY + (i * cfg.tabItemHeight);
            this.tabItemBounds.add(new Bounds(0, y, this.tabArea.width, cfg.tabItemHeight));
        }

        if (!this.tabItemBounds.isEmpty()) {
            Bounds lastTab = this.tabItemBounds.get(this.tabItemBounds.size() - 1);
            this.totalTabHeight = lastTab.maxY - this.tabArea.y;
        } else {
            this.totalTabHeight = 0;
        }

        this.optionRows.clear();
        int extraPadding = 0;
        int optionInnerWidth = Math.max(0, this.optionArea.width - cfg.scrollbarWidth);

        for (int i = 0; i < activeOptions.size(); i++) {
            RenderableItem item = activeOptions.get(i);

            if (item.isHeader() && i > 0) {
                extraPadding += cfg.optionHeaderOffsetY;
            }

            int rowY = cfg.topBarHeight + cfg.optionItemStartOffsetY + (i * cfg.optionItemHeight) + extraPadding;
            Bounds rowBounds = new Bounds(this.optionArea.x, rowY, optionInnerWidth, cfg.optionItemHeight);

            int tx = this.optionArea.x + cfg.optionTextOffsetX;
            int ty = rowY + (cfg.optionItemHeight - this.fontLineHeight) / 2 + cfg.engineTextHeightOffset;

            if (item.isHeader()) {
                this.optionRows.add(new OptionRowLayout(true, rowBounds, tx, ty, null));
            } else {
                int rx = this.optionArea.maxX - cfg.resetButtonWidth - cfg.rightMarginFromResetButton + cfg.engineResetButtonOffsetX;
                Bounds resetBounds = new Bounds(rx, rowY + cfg.resetButtonPaddingY, cfg.resetButtonWidth, cfg.resetButtonHeight);
                this.optionRows.add(new OptionRowLayout(false, rowBounds, tx, ty, resetBounds));
            }
        }

        if (!this.optionRows.isEmpty()) {
            OptionRowLayout lastRow = this.optionRows.get(this.optionRows.size() - 1);
            this.totalOptionHeight = lastRow.rowBounds.maxY - this.optionArea.y;
        } else {
            this.totalOptionHeight = 0;
        }
    }

    private void rebuildConfirmationLayout() {
        // Enforce safe boundaries for confirmation dialog
        int dW = Math.max(config.confirmButtonWidth * 2 + config.confirmButtonSpacing + (config.confirmPaddingInner * 2), config.confirmDialogWidth);
        int dH = config.confirmDialogHeight;

        int dialogX = (width - dW) / 2;
        int dialogY = (height - dH) / 2;
        this.confirmDialog = new Bounds(dialogX, dialogY, dW, dH);

        int btnY = confirmDialog.maxY - config.confirmPaddingInner - config.confirmButtonHeight;
        int totalBtnWidth = (config.confirmButtonWidth * 2) + config.confirmButtonSpacing;
        int startBtnX = dialogX + (dW - totalBtnWidth) / 2;

        this.confirmNoButton = new Bounds(startBtnX, btnY, config.confirmButtonWidth, config.confirmButtonHeight);

        int yesX = startBtnX + config.confirmButtonWidth + config.confirmButtonSpacing;
        this.confirmYesButton = new Bounds(yesX, btnY, config.confirmButtonWidth, config.confirmButtonHeight);
    }

    private void rebuildColorPickerLayout(Font font) {
        int pW = (int) (width * config.pickerDialogWidthPercent);
        int pH = (int) (height * config.pickerDialogHeightPercent);
        pW = Math.max(config.pickerDialogMinWidth, Math.min(pW, config.pickerDialogMaxWidth));
        pH = Math.max(config.pickerDialogMinHeight, Math.min(pH, config.pickerDialogMaxHeight));

        int px = (width - pW) / 2;
        int py = (height - pH) / 2;
        this.pickerDialog = new Bounds(px, py, pW, pH);

        int padding = config.pickerPaddingInner;
        int spacing = config.pickerSliderSpacing;

        int rightEdgeX = px + pW - padding;
        int visualRightEdgeX = rightEdgeX - config.engineClearButtonSpacingY;

        int titleY = this.pickerDialog.y + padding;

        int toggleW = config.pickerModeToggleWidth;
        int toggleH = config.pickerModeToggleHeight;
        int toggleX = rightEdgeX - toggleW - config.engineModeToggleSpacingX;
        int toggleY = titleY + (font.lineHeight - toggleH) / 2;
        this.pickerModeToggleButton = new Bounds(toggleX, toggleY, toggleW, toggleH);

        // Left side content sizing
        int availableW = pW - (padding * 2);
        int sliderW = config.pickerSliderWidth;
        int previewAreaW = (config.pickerSwatWidth * 2) + config.pickerSwatSpacingX;

        int sbSizeX = availableW - (sliderW * 2) - previewAreaW - (spacing * 3);
        int topSectionHeight = pH - (padding * 2) - (config.pickerPaletteBoxSize * 2) - config.engineTopSectionHeightOffset;

        int sbSize = Math.min(sbSizeX, topSectionHeight);
        sbSize = Math.max(config.engineMinSbSize, Math.min(sbSize, config.engineMaxSbSize));

        int sbX = px + padding;
        int sbY = py + config.pickerTitleOffsetY;
        this.pickerSbSpace = new Bounds(sbX, sbY, sbSize, sbSize);

        int hueX = sbX + sbSize + spacing;
        this.pickerHueSlider = new Bounds(hueX, sbY, sliderW, sbSize);

        int alphaX = hueX + sliderW + spacing;
        this.pickerAlphaSliderVertical = new Bounds(alphaX, sbY, sliderW, sbSize);

        // RGBA Sliders
        int rgbSliderW = sbSize + spacing + sliderW;
        int rgbSliderH = config.pickerRgbSliderHeight;


        int labelHeight = font.lineHeight;
        int labelToSliderGap = 2;
        int blockGap = 8;
        int singleBlockHeight = labelHeight + labelToSliderGap + rgbSliderH + blockGap;

        int currentY = sbY;

        // Red Slider
        int rSliderY = currentY + labelHeight + labelToSliderGap;
        this.pickerRSlider = new Bounds(sbX, rSliderY, rgbSliderW, rgbSliderH);
        currentY += singleBlockHeight;

        // Green Slider
        int gSliderY = currentY + labelHeight + labelToSliderGap;
        this.pickerGSlider = new Bounds(sbX, gSliderY, rgbSliderW, rgbSliderH);
        currentY += singleBlockHeight;

        // Blue Slider
        int bSliderY = currentY + labelHeight + labelToSliderGap;
        this.pickerBSlider = new Bounds(sbX, bSliderY, rgbSliderW, rgbSliderH);
        currentY += singleBlockHeight;

        // Alpha Slider (Horizontal)
        int aSliderY = currentY + labelHeight + labelToSliderGap;
        this.pickerAlphaSliderHorizontal = new Bounds(sbX, aSliderY, rgbSliderW, rgbSliderH);

        // Right side content: Top-to-bottom stack layout
        int swatW = config.pickerSwatWidth;
        int swatH = config.pickerSwatHeight;
        int swatY = sbY + font.lineHeight + config.engineSwatTextSpacingY;
        int currentSwatX = rightEdgeX - swatW;
        int newSwatX = currentSwatX - config.pickerSwatSpacingX - swatW;

        this.pickerNewColorSwat = new Bounds(newSwatX, swatY, swatW, swatH);
        this.pickerCurrentColorSwat = new Bounds(currentSwatX, swatY, swatW, swatH);

        // Palettes (Bottom anchored layout)
        this.pickerPresetBounds.clear();
        this.pickerRecentBounds.clear();

        int boxSize = config.pickerPaletteBoxSize;
        int totalBoxes = config.pickerMaxPaletteColors;

        int paletteY = sbY + sbSize + config.pickerPaletteLabelOffsetY;
        int availablePaletteW = pW - (padding * 2) - boxSize;
        int dynamicBoxSpacing = totalBoxes > 1 ? (availablePaletteW - (boxSize * (totalBoxes - 1))) / totalBoxes : 0;
        dynamicBoxSpacing = Math.max(config.engineMinPaletteBoxSpacing, Math.min(dynamicBoxSpacing, config.pickerPaletteBoxSpacing));

        for (int i = 0; i < totalBoxes; i++) {
            int bx = px + padding + i * (boxSize + dynamicBoxSpacing);
            pickerPresetBounds.add(new Bounds(bx, paletteY, boxSize, boxSize));
        }

        int recentY = paletteY + boxSize + config.pickerRecentLabelOffsetY;
        for (int i = 0; i < totalBoxes; i++) {
            int bx = px + padding + i * (boxSize + dynamicBoxSpacing);
            pickerRecentBounds.add(new Bounds(bx, recentY, boxSize, boxSize));
        }

        // Right side bottom buttons
        int btnW = config.pickerButtonWidth;
        int btnH = config.pickerButtonHeight;

        int actionBtnY = (py + pH) - padding - btnH - config.engineActionButtonSpacingY;

        int okX = visualRightEdgeX - btnW;
        int cancelX = okX - btnW - config.engineActionButtonSpacingX;
        this.pickerOkButton = new Bounds(okX, actionBtnY, btnW, btnH);
        this.pickerCancelButton = new Bounds(cancelX, actionBtnY, btnW, btnH);
    }

    public Bounds getScrolledTabBounds(int index, double scrollY) {
        Bounds base = tabItemBounds.get(index);
        return new Bounds(base.x, base.y - (int) scrollY, base.width, base.height);
    }

    public Bounds getScrolledOptionBounds(Bounds original, double scrollY) {
        return new Bounds(original.x, original.y - (int) scrollY, original.width, original.height);
    }

    public Bounds getScrolledResetButtonBounds(Bounds originalReset, double scrollY) {
        if (originalReset == null) return null;
        return new Bounds(originalReset.x, originalReset.y - (int) scrollY, originalReset.width, originalReset.height);
    }

    public int getScrolledTextY(int originalTextY, double scrollY) {
        return originalTextY - (int) scrollY;
    }

    public boolean isWithinContentArea(double mouseY) {
        return mouseY > config.topBarHeight && mouseY < (height - config.bottomBarHeight);
    }

    public double getSliderProgressFromMouse(double mouseX, Bounds widgetBounds) {
        int sliderX = widgetBounds.x + widgetBounds.width - config.sliderWidth - optionWidgetRightMargin;
        double pct = (mouseX - sliderX) / (double) config.sliderWidth;
        return Math.max(0.0, Math.min(1.0, pct));
    }

    public Bounds calculateScrollBarThumb(Bounds track, double scroll, int contentHeight) {
        if (contentHeight <= track.height) return null;
        int maxHeight = (int) (track.height * config.scrollbarMaxHeightPercent);
        int barHeight = (int) ((track.height / (float) contentHeight) * track.height);
        barHeight = Math.max(config.scrollbarMinHeight, Math.min(barHeight, maxHeight));
        int maxScroll = contentHeight - track.height;
        int barY = track.y + (int) ((scroll / maxScroll) * (track.height - barHeight));
        return new Bounds(track.x, barY, track.width, barHeight);
    }

    public boolean isMouseOverTabs(double mouseX) {
        return mouseX >= tabArea.x && mouseX < tabArea.maxX;
    }

    public boolean isMouseOverOptions(double mouseX) {
        return mouseX >= optionArea.x && mouseX < optionArea.maxX;
    }

    public LayoutConfig getConfig() {
        return config;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int fontLineHeight() {
        return fontLineHeight;
    }
}
