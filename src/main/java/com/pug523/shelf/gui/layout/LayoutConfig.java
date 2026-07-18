package com.pug523.shelf.gui.layout;

import com.pug523.shelf.gui.Colors;
import com.pug523.shelf.api.annotation.ConfigEntry;
import com.pug523.shelf.api.annotation.WidgetTypes;

public class LayoutConfig {
    private static final String C = "shelf.mod.config.option.layout.";

    // Structural layout defaults

    @ConfigEntry(key = "top_bar_height", category = C + "layout_structure", group = C + "bars")
    @WidgetTypes.SliderInt(min = 0, max = 100)
    public int topBarHeight = 30;

    @ConfigEntry(key = "bottom_bar_height", category = C + "layout_structure", group = C + "bars")
    @WidgetTypes.SliderInt(min = 0, max = 100)
    public int bottomBarHeight = 30;

    @ConfigEntry(key = "tab_area_width_percent", category = C + "layout_structure", group = C + "proportions")
    @WidgetTypes.SliderDouble(min = 0.0, max = 1.0, step = 0.01)
    public double tabAreaWidthPercent = 0.20;

    @ConfigEntry(key = "option_area_width_percent", category = C + "layout_structure", group = C + "proportions")
    @WidgetTypes.SliderDouble(min = 0.0, max = 1.0, step = 0.01)
    public double optionAreaWidthPercent = 0.55;

    @ConfigEntry(key = "search_bar_width", category = C + "layout_sizing", group = C + "search_bar")
    @WidgetTypes.SliderInt(min = 0, max = 300)
    public int searchBarWidth = 120;

    @ConfigEntry(key = "tab_scroll_speed", category = C + "layout_structure", group = C + "scroll")
    @WidgetTypes.SliderDouble(min = 1.0, max = 100.0, step = 0.5)
    public double tabScrollSpeed = 15.0;

    @ConfigEntry(key = "option_scroll_speed", category = C + "layout_structure", group = C + "scroll")
    @WidgetTypes.SliderDouble(min = 1.0, max = 100.0, step = 0.5)
    public double optionScrollSpeed = 20.0;

    // Sizing constants

    @ConfigEntry(key = "tab_item_height", category = C + "layout_sizing", group = C + "items")
    @WidgetTypes.SliderInt(min = 5, max = 50)
    public int tabItemHeight = 22;

    @ConfigEntry(key = "option_item_height", category = C + "layout_sizing", group = C + "items")
    @WidgetTypes.SliderInt(min = 5, max = 50)
    public int optionItemHeight = 22;

    @ConfigEntry(key = "tab_tree_indentation", category = C + "layout_sizing", group = C + "offsets")
    @WidgetTypes.SliderInt(min = 0, max = 40)
    public int tabTreeIndentation = 4;

    @ConfigEntry(key = "tab_item_start_offset_y", category = C + "layout_sizing", group = C + "offsets")
    @WidgetTypes.SliderInt(min = 0, max = 50)
    public int tabItemStartOffsetY = 5;

    @ConfigEntry(key = "option_item_start_offset_y", category = C + "layout_sizing", group = C + "offsets")
    @WidgetTypes.SliderInt(min = 0, max = 50)
    public int optionItemStartOffsetY = 10;

    @ConfigEntry(key = "text_padding_x", category = C + "layout_sizing", group = C + "padding")
    @WidgetTypes.SliderInt(min = 0, max = 50)
    public int textPaddingX = 10;

    @ConfigEntry(key = "reset_button_width", category = C + "layout_sizing", group = C + "reset_button")
    @WidgetTypes.SliderInt(min = 10, max = 100)
    public int resetButtonWidth = 27;

    @ConfigEntry(key = "reset_button_height", category = C + "layout_sizing", group = C + "reset_button")
    @WidgetTypes.SliderInt(min = 5, max = 50)
    public int resetButtonHeight = 16;

    @ConfigEntry(key = "reset_button_padding_y", category = C + "layout_sizing", group = C + "reset_button")
    @WidgetTypes.SliderInt(min = 0, max = 20)
    public int resetButtonPaddingY = 4;

    @ConfigEntry(key = "right_margin_from_reset_button", category = C + "layout_sizing", group = C + "reset_button")
    @WidgetTypes.SliderInt(min = 0, max = 50)
    public int rightMarginFromResetButton = 10;

    @ConfigEntry(key = "scrollbar_width", category = C + "layout_sizing", group = C + "scrollbar")
    @WidgetTypes.SliderInt(min = 1, max = 15)
    public int scrollbarWidth = 3;

    @ConfigEntry(key = "scrollbar_min_height", category = C + "layout_sizing", group = C + "scrollbar")
    @WidgetTypes.SliderInt(min = 5, max = 100)
    public int scrollbarMinHeight = 20;

    @ConfigEntry(key = "scrollbar_max_height_percent", category = C + "layout_sizing", group = C + "scrollbar")
    @WidgetTypes.SliderDouble(min = 0.01, max = 1.0, step = 0.01)
    public double scrollbarMaxHeightPercent = 0.15;

    @ConfigEntry(key = "option_text_offset_x", category = C + "layout_sizing", group = C + "text_offsets")
    @WidgetTypes.SliderInt(min = 0, max = 100)
    public int optionTextOffsetX = 16;

    @ConfigEntry(key = "option_header_offset_x", category = C + "layout_sizing", group = C + "text_offsets")
    @WidgetTypes.SliderInt(min = 0, max = 100)
    public int optionHeaderOffsetX = 8;

    @ConfigEntry(key = "option_header_offset_y", category = C + "layout_sizing", group = C + "text_offsets")
    @WidgetTypes.SliderInt(min = 0, max = 100)
    public int optionHeaderOffsetY = 12;

    @ConfigEntry(key = "desc_text_offset_x", category = C + "layout_sizing", group = C + "desc_offsets")
    @WidgetTypes.SliderInt(min = 0, max = 100)
    public int descTextOffsetX = 10;

