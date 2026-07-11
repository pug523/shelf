package com.pug523.shelf.gui.layout;

import com.pug523.shelf.gui.Colors;

public class LayoutConfig {
    // Structural layout defaults
    public int topBarHeight = 30;
    public int bottomBarHeight = 30;
    public double tabAreaWidthPercent = 0.20;
    public double optionAreaWidthPercent = 0.55;
    public double tabScrollSpeed = 15.0;
    public double optionScrollSpeed = 20.0;

    // Sizing constants
    public int tabItemHeight = 22;
    public int optionItemHeight = 22;
    public int tabTreeIndentation = 10;
    public int tabItemStartOffsetY = 5;
    public int optionItemStartOffsetY = 10;
    public int textPaddingX = 10;
    public int resetButtonWidth = 27;
    public int resetButtonHeight = 16;
    public int resetButtonPaddingY = 4;
    public int rightMarginFromResetButton = 10;
    public int scrollbarWidth = 3;
    public int scrollbarMinHeight = 20;
    public double scrollbarMaxHeightPercent = 0.15;
    public int optionTextOffsetX = 16;
    public int optionHeaderOffsetX = 8;
    public int optionHeaderOffsetY = 12;
    public int descTextOffsetX = 10;
    public int descTextOffsetY = 15;
    public int descTextRightPadding = 20;

    public int footerButtonWidth = 60;
    public int footerButtonHeight = 20;
    public int footerPaddingRight = 10;
    public int footerButtonSpacing = 5;
    public int resetIconSize = 14;
    public int tabArrowOffsetX = 2;
    public int tabArrowOffsetY = 3;
    public int tabTextOffsetX = 12;
    public int descTitleSpacingY = 12;

    // Capsule Toggle Widget Sizing
    public int capsuleToggleWidth = 30;
    public int capsuleToggleHeight = 15;
    public int capsuleToggleHitboxPadding = 4;

    // Toggle Action Button Widget Sizing
    public int toggleButtonWidth = 44;
    public int toggleButtonHeight = 18;

    // Toggle Box Button Widget Sizing
    public int boxToggleWidth = 14;
    public int boxToggleHeight = 14;
    public int boxToggleOutlineThickness = 1;
    public int boxToggleInnerPadding = 2;

    // Cycling Button Widget Sizing
    public int cyclingButtonWidthPadding = 20;
    public int cyclingButtonHeight = 18;

    // Keybind Button Widget Sizing
    public int keybindButtonMaxWidth = 120;
    public int keybindButtonWidthPadding = 20;
    public int keybindButtonHeight = 18;

    // Slider Widget Sizing
    public int sliderWidth = 115;
    public int sliderHeight = 4;
    public int sliderKnobSize = 12;
    public int sliderTextPadding = 8;

    // Color Picker Widget Sizing
    public int colorPickerSquareRightPadding = 5;

    // Color Picker Overlay Sizing
    public double pickerDialogWidthPercent = 0.35;
    public double pickerDialogHeightPercent = 0.55;
    public int pickerDialogMinWidth = 290;
    public int pickerDialogMinHeight = 220;
    public int pickerDialogMaxWidth = 480;
    public int pickerDialogMaxHeight = 380;

    public int pickerPaddingInner = 16;
    public int pickerSliderWidth = 6;
    public int pickerSliderSpacing = 10;
    public int pickerRgbSliderHeight = 6;
    public int pickerSwatWidth = 32;
    public int pickerSwatHeight = 24;
    public int pickerPaletteBoxSize = 12;
    public int pickerPaletteBoxSpacing = 4;
    public int pickerMaxPaletteColors = 10;
    public int pickerTitleOffsetY = 36;
    public int pickerSwatOffsetY = 12;
    public int pickerSwatSpacingX = 12;
    public int pickerPaletteLabelOffsetY = 20;
    public int pickerRecentLabelOffsetY = 22;

