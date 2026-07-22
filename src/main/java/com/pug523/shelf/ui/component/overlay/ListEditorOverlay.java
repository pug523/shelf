package com.pug523.shelf.ui.component.overlay;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.blaze3d.platform.InputConstants;
import com.pug523.shelf.common.compat.ComponentCompat;
import com.pug523.shelf.common.compat.GuiCompat;
import com.pug523.shelf.common.compat.JavaCompat;
import com.pug523.shelf.core.config.Option;
import com.pug523.shelf.core.Colors;
import com.pug523.shelf.gui.layout.Bounds;
import com.pug523.shelf.ui.layout.LayoutConfig;
import com.pug523.shelf.ui.layout.LayoutEngine;
import com.pug523.shelf.ui.sound.SoundUtil;
import com.pug523.shelf.ui.option.GuiOption;
import com.pug523.shelf.ui.option.OptionWidget;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

// TODO: refactor
public class ListEditorOverlay<T> extends WindowOverlay {
    public static final Component TITLE_TEXT = ComponentCompat.literal("Edit List");
    public static final Component BTN_CANCEL = ComponentCompat.literal("Cancel");
    public static final Component BTN_OK = ComponentCompat.literal("OK");

    private final List<T> workingList;
    private final List<OptionWidget<T>> itemWidgets = new ArrayList<>();
    private final List<GuiOption<T>> itemOptions = new ArrayList<>();

    private final Function<GuiOption<T>, OptionWidget<T>> itemWidgetFactory;
    private final Supplier<T> itemDefaultValueFactory;

    private int selectedIndex = -1;
    private double scrollOffset = 0;

    // TODO: i18n
    private final ActionButtonWidget addButton = new ActionButtonWidget(ComponentCompat.literal("+ Add"),
            (btn) -> addNewItem());
    private final ActionButtonWidget removeButton = new ActionButtonWidget(ComponentCompat.literal("- Remove"),
            (btn) -> removeSelectedItem());
    private final ActionButtonWidget moveUpButton = new ActionButtonWidget(ComponentCompat.literal("▲ Up"),
            (btn) -> moveSelected(-1));
    private final ActionButtonWidget moveDownButton = new ActionButtonWidget(ComponentCompat.literal("▼ Down"),
            (btn) -> moveSelected(1));

    public ListEditorOverlay(GuiOption<List<T>> targetOption, Function<GuiOption<T>, OptionWidget<T>> itemWidgetFactory,
            Supplier<T> itemDefaultValueFactory, Runnable onFinish) {
        super(BTN_CANCEL, BTN_OK);

        this.itemWidgetFactory = itemWidgetFactory;
        this.itemDefaultValueFactory = itemDefaultValueFactory;
        this.workingList = new ArrayList<>(targetOption.getPendingValue());

        setCallbacks(() -> {
            syncWorkingListFromWidgets();
            targetOption.setPendingValue(new ArrayList<>(this.workingList));
            onFinish.run();
        }, onFinish);

        rebuildItemWidgets();
        updateButtonStates();
    }

    private void scrollToShow(int index, LayoutEngine layout) {
        int itemHeight = 22;
        int itemTop = index * itemHeight;
        int itemBottom = itemTop + itemHeight;

        int listHeight = 120;
        if (layout != null) {
            LayoutConfig cfg = layout.getConfig();
            Bounds dialogBounds = layout.pickerDialog;
            int listPadding = cfg.pickerPaddingInner;
            listHeight = dialogBounds.height - (listPadding * 3) - 50;
        }

        if (itemTop < scrollOffset) {
            scrollOffset = itemTop;
        } else if (itemBottom > scrollOffset + listHeight) {
            scrollOffset = itemBottom - listHeight;
        }
    }

