package com.pug523.shelf.gui.widget.option;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import com.pug523.shelf.compat.ComponentCompat;
import com.pug523.shelf.compat.GuiCompat;
import com.pug523.shelf.compat.ScreenCompat;
import com.pug523.shelf.gui.ConfigScreen;
import com.pug523.shelf.gui.controller.OverlayController;
import com.pug523.shelf.gui.layout.LayoutConfig;
import com.pug523.shelf.gui.layout.LayoutEngine;
import com.pug523.shelf.gui.widget.ActionButtonWidget;
import com.pug523.shelf.gui.widget.overlay.ListEditorOverlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ListOptionWidget<T> extends OptionWidget<List<T>> {
    private final ActionButtonWidget editButton;

    private final Function<GuiOption<T>, OptionWidget<T>> itemWidgetFactory;
    private final Supplier<T> itemDefaultValueFactory;

    public ListOptionWidget(
        GuiOption<List<T>> option,
        Function<GuiOption<T>, OptionWidget<T>> itemWidgetFactory,
        Supplier<T> itemDefaultValueFactory) {
        super(option);
        this.itemWidgetFactory = itemWidgetFactory;
        this.itemDefaultValueFactory = itemDefaultValueFactory;

        // TODO: i18n
        this.editButton = new ActionButtonWidget(ComponentCompat.literal("Edit List..."), (btn) -> openEditor());
    }

    private void openEditor() {
        Screen screen = ScreenCompat.getScreen(Minecraft.getInstance());
        if (screen instanceof ConfigScreen) {
            ConfigScreen configScreen = (ConfigScreen) screen;
            OverlayController overlayController = configScreen.getOverlayController();
            overlayController.clear();
            ListEditorOverlay<T> overlay = new ListEditorOverlay<>(
                option,
                itemWidgetFactory,
                itemDefaultValueFactory,
                overlayController::pop
            );
            overlayController.push(overlay);
        }
    }

    private String countText() {
        int count = option.getPendingValue().size();
        return String.format("%d Items", count);
        // if (count == 1) {
        //     return "1 Item";
        // } else {
        //     return String.format("%d Items", count);
        // }
    }

    @Override
    public void render(Font font, GuiCompat gui, LayoutEngine layout, int x, int y, int width, int height, int mouseX, int mouseY) {
        LayoutConfig cfg = layout.getConfig();

        // TODO: add to layout config independently
        int listEditButtonWidth = 70;
        int listEditButtonHeight = cfg.toggleButtonHeight;
        int listCountTextPadding = 8;
        int listCountTextOffsetY = 1;
        int colorListCountText = cfg.colorTextMuted;

        int btnX = x + width - listEditButtonWidth - layout.optionWidgetRightMargin;
        int btnY = y + (height - listEditButtonHeight) / 2;
        editButton.render(font, gui, layout, btnX, btnY, listEditButtonWidth, listEditButtonHeight, mouseX, mouseY);

        Component count = ComponentCompat.literal(countText());
        int textWidth = ComponentCompat.width(font, count);
        int textX = btnX - textWidth -  listCountTextPadding;
        int textY = y + (height - font.lineHeight) / 2 + listCountTextOffsetY;
        gui.text(font, count, textX, textY, colorListCountText, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int modifiers, LayoutEngine layout) {
        return editButton.mouseClicked(mouseX, mouseY, button, modifiers, layout);
    }
}