    public int pickerButtonWidth = 55;
    public int pickerButtonHeight = 18;
    public int pickerXBtnPadding = 4;
    public int pickerMetricsOffsetY = 8;
    public int pickerMetricsSpacingY = 14;
    public int pickerPaletteTextSpacingY = 12;
    public int pickerClearBtnPaddingX = 4;
    public int pickerClearBtnPaddingY = 2;
    public int pickerSliderIndicatorSize = 2;
    public int pickerSbSpaceIndicatorSize = 4;
    public int pickerModeToggleWidth = 64;
    public int pickerModeToggleHeight = 14;

    public int confirmDialogWidth = 320;
    public int confirmDialogHeight = 120;
    public int confirmPaddingInner = 16;
    public int confirmButtonWidth = 60;
    public int confirmButtonHeight = 20;
    public int confirmButtonSpacing = 12;

    // Rounded
    public boolean roundedCapsule = true;
    public boolean roundedSlider = true;

    // Shadow
    public boolean actionButtonShadow = true;
    public boolean toggleButtonShadow = true;

    // Color tokens
    public int colorScreenBaseBackground = Colors.ALPHA_BLACK_13;
    public int colorHeaderBackground = Colors.ALPHA_WHITE_08;
    public int colorFooterBackground = Colors.ALPHA_BLACK_53;
    public int colorTabPanelBackground = Colors.ALPHA_BLACK_20;
    public int colorOptionPanelBackground = Colors.ALPHA_BLACK_26;
    public int colorDescriptionPanelBackground = Colors.ALPHA_BLACK_33;

    public int colorTextPrimary = Colors.WHITE;
    public int colorTextSecondary = Colors.TEXT_GRAY;
    public int colorTextMuted = Colors.OFF_WHITE;
    public int colorTextDisabled = Colors.ALPHA_WHITE_53;

    public int colorItemSelectedText = Colors.WHITE;
    public int colorItemUnselectedText = Colors.ALPHA_WHITE_60;
    public int colorItemHoverBackground = Colors.ALPHA_WHITE_13;
    public int colorItemSelectedBackground = Colors.ALPHA_WHITE_27_O;

    public int colorScrollBarTrack = Colors.ALPHA_BLACK_53;
    public int colorScrollBarThumb = Colors.OFF_WHITE;

    public int colorButtonBorder = Colors.BORDER_BLACK;
    public int colorButtonBackground = Colors.BUTTON_GRAY;
    public int colorButtonBackgroundHover = Colors.BUTTON_GRAY_HOVER;
    public int colorButtonBackgroundDisabled = Colors.ALPHA_BLACK_53;
    public int colorButtonText = Colors.WHITE;
    public int colorButtonTextDisabled = Colors.MUTED_GRAY;

    // Boolean Toggle Widget Colors
    public int colorToggleBgOff = Colors.ALPHA_WHITE_08;
    public int colorToggleBgOn = Colors.GREEN2;
    public int colorToggleKnob = Colors.WHITE;
    public int colorToggleBgOffHover = Colors.MIDDLE_GRAY2;
    public int colorToggleBgOnHover = Colors.GREEN3;
    public int colorToggleBox = Colors.LIGHT_GRAY;
    public int colorToggleBoxHover= Colors.WHITE;

    // Slider Widget Colors
    public int colorSliderTrack = Colors.ALPHA_WHITE_08;
    public int colorSliderProgress = Colors.INDIGO;
    public int colorSliderKnob = Colors.WHITE;
    public int colorSliderText = Colors.OFF_WHITE;

    // Engine
    public int engineMinTabAreaWidth = 60;
    public int engineMinOptionAreaWidth = 150;
    public int engineMinDescAreaWidthThreshold = 50;
    public int engineTrackPaddingY = 1;
    public int engineTextHeightOffset = 1;
    public int engineResetButtonOffsetX = 4;
    public int engineModeToggleSpacingX = 6;
    public int engineTopSectionHeightOffset = 65;
    public int engineMinSbSize = 60;
    public int engineMaxSbSize = 140;
    public int engineSwatTextSpacingY = 4;
    public int engineMinPaletteBoxSpacing = 2;
    public int engineActionButtonSpacingY = 6;
    public int engineActionButtonSpacingX = 6;
    public int engineClearButtonSpacingX = 6;
    public int engineClearButtonSpacingY = 2;

    public static LayoutConfig createDefault() {
        return new LayoutConfig();
    }
}