    @ConfigEntry(key = "desc_text_offset_y", category = C + "layout_sizing", group = C + "desc_offsets")
    @WidgetTypes.SliderInt(min = 0, max = 100)
    public int descTextOffsetY = 15;

    @ConfigEntry(key = "desc_text_right_padding", category = C + "layout_sizing", group = C + "desc_offsets")
    @WidgetTypes.SliderInt(min = 0, max = 100)
    public int descTextRightPadding = 20;

    @ConfigEntry(key = "footer_button_width", category = C + "layout_sizing", group = C + "footer")
    @WidgetTypes.SliderInt(min = 20, max = 200)
    public int footerButtonWidth = 60;

    @ConfigEntry(key = "footer_button_height", category = C + "layout_sizing", group = C + "footer")
    @WidgetTypes.SliderInt(min = 5, max = 50)
    public int footerButtonHeight = 20;

    @ConfigEntry(key = "footer_padding_right", category = C + "layout_sizing", group = C + "footer")
    @WidgetTypes.SliderInt(min = 0, max = 100)
    public int footerPaddingRight = 10;

    @ConfigEntry(key = "footer_button_spacing", category = C + "layout_sizing", group = C + "footer")
    @WidgetTypes.SliderInt(min = 0, max = 50)
    public int footerButtonSpacing = 5;

    @ConfigEntry(key = "reset_icon_size", category = C + "layout_sizing", group = C + "reset_button")
    @WidgetTypes.SliderInt(min = 4, max = 32)
    public int resetIconSize = 14;

    @ConfigEntry(key = "tab_arrow_offset_x", category = C + "layout_sizing", group = C + "tab_arrow")
    @WidgetTypes.SliderInt(min = 0, max = 20)
    public int tabArrowOffsetX = 2;

    @ConfigEntry(key = "tab_arrow_offset_y", category = C + "layout_sizing", group = C + "tab_arrow")
    @WidgetTypes.SliderInt(min = 0, max = 20)
    public int tabArrowOffsetY = 3;

    @ConfigEntry(key = "tab_text_offset_x", category = C + "layout_sizing", group = C + "text_offsets")
    @WidgetTypes.SliderInt(min = 0, max = 100)
    public int tabTextOffsetX = 12;

    @ConfigEntry(key = "desc_title_spacing_y", category = C + "layout_sizing", group = C + "desc_offsets")
    @WidgetTypes.SliderInt(min = 0, max = 50)
    public int descTitleSpacingY = 12;

    // Tab List Layout Alignment & Arrows

    @ConfigEntry(key = "tab_arrow_downward_offset_y", category = C + "tab_alignment", group = C + "arrows")
    @WidgetTypes.SliderInt(min = -10, max = 20)
    public int tabArrowDownwardOffsetY = 3;

    @ConfigEntry(key = "tab_arrow_rightward_offset_x", category = C + "tab_alignment", group = C + "arrows")
    @WidgetTypes.SliderInt(min = -10, max = 20)
    public int tabArrowRightwardOffsetX = 1;

    @ConfigEntry(key = "tab_arrow_rightward_offset_y", category = C + "tab_alignment", group = C + "arrows")
    @WidgetTypes.SliderInt(min = -10, max = 20)
    public int tabArrowRightwardOffsetY = 4;

    @ConfigEntry(key = "tab_scissor_clip_padding_y", category = C + "tab_alignment", group = C + "clipping")
    @WidgetTypes.SliderInt(min = 0, max = 10)
    public int tabScissorClipPaddingY = 1;

    // Reset Button Overlays & Icon Tinting

    @ConfigEntry(key = "color_reset_button_bg_hover", category = C + "reset_visuals", group = C + "colors")
    @WidgetTypes.ColorPicker
    public int colorResetButtonBgHover = Colors.ALPHA_WHITE_27;

    @ConfigEntry(key = "color_reset_button_bg_default", category = C + "reset_visuals", group = C + "colors")
    @WidgetTypes.ColorPicker
    public int colorResetButtonBgDefault = Colors.ALPHA_BLACK_13;

    @ConfigEntry(key = "reset_icon_alpha_hover", category = C + "reset_visuals", group = C + "alphas")
    @WidgetTypes.SliderFloat(min = 0.0f, max = 1.0f, step = 0.05f)
    public float resetIconAlphaHover = 1.0f;

    @ConfigEntry(key = "reset_icon_alpha_default", category = C + "reset_visuals", group = C + "alphas")
    @WidgetTypes.SliderFloat(min = 0.0f, max = 1.0f, step = 0.05f)
    public float resetIconAlphaDefault = 0.8f;

    @ConfigEntry(key = "reset_icon_alpha_disabled", category = C + "reset_visuals", group = C + "alphas")
    @WidgetTypes.SliderFloat(min = 0.0f, max = 1.0f, step = 0.05f)
    public float resetIconAlphaDisabled = 0.3f;

    @ConfigEntry(key = "reset_icon_active_r", category = C + "reset_visuals", group = C + "active_tint")
    @WidgetTypes.SliderFloat(min = 0.0f, max = 1.0f, step = 0.05f)
    public float resetIconActiveR = 0.9f;

    @ConfigEntry(key = "reset_icon_active_g", category = C + "reset_visuals", group = C + "active_tint")
    @WidgetTypes.SliderFloat(min = 0.0f, max = 1.0f, step = 0.05f)
    public float resetIconActiveG = 0.3f;

    @ConfigEntry(key = "reset_icon_active_b", category = C + "reset_visuals", group = C + "active_tint")
    @WidgetTypes.SliderFloat(min = 0.0f, max = 1.0f, step = 0.05f)
    public float resetIconActiveB = 0.3f;

    @ConfigEntry(key = "reset_icon_inactive_r", category = C + "reset_visuals", group = C + "inactive_tint")
    @WidgetTypes.SliderFloat(min = 0.0f, max = 1.0f, step = 0.05f)
    public float resetIconInactiveR = 0.5f;