    private void rebuildItemWidgets() {
        itemWidgets.clear();
        itemOptions.clear();

        for (int i = 0; i < workingList.size(); i++) {
            final int index = i;
            Option<T> tempItemOption = new Option<>(ComponentCompat.literal("Item " + (i + 1)), "item." + i,
                    itemDefaultValueFactory.get(), () -> workingList.get(index), (val) -> {
                        if (index < workingList.size()) {
                            workingList.set(index, val);
                        }
                    }, new ArrayList<>());

            itemOptions.add(tempItemOption);
            itemWidgets.add(itemWidgetFactory.apply(tempItemOption));
        }
    }

    private void setFocusedIndex(int index, LayoutEngine layout) {
        if (selectedIndex >= 0 && selectedIndex < itemWidgets.size()) {
            itemWidgets.get(selectedIndex).focusChanged(false, layout);
        }

        selectedIndex = index;

        if (selectedIndex >= 0 && selectedIndex < itemWidgets.size()) {
            itemWidgets.get(selectedIndex).focusChanged(true, layout);
            scrollToShow(selectedIndex, layout);
        }

        updateButtonStates();
    }

    private void updateButtonStates() {
        boolean hasSelection = selectedIndex >= 0 && selectedIndex < itemWidgets.size();

        removeButton.setEnabled(hasSelection);
        moveUpButton.setVisible(hasSelection);
        moveDownButton.setVisible(hasSelection);

        boolean canMoveUp = hasSelection && selectedIndex > 0;
        boolean canMoveDown = hasSelection && selectedIndex < itemWidgets.size() - 1;
        moveUpButton.setEnabled(canMoveUp);
        moveDownButton.setEnabled(canMoveDown);
    }

    private void syncWorkingListFromWidgets() {
        for (int i = 0; i < itemOptions.size() && i < workingList.size(); i++) {
            workingList.set(i, itemOptions.get(i).getPendingValue());
        }
    }

    private void addNewItem() {
        syncWorkingListFromWidgets();

        int insertIndex = (selectedIndex >= 0 && selectedIndex < workingList.size()) ? selectedIndex + 1
                : workingList.size();

        workingList.add(insertIndex, itemDefaultValueFactory.get());
        rebuildItemWidgets();

        setFocusedIndex(insertIndex, null);
        SoundUtil.clickSound();
    }

    private void removeSelectedItem() {
        syncWorkingListFromWidgets();
        if (selectedIndex >= 0 && selectedIndex < workingList.size()) {
            workingList.remove(selectedIndex);
            rebuildItemWidgets();

            if (workingList.isEmpty()) {
                setFocusedIndex(-1, null);
            } else {
                int nextIndex = Math.min(selectedIndex, workingList.size() - 1);
                setFocusedIndex(nextIndex, null);
            }
            SoundUtil.clickSound();
        }
    }

    private void moveSelected(int direction) {
        syncWorkingListFromWidgets();
        if (selectedIndex < 0 || selectedIndex >= workingList.size())
            return;
        int targetIndex = selectedIndex + direction;
        if (targetIndex >= 0 && targetIndex < workingList.size()) {
            T temp = workingList.get(selectedIndex);
            workingList.set(selectedIndex, workingList.get(targetIndex));
            workingList.set(targetIndex, temp);

            rebuildItemWidgets();

            setFocusedIndex(targetIndex, null);
            SoundUtil.clickSound();
        }
    }

