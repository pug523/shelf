package com.pug523.shelf.ui.component;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import com.pug523.shelf.common.compat.*;
import com.pug523.shelf.ui.Widget;
import com.pug523.shelf.ui.screen.ConfigScreen;
import com.pug523.shelf.ui.screen.controller.OverlayController;
import com.pug523.shelf.ui.component.overlay.DropdownOverlay;
import com.pug523.shelf.core.geometry.MousePos;
import com.pug523.shelf.core.geometry.Rect;
import com.pug523.shelf.ui.element.RectangleBox;
import com.pug523.shelf.ui.element.Label;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// TODO: fix after DropdownOverlay fixed (or refactor to not to use Overlay, use new way to render dropdown)
public class TextInputFieldWidget<T> implements Widget {
    private final Predicate<String> filter;
    private final Predicate<String> validator;
    private final Consumer<String> onConfirm;
    private final Function<String, Component> textFormatter;
    private final Function<T, String> suggestionIdExtractor;
    private final SuggestionIconRenderer<T> suggestionIconRenderer;
    private final Consumer<T> onSuggestionSelected;
    private final String defaultValue;
    private final Component hint;

    private final Font font;
    private final EditBoxCompat editBox;

    private final List<T> suggestions = new ArrayList<>();
    private boolean showSuggestions = false;

    private final Config config;

    public static class Config {
        public boolean rightAlign = false;
        public boolean alwaysUnderlined = false;
        public int secondaryTextColor = 0xFFFFFFFF;
        public int mutedTextColor = 0xFFAAAAAA;
        public int hintColor = 0xFF666666;
        public int highlightColor = 0x800000FF;
        public int itemInputSuggestionsOffsetY = 2;
        public int itemInputRowHeight = 12;
        public int itemInputMaxSuggestions = 5;
        public boolean suggestionRightAlign = false;
    }

    public TextInputFieldWidget(Predicate<String> filter, Predicate<String> validator, Consumer<String> onConfirm,
            Function<String, Component> textFormatter, Function<T, String> suggestionIdExtractor,
            SuggestionIconRenderer<T> suggestionIconRenderer, Consumer<T> onSuggestionSelected, String defaultValue,
            Component hint, Component narration, Config config) {
        this.filter = filter;
        this.validator = validator;
        this.onConfirm = onConfirm;
        this.textFormatter = textFormatter;
        this.suggestionIdExtractor = suggestionIdExtractor;
        this.suggestionIconRenderer = suggestionIconRenderer;
        this.onSuggestionSelected = onSuggestionSelected;
        this.defaultValue = defaultValue;
        this.hint = hint;
        this.config = config;

        this.font = Minecraft.getInstance().font;
        this.editBox = new EditBoxCompat(this.font, new Rect(0, 0, 0, 0), narration);
        rebuildEditBox();
    }

    public Config config() {
        return config;
    }

    public void text(String text) {
        EditBox e = editBox.editBox;
        if (!text.equals(text())) {
            e.setValue(text);
            if (config.rightAlign) {
                e.setCursorPosition(text.length());
                e.setHighlightPos(text.length());
                editBox.moveCursor(0);
            }
        }
    }

    public String text() {
        return editBox.editBox.getValue();
    }

    public boolean isFocused() {
        return editBox.editBox.isFocused();
    }

    public void setFocused(boolean focus) {
        editBox.setFocused(focus);

        if (!focus) {
            if (onConfirm != null) {
                onConfirm.accept(text());
            }

            if (config.rightAlign) {
                editBox.editBox.setCursorPosition(text().length());
                editBox.moveCursor(0);
            } else {
                editBox.editBox.setCursorPosition(0);
                editBox.moveCursor(0);
            }
        }
    }

    private void rebuildEditBox() {
        EditBox e = editBox.editBox;
        e.setValue(defaultValue);
        editBox.hint(hint);
        editBox.textShadow(false);
        if (textFormatter != null) {
            editBox.addFormatter((text, offset) -> {
                Component formatted = textFormatter.apply(text);
                return formatted != null ? formatted.getVisualOrderText() : null;
            });
        }

        e.setResponder(this.onConfirm);
    }