    @ConfigEntry(key = "reset_icon_inactive_g", category = C + "reset_visuals", group = C + "inactive_tint")
    @WidgetTypes.SliderFloat(min = 0.0f, max = 1.0f, step = 0.05f)
    public float resetIconInactiveG = 0.5f;

    @ConfigEntry(key = "reset_icon_inactive_b", category = C + "reset_visuals", group = C + "inactive_tint")
    @WidgetTypes.SliderFloat(min = 0.0f, max = 1.0f, step = 0.05f)
    public float resetIconInactiveB = 0.5f;

    // Screen Overlay Configurations

    @ConfigEntry(key = "color_overlay_dim_background", category = C + "screen_overlays", group = C + "dim")
    @WidgetTypes.ColorPicker
    public int colorOverlayDimBackground = Colors.ALPHA_BLACK_40;

    // Action Button Widget Sizing & Animation

    @ConfigEntry(key = "action_button_padding", category = C + "action_button_widgets", group = C + "sizing")
    @WidgetTypes.SliderInt(min = 0, max = 20)
    public int actionButtonPadding = 4;

    @ConfigEntry(key = "action_button_border_thickness", category = C + "action_button_widgets", group = C + "sizing")
    @WidgetTypes.SliderInt(min = 0, max = 5)
    public int actionButtonBorderThickness = 1;

    @ConfigEntry(key = "action_button_text_offset_y", category = C + "action_button_widgets", group = C + "sizing")
    @WidgetTypes.SliderInt(min = -10, max = 10)
    public int actionButtonTextOffsetY = 1;

    @ConfigEntry(key = "action_button_scroll_start_wait", category = C + "action_button_widgets", group = C + "animation")
    @WidgetTypes.SliderInt(min = 0, max = 5000, step = 100)
    public long actionButtonScrollStartWait = 1000L;

    @ConfigEntry(key = "action_button_scroll_speed", category = C + "action_button_widgets", group = C + "animation")
    @WidgetTypes.SliderInt(min = 1, max = 100)
    public long actionButtonScrollSpeed = 24L;

    @ConfigEntry(key = "action_button_scroll_end_wait", category = C + "action_button_widgets", group = C + "animation")
    @WidgetTypes.SliderInt(min = 0, max = 5000, step = 100)
    public long actionButtonScrollEndWait = 1500L;

    // Capsule Toggle Widget Sizing

    @ConfigEntry(key = "capsule_toggle_width", category = C + "widget_capsule", group = C + "sizing")
    @WidgetTypes.SliderInt(min = 10, max = 100)
    public int capsuleToggleWidth = 30;

    @ConfigEntry(key = "capsule_toggle_height", category = C + "widget_capsule", group = C + "sizing")
    @WidgetTypes.SliderInt(min = 5, max = 50)
    public int capsuleToggleHeight = 15;

    @ConfigEntry(key = "capsule_toggle_knob_size_factor", category = C + "widget_capsule", group = C + "sizing")
    @WidgetTypes.SliderDouble(min = 0.1, max = 1.0, step = 0.05)
    public double capsuleToggleKnobSizeFactor = 0.85;

    // Toggle Action Button Widget Sizing

    @ConfigEntry(key = "toggle_button_width", category = C + "widget_toggle_action", group = C + "sizing")
    @WidgetTypes.SliderInt(min = 10, max = 150)
    public int toggleButtonWidth = 44;

    @ConfigEntry(key = "toggle_button_height", category = C + "widget_toggle_action", group = C + "sizing")
    @WidgetTypes.SliderInt(min = 5, max = 50)
    public int toggleButtonHeight = 18;

    // Toggle Box Button Widget Sizing

    @ConfigEntry(key = "box_toggle_width", category = C + "widget_toggle_box", group = C + "sizing")
    @WidgetTypes.SliderInt(min = 5, max = 40)
    public int boxToggleWidth = 14;

    @ConfigEntry(key = "box_toggle_height", category = C + "widget_toggle_box", group = C + "sizing")
    @WidgetTypes.SliderInt(min = 5, max = 40)
    public int boxToggleHeight = 14;

    @ConfigEntry(key = "box_toggle_outline_thickness", category = C + "widget_toggle_box", group = C + "sizing")
    @WidgetTypes.SliderInt(min = 0, max = 5)
    public int boxToggleOutlineThickness = 1;

    @ConfigEntry(key = "box_toggle_inner_padding", category = C + "widget_toggle_box", group = C + "sizing")
    @WidgetTypes.SliderInt(min = 0, max = 10)
    public int boxToggleInnerPadding = 2;

    // Cycling Button Widget Sizing

    @ConfigEntry(key = "cycling_button_width_padding", category = C + "widget_cycling", group = C + "sizing")
    @WidgetTypes.SliderInt(min = 0, max = 100)
    public int cyclingButtonWidthPadding = 20;

    @ConfigEntry(key = "cycling_button_height", category = C + "widget_cycling", group = C + "sizing")
    @WidgetTypes.SliderInt(min = 5, max = 50)
    public int cyclingButtonHeight = 18;

    // Item Input Field Widget Sizing

    @ConfigEntry(key = "item_input_max_suggestions", category = C + "widget_item_input", group = C + "suggestions")
    @WidgetTypes.SliderInt(min = 1, max = 20)
    public int itemInputMaxSuggestions = 7;

    @ConfigEntry(key = "item_input_icon_size", category = C + "widget_item_input", group = C + "sizing")
    @WidgetTypes.SliderInt(min = 4, max = 32)
    public int itemInputIconSize = 16;

    @ConfigEntry(key = "item_input_row_height", category = C + "widget_item_input", group = C + "sizing")
    @WidgetTypes.SliderInt(min = 5, max = 50)
    public int itemInputRowHeight = 18;

    @ConfigEntry(key = "item_input_left_text_padding", category = C + "widget_item_input", group = C + "padding")
    @WidgetTypes.SliderInt(min = 0, max = 300)
    public int itemInputLeftTextPadding = 150;

