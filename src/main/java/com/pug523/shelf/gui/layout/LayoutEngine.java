package com.pug523.shelf.gui.layout;

import java.util.ArrayList;
import java.util.List;

import com.pug523.shelf.gui.TabNode;
import com.pug523.shelf.gui.model.RenderableItem;
import com.pug523.shelf.gui.overlay.ColorPickerOverlay;
import com.pug523.shelf.gui.text.TextUtil;
import com.pug523.shelf.gui.widget.ActionButtonWidget;

import net.minecraft.client.gui.Font;

public class LayoutEngine {
    private final LayoutConfig config;

    private int width;
    private int height;
    public int optionWidgetRightMargin;
    public int totalOptionHeight;

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
    public Bounds pickerHueSlider;
    public Bounds pickerAlphaSlider;
    public Bounds pickerNewColorSwat;
    public Bounds pickerCurrentColorSwat;
    public Bounds pickerOkButton;
    public Bounds pickerCancelButton;
    public Bounds pickerCloseButton;
    public Bounds pickerClearButton;
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

        // Tab items
        this.tabItemBounds.clear();
        for (int i = 0; i < flatTabs.size(); i++) {
            int y = config.topBarHeight + config.tabItemStartOffsetY + (i * config.tabItemHeight);
            tabItemBounds.add(new Bounds(0, y, tabArea.width, config.tabItemHeight));
        }
        this.tabScrollbarTrack = new Bounds(tabArea.maxX - config.scrollbarWidth, tabArea.y + config.engineTrackPaddingY,
            config.scrollbarWidth, Math.max(0, tabArea.height - config.engineTrackPaddingY));

        // Option rows
        this.optionRows.clear();
        int extraPadding = 0;
        int optionInnerWidth = Math.max(0, optionArea.width - config.scrollbarWidth);

        for (int i = 0; i < optionItems.size(); i++) {
            RenderableItem item = optionItems.get(i);
            if (item.isHeader() && i > 0) {
                extraPadding += config.optionHeaderOffsetY;
            }
            int rowY = config.topBarHeight + config.optionItemStartOffsetY + (i * config.optionItemHeight) + extraPadding;
            Bounds rowBounds = new Bounds(optionArea.x, rowY, optionInnerWidth, config.optionItemHeight);

            int tx = optionArea.x + config.optionTextOffsetX;
            int ty = rowY + (config.optionItemHeight - font.lineHeight) / 2 + config.engineTextHeightOffset;

            if (item.isHeader()) {
                optionRows.add(new OptionRowLayout(true, rowBounds, tx, ty, null));
            } else {
                int rx = optionArea.maxX - config.resetButtonWidth - config.rightMarginFromResetButton + config.engineResetButtonOffsetX;
                Bounds resetBounds = new Bounds(rx, rowY + config.resetButtonPaddingY, config.resetButtonWidth, config.resetButtonHeight);
                optionRows.add(new OptionRowLayout(false, rowBounds, tx, ty, resetBounds));
            }
        }

        if (!this.optionRows.isEmpty()) {
            OptionRowLayout lastRow = this.optionRows.get(this.optionRows.size() - 1);
            this.totalOptionHeight = lastRow.rowBounds.maxY + config.optionItemStartOffsetY;
        } else {
            this.totalOptionHeight = 0;
        }

        this.optionScrollbarTrack = new Bounds(optionArea.maxX - config.scrollbarWidth, optionArea.y + config.engineTrackPaddingY,
            config.scrollbarWidth, Math.max(0, optionArea.height - config.engineTrackPaddingY));

        rebuildConfirmationLayout();
        rebuildColorPickerLayout(font);
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

        // Title bar buttons
        int xBtnWidth = TextUtil.width(font, ColorPickerOverlay.BTN_X);
        int xX = rightEdgeX - xBtnWidth;
        this.pickerCloseButton = new Bounds(xX - config.pickerXBtnPadding, titleY - config.pickerXBtnPadding,
            xBtnWidth + (config.pickerXBtnPadding * 2), font.lineHeight + (config.pickerXBtnPadding * 2));

        int toggleW = config.pickerModeToggleWidth;
        int toggleH = config.pickerModeToggleHeight;
        int toggleX = this.pickerCloseButton.x - toggleW - config.engineModeToggleSpacingX;
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
        this.pickerAlphaSlider = new Bounds(alphaX, sbY, sliderW, sbSize);

        // RGB Sliders
        int rgbSliderW = sbSize + spacing + sliderW;
        int rgbSliderH = config.pickerRgbSliderHeight;
        int totalSlidersH = rgbSliderH * 3;
        int rgbSpacing = (sbSize - totalSlidersH) / 2;

        this.pickerRSlider = new Bounds(sbX, sbY, rgbSliderW, rgbSliderH);
        this.pickerGSlider = new Bounds(sbX, sbY + rgbSliderH + rgbSpacing, rgbSliderW, rgbSliderH);
        this.pickerBSlider = new Bounds(sbX, sbY + (rgbSliderH + rgbSpacing) * 2, rgbSliderW, rgbSliderH);

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

        // Right side bottom buttons: Derived from dialog's actual bottom line to ensure anchoring
        int btnW = config.pickerButtonWidth;
        int btnH = config.pickerButtonHeight;

        int clearPadX = config.pickerClearBtnPaddingX + config.engineClearButtonSpacingX;
        int clearPadY = config.pickerClearBtnPaddingY + config.engineClearButtonSpacingY;
        int clearBoundsH = font.lineHeight + (clearPadY * 2);

        // Lock clear button to the absolute bottom inner edge of the dialog
        int clearBoundsY = (py + pH) - padding - clearBoundsH;

        if (!this.pickerRecentBounds.isEmpty()) {
            int clearWidth = TextUtil.width(font, ColorPickerOverlay.BTN_CLEAR);
            int clearX = visualRightEdgeX - (clearWidth + clearPadX * 2);
            this.pickerClearButton = new Bounds(clearX, clearBoundsY, clearWidth + (clearPadX * 2), clearBoundsH);
        }

        int actionBtnY = clearBoundsY - btnH - config.engineActionButtonSpacingY;

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
}