    private boolean shouldRenderHint() {
        return hint != null && text().isEmpty() && !isFocused();
    }

    private void renderHighlightOverlay(Font font, GuiCompat gui, int x, int y, int cursorPos, int highlightPos) {
        int startIdx = Math.min(cursorPos, highlightPos);
        String text = text();

        String textBeforeStart = text.substring(0, JavaCompat.clamp(startIdx, 0, text.length()));
        int startX = x + ComponentCompat.width(font, textBeforeStart);

        String highlighted = editBox.editBox.getHighlighted();
        int highlightWidth = ComponentCompat.width(font, highlighted);

        int highlightY = y - 1;
        int highlightHeight = font.lineHeight + 2;

        gui.fill(startX, highlightY, startX + highlightWidth, highlightY + highlightHeight, config.highlightColor);
    }

    private void renderCursorBar(Font font, GuiCompat gui, int x, int y, int cursorPos, String text, int color) {
        String textBeforeCursor = text.substring(0, JavaCompat.clamp(cursorPos, 0, text.length()));
        int cursorOffset = ComponentCompat.width(font, textBeforeCursor);
        int cursorX = x + cursorOffset;

        if ((System.currentTimeMillis() / 500) % 2 == 0) {
            int cursorY = y - 1;
            int cursorHeight = font.lineHeight + 2;
            gui.fill(cursorX, cursorY, cursorX + 1, cursorY + cursorHeight, color);
        }
    }

    private void selectSuggestion(T suggestion) {
        String id = suggestionIdExtractor.apply(suggestion);
        text(id);
        if (onSuggestionSelected != null) {
            onSuggestionSelected.accept(suggestion);
        }
        showSuggestions = false;
    }

    private void tryTriggerSuggestionsOverlay(Rect rect) {
        if (!showSuggestions || suggestions.isEmpty() || suggestionIdExtractor == null) {
            return;
        }

        Screen screen = ScreenCompat.getScreen(Minecraft.getInstance());
        if (!(screen instanceof ConfigScreen)) {
            return;
        }

        OverlayController overlayController = ((ConfigScreen) screen).getOverlayController();
        overlayController.clear();

        int rowHeight = config.itemInputRowHeight;

        List<SuggestionItemWidget<T>> items = suggestions
                .stream().limit(config.itemInputMaxSuggestions).map(s -> new SuggestionItemWidget<>(new Label(),
                        new RectangleBox(), s, suggestionIdExtractor, suggestionIconRenderer, null, new Config()))
                .collect(Collectors.toList());

        int boxX = rect.x;
        int boxY = rect.y + font.lineHeight + config.itemInputSuggestionsOffsetY;
        DropdownOverlay dropdown = new DropdownOverlay(boxX, boxY, rect.width, rowHeight, items, () -> {
            showSuggestions = false;
            overlayController.pop();
        }, clickedItem -> {
            SuggestionItemWidget<T> suggestionItem = (SuggestionItemWidget<T>) clickedItem;
            selectSuggestion(suggestionItem.value());
            overlayController.pop();
        });

        overlayController.push(dropdown);
    }