    @ConfigEntry(key = "item_input_min_text_field_width", category = C + "widget_item_input", group = C + "sizing")
    @WidgetTypes.SliderInt(min = 10, max = 200)
    public int itemInputMinTextFieldWidth = 50;

    @ConfigEntry(key = "item_input_icon_spacing", category = C + "widget_item_input", group = C + "spacing")
    @WidgetTypes.SliderInt(min = 0, max = 20)
    public int itemInputIconSpacing = 4;

    @ConfigEntry(key = "item_input_suggestions_offset_y", category = C + "widget_item_input", group = C + "suggestions")
    @WidgetTypes.SliderInt(min = -20, max = 50)
    public int itemInputSuggestionsOffsetY = 4;

    @ConfigEntry(key = "item_input_suggestion_icon_spacing", category = C + "widget_item_input", group = C + "suggestions")
    @WidgetTypes.SliderInt(min = 0, max = 20)
    public int itemInputSuggestionIconSpacing = 6;

    @ConfigEntry(key = "item_input_max_length", category = C + "widget_item_input", group = C + "constraints")
    @WidgetTypes.SliderInt(min = 1, max = 256)
    public int itemInputMaxLength = 64;

    // Keybind Button Widget Sizing

    @ConfigEntry(key = "keybind_button_max_width", category = C + "widget_keybind", group = C + "sizing")
    @WidgetTypes.SliderInt(min = 20, max = 300)
    public int keybindButtonMaxWidth = 120;

    @ConfigEntry(key = "keybind_button_width_padding", category = C + "widget_keybind", group = C + "sizing")
    @WidgetTypes.SliderInt(min = 0, max = 100)
    public int keybindButtonWidthPadding = 20;

    @ConfigEntry(key = "keybind_button_height", category = C + "widget_keybind", group = C + "sizing")
    @WidgetTypes.SliderInt(min = 5, max = 50)
    public int keybindButtonHeight = 18;

    // List Widget Sizing

    @ConfigEntry(key = "list_entry_height", category = C + "widget_list", group = C + "sizing")
    @WidgetTypes.SliderInt(min = 4, max = 40)
    public int listEntryHeight = 12;

    // Slider Widget Sizing

    @ConfigEntry(key = "slider_width", category = C + "widget_slider", group = C + "sizing")
    @WidgetTypes.SliderInt(min = 10, max = 300)
    public int sliderWidth = 115;

    @ConfigEntry(key = "slider_height", category = C + "widget_slider", group = C + "sizing")
    @WidgetTypes.SliderInt(min = 1, max = 30)
    public int sliderHeight = 4;

    @ConfigEntry(key = "slider_knob_size", category = C + "widget_slider", group = C + "sizing")
    @WidgetTypes.SliderInt(min = 2, max = 40)
    public int sliderKnobSize = 12;

    @ConfigEntry(key = "slider_text_padding", category = C + "widget_slider", group = C + "padding")
    @WidgetTypes.SliderInt(min = 0, max = 50)
    public int sliderTextPadding = 8;

    // String Input Field Widget Sizing

    @ConfigEntry(key = "string_input_max_length", category = C + "widget_string_input", group = C + "constraints")
    @WidgetTypes.SliderInt(min = 1, max = 512)
    public int stringInputMaxLength = 128;

    @ConfigEntry(key = "string_input_left_text_padding", category = C + "widget_string_input", group = C + "padding")
    @WidgetTypes.SliderInt(min = 0, max = 300)
    public int stringInputLeftTextPadding = 150;

    @ConfigEntry(key = "string_input_min_text_field_width", category = C + "widget_string_input", group = C + "sizing")
    @WidgetTypes.SliderInt(min = 10, max = 300)
    public int stringInputMinTextFieldWidth = 50;

    @ConfigEntry(key = "string_input_text_offset_y", category = C + "widget_string_input", group = C + "sizing")
    @WidgetTypes.SliderInt(min = -10, max = 10)
    public int stringInputTextOffsetY = 1;

    // Color Picker Widget Sizing & Logic

    @ConfigEntry(key = "color_picker_square_size", category = C + "widget_color_picker", group = C + "sizing")
    @WidgetTypes.SliderInt(min = 4, max = 40)
    public int colorPickerSquareSize = 14;

    @ConfigEntry(key = "color_picker_text_width", category = C + "widget_color_picker", group = C + "sizing")
    @WidgetTypes.SliderInt(min = 10, max = 150)
    public int colorPickerTextWidth = 55;

    @ConfigEntry(key = "color_picker_square_right_padding", category = C + "widget_color_picker", group = C + "padding")
    @WidgetTypes.SliderInt(min = 0, max = 30)
    public int colorPickerSquareRightPadding = 5;

    @ConfigEntry(key = "color_picker_text_offset_y", category = C + "widget_color_picker", group = C + "sizing")
    @WidgetTypes.SliderInt(min = -10, max = 10)
    public int colorPickerTextOffsetY = 1;

    @ConfigEntry(key = "color_picker_max_length", category = C + "widget_color_picker", group = C + "constraints")
    @WidgetTypes.SliderInt(min = 1, max = 16)
    public int colorPickerMaxLength = 8;

    @ConfigEntry(key = "color_picker_min_alpha_clamp", category = C + "widget_color_picker", group = C + "constraints")
    @WidgetTypes.SliderInt(min = 0, max = 255)
    public int colorPickerMinAlphaClamp = 0x44;

    @ConfigEntry(key = "color_picker_min_rgb_clamp", category = C + "widget_color_picker", group = C + "constraints")
    @WidgetTypes.SliderInt(min = 0, max = 255)
    public int colorPickerMinRGBClamp = 0x44;

    @ConfigEntry(key = "color_picker_max_rgb_clamp", category = C + "widget_color_picker", group = C + "constraints")
    @WidgetTypes.SliderInt(min = 0, max = 255)
    public int colorPickerMaxRGBClamp = 0xDD;