    @Override
    public void render(Font font, GuiCompat gui, LayoutEngine layout, int x, int y, int width, int height, int mouseX,
            int mouseY) {
        LayoutConfig cfg = layout.getConfig();

        Bounds dialogBounds = layout.pickerDialog;
        gui.enableScissor(dialogBounds.x, dialogBounds.y, dialogBounds.maxX, dialogBounds.maxY);

        renderDialogFrame(font, gui, dialogBounds, TITLE_TEXT, cfg);

        int listPadding = cfg.pickerPaddingInner;
        int listX = dialogBounds.x + listPadding;
        int listY = dialogBounds.y + listPadding + 20;
        int listWidth = dialogBounds.width - (listPadding * 2);
        int listHeight = dialogBounds.height - (listPadding * 3) - 50;

        gui.fill(listX, listY, listX + listWidth, listY + listHeight, 0xAF0D0E15);

        gui.enableScissor(listX, listY, listX + listWidth, listY + listHeight);

        // TODO: move to layout config
        int itemHeight = 22;
        int totalContentHeight = itemWidgets.size() * itemHeight;

        scrollOffset = JavaCompat.clamp(scrollOffset, 0.0d, Math.max(0.0d, totalContentHeight - listHeight));

        for (int i = 0; i < itemWidgets.size(); i++) {
            int itemY = (int) (listY + (i * itemHeight) - scrollOffset);
            if (itemY + itemHeight < listY || itemY > listY + listHeight)
                continue;

            boolean isSelected = (i == selectedIndex);
            int bgColor = isSelected ? 0x6F2563EB : (i % 2 == 0 ? 0x1FFFFFFF : 0x00000000);

            gui.fill(listX, itemY, listX + listWidth, itemY + itemHeight, bgColor);

            OptionWidget<T> widget = itemWidgets.get(i);

            String numText = "#" + (i + 1);
            gui.text(font, ComponentCompat.literal(numText), listX + 6, itemY + (itemHeight - 8) / 2, Colors.WHITE,
                    false);

            int widgetX = listX + 24;
            int widgetWidth = listWidth - 28;
            int widgetHeight = itemHeight - 4;
            int widgetY = itemY + 2;

            widget.render(font, gui, layout, widgetX, widgetY, widgetWidth, widgetHeight, mouseX, mouseY);
        }
        gui.disableScissor();

        int controlY = listY + listHeight + 8;
        int btnW = 60;
        int btnH = 18;

        addButton.render(font, gui, layout, listX, controlY, btnW, btnH, mouseX, mouseY);
        removeButton.render(font, gui, layout, listX + btnW + 4, controlY, btnW, btnH, mouseX, mouseY);
        moveUpButton.render(font, gui, layout, listX + (btnW + 4) * 2, controlY, btnW, btnH, mouseX, mouseY);
        moveDownButton.render(font, gui, layout, listX + (btnW + 4) * 3, controlY, btnW, btnH, mouseX, mouseY);

        int footerY = dialogBounds.maxY - cfg.pickerPaddingInner - 20;
        int confirmBtnW = 60;
        int confirmBtnH = 18;
        int cancelX = dialogBounds.maxX - cfg.pickerPaddingInner - (confirmBtnW * 2) - 4;
        int okX = dialogBounds.maxX - cfg.pickerPaddingInner - confirmBtnW;

        cancelButton.render(font, gui, layout, cancelX, footerY, confirmBtnW, confirmBtnH, mouseX, mouseY);
        okButton.renderWithBackground(font, gui, layout, okX, footerY, confirmBtnW, confirmBtnH, mouseX, mouseY,
                COLOR_BTN_OK_BG);

        gui.disableScissor();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int modifiers, LayoutEngine layout) {
        if (cancelButton.mouseClicked(mouseX, mouseY, button, 0, layout)
                || okButton.mouseClicked(mouseX, mouseY, button, 0, layout)
                || addButton.mouseClicked(mouseX, mouseY, button, 0, layout)
                || removeButton.mouseClicked(mouseX, mouseY, button, 0, layout)
                || moveUpButton.mouseClicked(mouseX, mouseY, button, 0, layout)
                || moveDownButton.mouseClicked(mouseX, mouseY, button, 0, layout)) {
            return true;
        }

        LayoutConfig cfg = layout.getConfig();
        Bounds dialogBounds = layout.pickerDialog;
        int listPadding = cfg.pickerPaddingInner;
        int listX = dialogBounds.x + listPadding;
        int listY = dialogBounds.y + listPadding + 20;
        int listWidth = dialogBounds.width - (listPadding * 2);
        int listHeight = dialogBounds.height - (listPadding * 3) - 50;

        if (mouseX >= listX && mouseX <= listX + listWidth && mouseY >= listY && mouseY <= listY + listHeight) {
            int clickedRelativeY = (int) (mouseY - listY + scrollOffset);
            int itemHeight = 22;
            int clickedIdx = clickedRelativeY / itemHeight;

            if (clickedIdx >= 0 && clickedIdx < itemWidgets.size()) {
                setFocusedIndex(clickedIdx, layout);
                SoundUtil.clickSound();

                OptionWidget<T> widget = itemWidgets.get(clickedIdx);
                int widgetX = listX + 24;
                int widgetWidth = listWidth - 28;
                int widgetHeight = itemHeight - 4;
                int itemY = (int) (listY + (clickedIdx * itemHeight) - scrollOffset);
                int widgetY = itemY + 2;

                if (mouseX >= widgetX && mouseX <= widgetX + widgetWidth && mouseY >= widgetY
                        && mouseY <= widgetY + widgetHeight) {
                    widget.mouseClicked(mouseX, mouseY, button, modifiers, layout);
                }
            } else {
                setFocusedIndex(-1, layout);
            }
            return true;
        }

        return true;
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button, LayoutEngine layout) {
        cancelButton.mouseReleased(mouseX, mouseY, button, layout);
        okButton.mouseReleased(mouseX, mouseY, button, layout);
        addButton.mouseReleased(mouseX, mouseY, button, layout);
        removeButton.mouseReleased(mouseX, mouseY, button, layout);
        moveUpButton.mouseReleased(mouseX, mouseY, button, layout);
        moveDownButton.mouseReleased(mouseX, mouseY, button, layout);

        if (selectedIndex >= 0 && selectedIndex < itemWidgets.size()) {
            itemWidgets.get(selectedIndex).mouseReleased(mouseX, mouseY, button, layout);
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY,
            LayoutEngine layout) {
        if (selectedIndex >= 0 && selectedIndex < itemWidgets.size()) {
            if (itemWidgets.get(selectedIndex).mouseDragged(mouseX, mouseY, button, dragX, dragY, layout)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY, LayoutEngine layout) {
        scrollOffset -= scrollY * 12;
        return true;
    }

    @Override
    public boolean keyPressed(int keycode, int scancode, int modifiers, LayoutEngine layout) {
        if (selectedIndex >= 0 && selectedIndex < itemWidgets.size()) {
            if (itemWidgets.get(selectedIndex).keyPressed(keycode, scancode, modifiers, layout)) {
                return true;
            }
        }

        if (keycode == InputConstants.KEY_UP) {
            if (!itemWidgets.isEmpty()) {
                int nextIndex = (selectedIndex <= 0) ? itemWidgets.size() - 1 : selectedIndex - 1;
                setFocusedIndex(nextIndex, layout);
                SoundUtil.clickSound();
                return true;
            }
        } else if (keycode == InputConstants.KEY_DOWN) {
            if (!itemWidgets.isEmpty()) {
                int nextIndex = (selectedIndex >= itemWidgets.size() - 1) ? 0 : selectedIndex + 1;
                setFocusedIndex(nextIndex, layout);
                SoundUtil.clickSound();
                return true;
            }
        } else if (keycode == InputConstants.KEY_ESCAPE) {
            cancel();
            return true;
        } else if (keycode == InputConstants.KEY_RETURN || keycode == InputConstants.KEY_NUMPADENTER) {
            ok();
            return true;
        }

        return true;
    }

    @Override
    public boolean charTyped(int codepoint, int modifiers, LayoutEngine layout) {
        if (selectedIndex >= 0 && selectedIndex < itemWidgets.size()) {
            return itemWidgets.get(selectedIndex).charTyped(codepoint, modifiers, layout);
        }
        return false;
    }
}