    @Override
    public void render(Font font, GuiCompat gui, Rect rect, MousePos mousePos) {
        editBox.setBound(rect);

        String rawText = text();
        boolean isValid = validator.test(rawText);
        int textColor = isValid ? config.secondaryTextColor : config.mutedTextColor;
        int lineY = rect.y + font.lineHeight + 1;
        boolean underline = config.alwaysUnderlined;

        int textX = rect.x;
        int textY = rect.y;

        if (config.rightAlign) {
            Component text = ComponentCompat.literal(rawText);
            int textWidth = ComponentCompat.width(font, text);
            textX = rect.x + rect.width - textWidth;

            gui.text(font, text, textX, textY, textColor);

            if (isFocused()) {
                underline = true;
                int cursorPos = editBox.editBox.getCursorPosition();
                int highlightPos = editBox.highlightPos();

                if (cursorPos != highlightPos) {
                    renderHighlightOverlay(font, gui, textX, textY, cursorPos, highlightPos);
                }

                renderCursorBar(font, gui, textX, textY, cursorPos, rawText, textColor);
            }
            if (shouldRenderHint()) {
                underline = true;
                textX = rect.x + rect.width - ComponentCompat.width(font, hint);
                gui.text(font, hint, textX, textY, config.hintColor);
            }
        } else {
            if (!isFocused()) {
                editBox.editBox.setCursorPosition(0);
                editBox.moveCursor(0);
            }
            editBox.editBox.setTextColor(textColor);
            editBox.render(gui, mousePos, 1.0f);

            if (shouldRenderHint()) {
                underline = true;
                //#if MC <= 11802
                //$$ gui.text(font, hint, textX, textY, config.hintColor);
                //#endif
            }

            if (isFocused() || shouldRenderHint()) {
                underline = true;
            }
        }

        if (underline) {
            gui.fill(textX, lineY, textX + rect.width, lineY + 1, textColor);
        }

        //#if MC >= 12111
        if (mousePos.isHovering(rect)) {
            gui.requestCursor(CursorTypes.IBEAM);
        }
        //#endif
    }

    @Override
    public boolean mouseClicked(Rect rect, MousePos mousePos, int button, int modifiers) {
        boolean hovered = mousePos.isHovering(rect);
        setFocused(hovered);

        if (hovered) {
            if (config.rightAlign) {
                String rawText = text();
                int textWidth = ComponentCompat.width(font, rawText);
                int textX = rect.x + rect.width - textWidth;
                double relativeMouseX = mousePos.x - textX;

                int targetCursorPos = 0;
                int currentWidth = 0;
                for (int i = 0; i < rawText.length(); i++) {
                    int charWidth = ComponentCompat.width(font, String.valueOf(rawText.charAt(i)));
                    if (relativeMouseX < currentWidth + (charWidth / 2.0)) {
                        break;
                    }
                    currentWidth += charWidth;
                    targetCursorPos = i + 1;
                }
                editBox.editBox.setCursorPosition(targetCursorPos);
                editBox.editBox.setHighlightPos(targetCursorPos);
            } else {
                editBox.mouseClicked(mousePos, button, modifiers, false);
            }

            tryTriggerSuggestionsOverlay(rect);

            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(Rect rect, MousePos mousePos, int keycode, int scancode, int modifiers) {
        if (!isFocused()) {
            return false;
        }

        if (showSuggestions && !suggestions.isEmpty() && keycode == InputConstants.KEY_TAB) {
            selectSuggestion(suggestions.get(0));
            Screen screen = ScreenCompat.getScreen(Minecraft.getInstance());
            if (screen instanceof ConfigScreen) {
                ((ConfigScreen) screen).getOverlayController().clear();
            }
            return true;
        }

        if (keycode == InputConstants.KEY_RETURN || keycode == InputConstants.KEY_NUMPADENTER) {
            setFocused(false);
            return true;
        }

        return editBox.keyPressed(keycode, scancode, modifiers);
    }

    @Override
    public boolean charTyped(Rect rect, MousePos mousePos, int codepoint, int modifiers) {
        if (!isFocused()) {
            return false;
        }

        String text = text();
        char typedChar = (char) codepoint;

        String proposedText = text + typedChar;
        if (!filter.test(proposedText)) {
            return true;
        }

        boolean result = editBox.charTyped(codepoint, modifiers);
        tryTriggerSuggestionsOverlay(rect);
        return result;
    }

    @Override
    public void focusChanged(Rect rect, MousePos mousePos, boolean focus) {
        setFocused(focus);
        if (focus) {
            tryTriggerSuggestionsOverlay(rect);
        } else {
            Screen screen = ScreenCompat.getScreen(Minecraft.getInstance());
            if (screen instanceof ConfigScreen) {
                ((ConfigScreen) screen).getOverlayController().clear();
            }
        }
    }

    @FunctionalInterface
    public interface SuggestionIconRenderer<T> {
        void render(GuiCompat gui, T value, Rect rect);
    }
}