    // Color Picker Overlay Sizing

    @ConfigEntry(key = "picker_dialog_width_percent", category = C + "color_picker_overlay", group = C + "dialog_proportions")
    @WidgetTypes.SliderDouble(min = 0.1, max = 1.0, step = 0.01)
    public double pickerDialogWidthPercent = 0.35;

    @ConfigEntry(key = "picker_dialog_height_percent", category = C + "color_picker_overlay", group = C + "dialog_proportions")
    @WidgetTypes.SliderDouble(min = 0.1, max = 1.0, step = 0.01)
    public double pickerDialogHeightPercent = 0.55;

    @ConfigEntry(key = "picker_dialog_min_width", category = C + "color_picker_overlay", group = C + "dialog_constraints")
    @WidgetTypes.SliderInt(min = 50, max = 1000)
    public int pickerDialogMinWidth = 290;

    @ConfigEntry(key = "picker_dialog_min_height", category = C + "color_picker_overlay", group = C + "dialog_constraints")
    @WidgetTypes.SliderInt(min = 50, max = 1000)
    public int pickerDialogMinHeight = 220;

    @ConfigEntry(key = "picker_dialog_max_width", category = C + "color_picker_overlay", group = C + "dialog_constraints")
    @WidgetTypes.SliderInt(min = 100, max = 2000)
    public int pickerDialogMaxWidth = 480;

    @ConfigEntry(key = "picker_dialog_max_height", category = C + "color_picker_overlay", group = C + "dialog_constraints")
    @WidgetTypes.SliderInt(min = 100, max = 2000)
    public int pickerDialogMaxHeight = 380;

    @ConfigEntry(key = "picker_padding_inner", category = C + "color_picker_overlay", group = C + "dialog_padding")
    @WidgetTypes.SliderInt(min = 0, max = 100)
    public int pickerPaddingInner = 16;

    @ConfigEntry(key = "picker_slider_width", category = C + "color_picker_overlay", group = C + "dialog_widgets")
    @WidgetTypes.SliderInt(min = 1, max = 30)
    public int pickerSliderWidth = 6;

    @ConfigEntry(key = "picker_slider_spacing", category = C + "color_picker_overlay", group = C + "dialog_widgets")
    @WidgetTypes.SliderInt(min = 0, max = 50)
    public int pickerSliderSpacing = 10;

    @ConfigEntry(key = "picker_rgb_slider_height", category = C + "color_picker_overlay", group = C + "dialog_widgets")
    @WidgetTypes.SliderInt(min = 1, max = 30)
    public int pickerRgbSliderHeight = 6;

    @ConfigEntry(key = "picker_swat_width", category = C + "color_picker_overlay", group = C + "dialog_widgets")
    @WidgetTypes.SliderInt(min = 5, max = 100)
    public int pickerSwatWidth = 32;

    @ConfigEntry(key = "picker_swat_height", category = C + "color_picker_overlay", group = C + "dialog_widgets")
    @WidgetTypes.SliderInt(min = 5, max = 100)
    public int pickerSwatHeight = 24;

    @ConfigEntry(key = "picker_palette_box_size", category = C + "color_picker_overlay", group = C + "dialog_widgets")
    @WidgetTypes.SliderInt(min = 2, max = 40)
    public int pickerPaletteBoxSize = 12;

    @ConfigEntry(key = "picker_palette_box_spacing", category = C + "color_picker_overlay", group = C + "dialog_widgets")
    @WidgetTypes.SliderInt(min = 0, max = 20)
    public int pickerPaletteBoxSpacing = 4;

    @ConfigEntry(key = "picker_max_palette_colors", category = C + "color_picker_overlay", group = C + "dialog_constraints")
    @WidgetTypes.SliderInt(min = 1, max = 50)
    public int pickerMaxPaletteColors = 8;

    @ConfigEntry(key = "picker_title_offset_y", category = C + "color_picker_overlay", group = C + "dialog_offsets")
    @WidgetTypes.SliderInt(min = 0, max = 200)
    public int pickerTitleOffsetY = 36;

    @ConfigEntry(key = "picker_swat_offset_y", category = C + "color_picker_overlay", group = C + "dialog_offsets")
    @WidgetTypes.SliderInt(min = 0, max = 200)
    public int pickerSwatOffsetY = 12;

    @ConfigEntry(key = "picker_swat_spacing_x", category = C + "color_picker_overlay", group = C + "dialog_offsets")
    @WidgetTypes.SliderInt(min = 0, max = 100)
    public int pickerSwatSpacingX = 12;

    @ConfigEntry(key = "picker_palette_label_offset_y", category = C + "color_picker_overlay", group = C + "dialog_offsets")
    @WidgetTypes.SliderInt(min = 0, max = 100)
    public int pickerPaletteLabelOffsetY = 20;

    @ConfigEntry(key = "picker_recent_label_offset_y", category = C + "color_picker_overlay", group = C + "dialog_offsets")
    @WidgetTypes.SliderInt(min = 0, max = 100)
    public int pickerRecentLabelOffsetY = 22;

    @ConfigEntry(key = "picker_button_width", category = C + "color_picker_overlay", group = C + "dialog_widgets")
    @WidgetTypes.SliderInt(min = 10, max = 150)
    public int pickerButtonWidth = 55;

    @ConfigEntry(key = "picker_button_height", category = C + "color_picker_overlay", group = C + "dialog_widgets")
    @WidgetTypes.SliderInt(min = 5, max = 50)
    public int pickerButtonHeight = 18;

    @ConfigEntry(key = "picker_x_btn_padding", category = C + "color_picker_overlay", group = C + "dialog_padding")
    @WidgetTypes.SliderInt(min = 0, max = 20)
    public int pickerXBtnPadding = 4;

    @ConfigEntry(key = "picker_metrics_offset_y", category = C + "color_picker_overlay", group = C + "dialog_offsets")
    @WidgetTypes.SliderInt(min = 0, max = 100)
    public int pickerMetricsOffsetY = 8;

