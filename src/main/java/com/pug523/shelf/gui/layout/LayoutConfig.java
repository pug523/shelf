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
    public int scrollbarWidth = 3;
    public int scrollbarMinHeight = 20;
    public double scrollbarMaxHeightPercent = 0.25;
    public int optionTextOffsetX = 16;
    public int optionHeaderOffsetX = 8;
    public int optionHeaderOffsetY = 12;
    public int descTextOffsetX = 10;
    public int descTextOffsetY = 15;
    public int descTextRightPadding = 20;

    // Capsule Toggle Widget Sizing
    public int capsuleToggleWidth = 30;
    public int capsuleToggleHeight = 14;
    public int capsuleTogglePaddingRight = 25;
    public int capsuleToggleHitboxPadding = 4;

    // Toggle Action Button Widget Sizing
    public int toggleButtonWidth = 44;
    public int toggleButtonHeight = 18;
    public int toggleButtonRightPadding = 20;

    // Slider Widget Sizing
    public int sliderWidth = 80;
    public int sliderHeight = 4;
    public int sliderKnobSize = 8;
    public int sliderPaddingX = 25;
    public int sliderTextPadding = 5;

    // Color tokens
    public int colorScreenBaseBackground = Colors.ALPHA_DARK_62;
    public int colorHeaderBackground = Colors.ALPHA_WHITE_08;
    public int colorFooterBackground = Colors.ALPHA_BLACK_13;
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
    public int colorButtonBackgroundDisabled = Colors.BUTTON_GRAY_DIS;
    public int colorButtonText = Colors.WHITE;
    public int colorButtonTextDisabled = Colors.MUTED_GRAY;

    // Boolean Toggle Widget Colors
    public int colorToggleBgOff = Colors.MIDDLE_GRAY;
    public int colorToggleBgOn = Colors.GREEN2;
    public int colorToggleKnob = Colors.WHITE;
    public int colorToggleBgOffHover = Colors.MIDDLE_GRAY2;
    public int colorToggleBgOnHover = Colors.GREEN3;

    // Slider Widget Colors
    public int colorSliderTrack = Colors.ALPHA_WHITE_27;
    public int colorSliderProgress = Colors.INDIGO;
    public int colorSliderKnob = Colors.WHITE;
    public int colorSliderText = Colors.OFF_WHITE;

    public static final LayoutConfig createDefault() {
        return new LayoutConfig();
    }
}
