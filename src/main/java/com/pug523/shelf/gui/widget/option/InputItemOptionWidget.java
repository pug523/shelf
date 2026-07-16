package com.pug523.shelf.gui.widget.option;

import com.pug523.shelf.compat.BuiltinRegistriesCompat;
import com.pug523.shelf.compat.ComponentCompat;
import com.pug523.shelf.compat.GuiCompat;
import com.pug523.shelf.compat.IdentifierCompat;
import com.pug523.shelf.gui.layout.LayoutConfig;
import com.pug523.shelf.gui.layout.LayoutEngine;
import com.pug523.shelf.gui.widget.TextInputFieldWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class InputItemOptionWidget extends OptionWidget<Item> {
    private final TextInputFieldWidget<Item> textField;
    private LayoutConfig cachedLayoutConfig = null;

    public InputItemOptionWidget(GuiOption<Item> option) {
        super(option);

        this.textField = new TextInputFieldWidget<>(
            true,
            text -> text.matches("^[a-z0-9_.-]*:?[a-z0-9_.-]*$"),
            text -> text.isEmpty() || findItem(text) != Items.AIR,
            this::onTextChange,
            this::formatItemIdentifier,
            getItemRegistryName(this.getPendingValue())
        );
        // TODO: i18n
        this.textField.setHint(ComponentCompat.translatable("item..."));
        this.textField.setAlwaysUnderlined(true);
        this.textField.setupSuggestions(
            this::getItemRegistryName,
            this::renderItemIcon,
            this::onSuggestionSelected,
            false
        );
    }

    private Component formatItemIdentifier(String rawText) {
        if (rawText.isEmpty()) {
            return ComponentCompat.empty();
        }

        int colonIndex = rawText.indexOf(':');
        if (colonIndex == -1) {
            return ComponentCompat.literal(rawText);
        }

        MutableComponent result = ComponentCompat.literal(rawText.substring(0, colonIndex + 1));
        result.append(ComponentCompat.literal(rawText.substring(colonIndex + 1)));
        return result;
    }

    private void onTextChange(String input) {
        Item foundItem = findItem(input);
        if (input.isEmpty()) {
            this.setPendingValue(Items.AIR);
        } else {
            this.setPendingValue(foundItem);
        }

        int maxCount = (cachedLayoutConfig != null) ? cachedLayoutConfig.itemInputMaxSuggestions : 7;
        updateSuggestions(input, maxCount);
    }

    private void onSuggestionSelected(Item selectedItem) {
        this.setPendingValue(selectedItem);
    }

    private Item findItem(String input) {
        Identifier id = IdentifierCompat.tryParse(input);
        if (id != null && BuiltinRegistriesCompat.ITEM.containsKey(id)) {
            return BuiltinRegistriesCompat.getItem(id);
        }
        return Items.AIR;
    }

    private void updateSuggestions(String query, int maxCount) {
        if (query.isEmpty()) {
            this.textField.setSuggestions(new ArrayList<>());
            return;
        }

        List<Item> matchedItems = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        for (Identifier id : BuiltinRegistriesCompat.ITEM.keySet()) {
            String idStr = id.toString();
            if (idStr.contains(lowerQuery)) {
                matchedItems.add(BuiltinRegistriesCompat.getItem(id));
            }
            if (matchedItems.size() >= maxCount) {
                break;
            }
        }
        this.textField.setSuggestions(matchedItems);
    }

    private String getItemRegistryName(Item item) {
        return BuiltinRegistriesCompat.ITEM.getKey(item).toString();
    }

    @Override
    public void render(Font font, GuiCompat gui, LayoutEngine layout, int x, int y, int width, int height, int mouseX, int mouseY) {
        Item currentItem = this.getPendingValue();
        String currentStr = currentItem == Items.AIR ? "" : getItemRegistryName(currentItem);
        LayoutConfig cfg = layout.getConfig();
        this.cachedLayoutConfig = cfg;

        int iconSize = cfg.itemInputIconSize;
        this.textField.setMaxLength(cfg.itemInputMaxLength);

        if (!this.textField.isFocused() && !currentStr.equals(this.textField.getText())) {
            this.textField.setText(currentStr);
            updateSuggestions(currentStr, cfg.itemInputMaxSuggestions);
        }

        int leftTextPadding = cfg.itemInputLeftTextPadding;
        int targetX = x + leftTextPadding;
        int targetY = y + (height - font.lineHeight) / 2 + 1;
        int targetWidth = Math.max(cfg.itemInputMinTextFieldWidth, width - leftTextPadding - layout.optionWidgetRightMargin);

        if (currentItem != Items.AIR) {
            int inputIconX = this.textField.drawX() - iconSize - cfg.itemInputIconSpacing;
            int inputIconY = y + (height - iconSize) / 2;
            renderItemIcon(gui, currentItem, inputIconX, inputIconY);
        }

        this.textField.render(font, gui, layout, targetX, targetY, targetWidth, height, mouseX, mouseY);
    }

    private void renderItemIcon(GuiCompat gui, Item item, int x, int y) {
        // @formatter:off
        //#if MC >= 260000
        try {
        //#endif
            //#if MC >= 12000
            gui.getGraphics().fakeItem(item.getDefaultInstance(), x, y);
            //#endif
        //#if MC >= 260000
        } catch (NullPointerException ignored) {
            // Item stacks doesn't exist until dynamic registries have been loaded,
            // which is either loading into a level or opening the create world screen.
        }
        //#endif
        // @formatter:on
    }

    @Override
    public void resetPendingToDefault() {
        super.resetPendingToDefault();
        this.textField.setText(getItemRegistryName(this.getPendingValue()));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int modifiers, LayoutEngine layout) {
        return this.textField.mouseClicked(mouseX, mouseY, button, modifiers, layout);
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