    @ConfigEntry(key = "picker_metrics_spacing_y", category = C + "color_picker_overlay", group = C + "dialog_offsets")
    @WidgetTypes.SliderInt(min = 0, max = 50)
    public int pickerMetricsSpacingY = 14;

    @ConfigEntry(key = "picker_palette_text_spacing_y", category = C + "color_picker_overlay", group = C + "dialog_offsets")
    @WidgetTypes.SliderInt(min = 0, max = 50)
    public int pickerPaletteTextSpacingY = 12;

    @ConfigEntry(key = "picker_clear_btn_padding_x", category = C + "color_picker_overlay", group = C + "dialog_padding")
    @WidgetTypes.SliderInt(min = 0, max = 20)
    public int pickerClearBtnPaddingX = 4;

    @ConfigEntry(key = "picker_clear_btn_padding_y", category = C + "color_picker_overlay", group = C + "dialog_padding")
    @WidgetTypes.SliderInt(min = 0, max = 20)
    public int pickerClearBtnPaddingY = 2;

    @ConfigEntry(key = "picker_slider_indicator_size", category = C + "color_picker_overlay", group = C + "dialog_widgets")
    @WidgetTypes.SliderInt(min = 1, max = 25)
    public int pickerSliderIndicatorSize = 12;

    @ConfigEntry(key = "picker_sb_space_indicator_size", category = C + "color_picker_overlay", group = C + "dialog_widgets")
    @WidgetTypes.SliderInt(min = 1, max = 25)
    public int pickerSbSpaceIndicatorSize = 4;

    @ConfigEntry(key = "picker_mode_toggle_width", category = C + "color_picker_overlay", group = C + "dialog_widgets")
    @WidgetTypes.SliderInt(min = 20, max = 150)
    public int pickerModeToggleWidth = 64;

    @ConfigEntry(key = "picker_mode_toggle_height", category = C + "color_picker_overlay", group = C + "dialog_widgets")
    @WidgetTypes.SliderInt(min = 5, max = 50)
    public int pickerModeToggleHeight = 14;

    @ConfigEntry(key = "confirm_dialog_width", category = C + "confirm_dialog", group = C + "constraints")
    @WidgetTypes.SliderInt(min = 50, max = 1000)
    public int confirmDialogWidth = 320;

    @ConfigEntry(key = "confirm_dialog_height", category = C + "confirm_dialog", group = C + "constraints")
    @WidgetTypes.SliderInt(min = 30, max = 1000)
    public int confirmDialogHeight = 120;

    @ConfigEntry(key = "confirm_padding_inner", category = C + "confirm_dialog", group = C + "padding")
    @WidgetTypes.SliderInt(min = 0, max = 100)
    public int confirmPaddingInner = 16;

    @ConfigEntry(key = "confirm_button_width", category = C + "confirm_dialog", group = C + "widgets")
    @WidgetTypes.SliderInt(min = 10, max = 200)
    public int confirmButtonWidth = 60;

    @ConfigEntry(key = "confirm_button_height", category = C + "confirm_dialog", group = C + "widgets")
    @WidgetTypes.SliderInt(min = 5, max = 50)
    public int confirmButtonHeight = 20;

    @ConfigEntry(key = "confirm_button_spacing", category = C + "confirm_dialog", group = C + "widgets")
    @WidgetTypes.SliderInt(min = 0, max = 100)
    public int confirmButtonSpacing = 12;

    // Rounded

    @ConfigEntry(key = "rounded_capsule", category = C + "aesthetic_options", group = C + "rounding")
    @WidgetTypes.Toggle(WidgetTypes.Toggle.Style.BOX)
    public boolean roundedCapsule = true;

    @ConfigEntry(key = "rounded_slider", category = C + "aesthetic_options", group = C + "rounding")
    @WidgetTypes.Toggle(WidgetTypes.Toggle.Style.BOX)
    public boolean roundedSlider = true;

    // Shadow

    @ConfigEntry(key = "action_button_shadow", category = C + "aesthetic_options", group = C + "shadows")
    @WidgetTypes.Toggle(WidgetTypes.Toggle.Style.BOX)
    public boolean actionButtonShadow = true;

    @ConfigEntry(key = "toggle_button_shadow", category = C + "aesthetic_options", group = C + "shadows")
    @WidgetTypes.Toggle(WidgetTypes.Toggle.Style.BOX)
    public boolean toggleButtonShadow = true;

    // Color Aesthetic

    @ConfigEntry(key = "color_picker_text_use_color", category = C + "aesthetic_options", group = C + "colors")
    @WidgetTypes.Toggle(WidgetTypes.Toggle.Style.BOX)
    public boolean colorPickerTextUseColor = false;

    // Color tokens

    @ConfigEntry(key = "color_screen_base_background", category = C + "theme_colors", group = C + "panels")
    @WidgetTypes.ColorPicker
    public int colorScreenBaseBackground = Colors.ALPHA_BLACK_53;

    @ConfigEntry(key = "color_header_background", category = C + "theme_colors", group = C + "panels")
    @WidgetTypes.ColorPicker
    public int colorHeaderBackground = Colors.ALPHA_WHITE_08;

    @ConfigEntry(key = "color_footer_background", category = C + "theme_colors", group = C + "panels")
    @WidgetTypes.ColorPicker
    public int colorFooterBackground = Colors.ALPHA_BLACK_40;

    @ConfigEntry(key = "color_tab_panel_background", category = C + "theme_colors", group = C + "panels")
    @WidgetTypes.ColorPicker
    public int colorTabPanelBackground = Colors.ALPHA_BLACK_20;

    @ConfigEntry(key = "color_option_panel_background", category = C + "theme_colors", group = C + "panels")
    @WidgetTypes.ColorPicker
    public int colorOptionPanelBackground = Colors.ALPHA_BLACK_26;

    @ConfigEntry(key = "color_description_panel_background", category = C + "theme_colors", group = C + "panels")
    @WidgetTypes.ColorPicker
    public int colorDescriptionPanelBackground = Colors.ALPHA_BLACK_33;

    @ConfigEntry(key = "color_text_primary", category = C + "theme_colors", group = C + "text")
    @WidgetTypes.ColorPicker
    public int colorTextPrimary = Colors.WHITE;

    @ConfigEntry(key = "color_text_secondary", category = C + "theme_colors", group = C + "text")
    @WidgetTypes.ColorPicker
    public int colorTextSecondary = Colors.TEXT_GRAY;

    @ConfigEntry(key = "color_text_muted", category = C + "theme_colors", group = C + "text")
    @WidgetTypes.ColorPicker
    public int colorTextMuted = Colors.OFF_WHITE;

    @ConfigEntry(key = "color_text_disabled", category = C + "theme_colors", group = C + "text")
    @WidgetTypes.ColorPicker
    public int colorTextDisabled = Colors.ALPHA_WHITE_53;

    @ConfigEntry(key = "color_item_selected_text", category = C + "theme_colors", group = C + "list_states")
    @WidgetTypes.ColorPicker
    public int colorItemSelectedText = Colors.WHITE;

    @ConfigEntry(key = "color_item_unselected_text", category = C + "theme_colors", group = C + "list_states")
    @WidgetTypes.ColorPicker
    public int colorItemUnselectedText = Colors.ALPHA_WHITE_60;

    @ConfigEntry(key = "color_item_hover_background", category = C + "theme_colors", group = C + "list_states")
    @WidgetTypes.ColorPicker
    public int colorItemHoverBackground = Colors.ALPHA_WHITE_13;

    @ConfigEntry(key = "color_item_selected_background", category = C + "theme_colors", group = C + "list_states")
    @WidgetTypes.ColorPicker
    public int colorItemSelectedBackground = Colors.ALPHA_WHITE_27;

    @ConfigEntry(key = "color_scroll_bar_track", category = C + "theme_colors", group = C + "scrollbar")
    @WidgetTypes.ColorPicker
    public int colorScrollBarTrack = Colors.ALPHA_BLACK_53;

    @ConfigEntry(key = "color_scroll_bar_thumb", category = C + "theme_colors", group = C + "scrollbar")
    @WidgetTypes.ColorPicker
    public int colorScrollBarThumb = Colors.OFF_WHITE;

    @ConfigEntry(key = "color_button_border", category = C + "theme_colors", group = C + "standard_buttons")
    @WidgetTypes.ColorPicker
    public int colorButtonBorder = Colors.BORDER_BLACK;

    @ConfigEntry(key = "color_button_background", category = C + "theme_colors", group = C + "standard_buttons")
    @WidgetTypes.ColorPicker
    public int colorButtonBackground = Colors.GRAY_0;

    @ConfigEntry(key = "color_button_background_hover", category = C + "theme_colors", group = C + "standard_buttons")
    @WidgetTypes.ColorPicker
    public int colorButtonBackgroundHover = Colors.GRAY_1;

    @ConfigEntry(key = "color_button_background_disabled", category = C + "theme_colors", group = C + "standard_buttons")
    @WidgetTypes.ColorPicker
    public int colorButtonBackgroundDisabled = Colors.ALPHA_BLACK_53;

    @ConfigEntry(key = "color_button_text", category = C + "theme_colors", group = C + "standard_buttons")
    @WidgetTypes.ColorPicker
    public int colorButtonText = Colors.WHITE;

    @ConfigEntry(key = "color_button_text_disabled", category = C + "theme_colors", group = C + "standard_buttons")
    @WidgetTypes.ColorPicker
    public int colorButtonTextDisabled = Colors.MUTED_GRAY;

    // Color Picker Widget Colors

    @ConfigEntry(key = "color_color_picker_border_hover", category = C + "widget_colors_picker", group = C + "borders")
    @WidgetTypes.ColorPicker
    public int colorColorPickerBorderHover = Colors.WHITE;

    @ConfigEntry(key = "color_color_picker_border_default", category = C + "widget_colors_picker", group = C + "borders")
    @WidgetTypes.ColorPicker
    public int colorColorPickerBorderDefault = Colors.ALPHA_WHITE_27;

    @ConfigEntry(key = "color_color_picker_hash_text", category = C + "widget_colors_picker", group = C + "labels")
    @WidgetTypes.ColorPicker
    public int colorColorPickerHashText = Colors.OFF_WHITE;

    // Item Input Field Widget Colors

    @ConfigEntry(key = "color_item_input_suggestion_hover", category = C + "widget_colors_item_input", group = C + "suggestions")
    @WidgetTypes.ColorPicker
    public int colorItemInputSuggestionHover = Colors.INDIGO;

    @ConfigEntry(key = "color_item_input_suggestion_bg", category = C + "widget_colors_item_input", group = C + "suggestions")
    @WidgetTypes.ColorPicker
    public int colorItemInputSuggestionBg = Colors.ALPHA_BLACK_53;

    @ConfigEntry(key = "color_item_input_suggestion_text_hover", category = C + "widget_colors_item_input", group = C + "suggestions")
    @WidgetTypes.ColorPicker
    public int colorItemInputSuggestionTextHover = Colors.WHITE;

    @ConfigEntry(key = "color_item_input_suggestion_text_default", category = C + "widget_colors_item_input", group = C + "suggestions")
    @WidgetTypes.ColorPicker
    public int colorItemInputSuggestionTextDefault = Colors.OFF_WHITE;

    // Boolean Toggle Widget Colors

    @ConfigEntry(key = "color_toggle_bg_off", category = C + "widget_colors_toggle", group = C + "toggle_bg")
    @WidgetTypes.ColorPicker
    public int colorToggleBgOff = Colors.ALPHA_WHITE_08;

    @ConfigEntry(key = "color_toggle_bg_on", category = C + "widget_colors_toggle", group = C + "toggle_bg")
    @WidgetTypes.ColorPicker
    public int colorToggleBgOn = Colors.GREEN1;

    @ConfigEntry(key = "color_toggle_knob", category = C + "widget_colors_toggle", group = C + "knob")
    @WidgetTypes.ColorPicker
    public int colorToggleKnob = Colors.WHITE;

    @ConfigEntry(key = "color_toggle_bg_off_hover", category = C + "widget_colors_toggle", group = C + "toggle_bg")
    @WidgetTypes.ColorPicker
    public int colorToggleBgOffHover = Colors.MIDDLE_GRAY;

    @ConfigEntry(key = "color_toggle_bg_on_hover", category = C + "widget_colors_toggle", group = C + "toggle_bg")
    @WidgetTypes.ColorPicker
    public int colorToggleBgOnHover = Colors.GREEN2;

    @ConfigEntry(key = "color_toggle_box", category = C + "widget_colors_toggle", group = C + "toggle_box")
    @WidgetTypes.ColorPicker
    public int colorToggleBox = Colors.LIGHT_GRAY;

    @ConfigEntry(key = "color_toggle_box_hover", category = C + "widget_colors_toggle", group = C + "toggle_box")
    @WidgetTypes.ColorPicker
    public int colorToggleBoxHover = Colors.WHITE;

    // Slider Widget Colors

    @ConfigEntry(key = "color_slider_track", category = C + "widget_colors_slider", group = C + "tracks")
    @WidgetTypes.ColorPicker
    public int colorSliderTrack = Colors.ALPHA_WHITE_08;

    @ConfigEntry(key = "color_slider_progress", category = C + "widget_colors_slider", group = C + "tracks")
    @WidgetTypes.ColorPicker
    public int colorSliderProgress = Colors.INDIGO;

    @ConfigEntry(key = "color_slider_knob", category = C + "widget_colors_slider", group = C + "knobs")
    @WidgetTypes.ColorPicker
    public int colorSliderKnob = Colors.WHITE;

    @ConfigEntry(key = "color_slider_text", category = C + "widget_colors_slider", group = C + "labels")
    @WidgetTypes.ColorPicker
    public int colorSliderText = Colors.OFF_WHITE;

    // Engine

    @ConfigEntry(key = "engine_min_tab_area_width", category = C + "layout_engine", group = C + "tab_limits")
    @WidgetTypes.SliderInt(min = 10, max = 300)
    public int engineMinTabAreaWidth = 60;

    @ConfigEntry(key = "engine_min_option_area_width", category = C + "layout_engine", group = C + "option_limits")
    @WidgetTypes.SliderInt(min = 50, max = 500)
    public int engineMinOptionAreaWidth = 150;

    @ConfigEntry(key = "engine_min_desc_area_width_threshold", category = C + "layout_engine", group = C + "desc_limits")
    @WidgetTypes.SliderInt(min = 10, max = 200)
    public int engineMinDescAreaWidthThreshold = 50;

    @ConfigEntry(key = "engine_track_padding_y", category = C + "layout_engine", group = C + "paddings")
    @WidgetTypes.SliderInt(min = 0, max = 10)
    public int engineTrackPaddingY = 1;

    @ConfigEntry(key = "engine_text_height_offset", category = C + "layout_engine", group = C + "offsets")
    @WidgetTypes.SliderInt(min = -10, max = 10)
    public int engineTextHeightOffset = 1;

    @ConfigEntry(key = "engine_reset_button_offset_x", category = C + "layout_engine", group = C + "offsets")
    @WidgetTypes.SliderInt(min = -10, max = 50)
    public int engineResetButtonOffsetX = 4;

    @ConfigEntry(key = "engine_mode_toggle_spacing_x", category = C + "layout_engine", group = C + "spacings")
    @WidgetTypes.SliderInt(min = 0, max = 50)
    public int engineModeToggleSpacingX = 6;

    @ConfigEntry(key = "engine_top_section_height_offset", category = C + "layout_engine", group = C + "offsets")
    @WidgetTypes.SliderInt(min = 0, max = 200)
    public int engineTopSectionHeightOffset = 65;

    @ConfigEntry(key = "engine_min_sb_size", category = C + "layout_engine", group = C + "scrollbar_limits")
    @WidgetTypes.SliderInt(min = 10, max = 200)
    public int engineMinSbSize = 60;

    @ConfigEntry(key = "engine_max_sb_size", category = C + "layout_engine", group = C + "scrollbar_limits")
    @WidgetTypes.SliderInt(min = 10, max = 500)
    public int engineMaxSbSize = 140;

    @ConfigEntry(key = "engine_swat_text_spacing_y", category = C + "layout_engine", group = C + "spacings")
    @WidgetTypes.SliderInt(min = 0, max = 20)
    public int engineSwatTextSpacingY = 4;

    @ConfigEntry(key = "engine_min_palette_box_spacing", category = C + "layout_engine", group = C + "spacings")
    @WidgetTypes.SliderInt(min = 0, max = 10)
    public int engineMinPaletteBoxSpacing = 2;

    @ConfigEntry(key = "engine_action_button_spacing_y", category = C + "layout_engine", group = C + "spacings")
    @WidgetTypes.SliderInt(min = 0, max = 20)
    public int engineActionButtonSpacingY = 6;

    @ConfigEntry(key = "engine_action_button_spacing_x", category = C + "layout_engine", group = C + "spacings")
    @WidgetTypes.SliderInt(min = 0, max = 20)
    public int engineActionButtonSpacingX = 6;

    @ConfigEntry(key = "engine_clear_button_spacing_x", category = C + "layout_engine", group = C + "spacings")
    @WidgetTypes.SliderInt(min = 0, max = 20)
    public int engineClearButtonSpacingX = 6;

    @ConfigEntry(key = "engine_clear_button_spacing_y", category = C + "layout_engine", group = C + "spacings")
    @WidgetTypes.SliderInt(min = 0, max = 10)
    public int engineClearButtonSpacingY = 2;

    public static LayoutConfig createDefault() {
        return new LayoutConfig();
    }
}
