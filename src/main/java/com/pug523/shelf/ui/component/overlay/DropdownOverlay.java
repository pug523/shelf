package com.pug523.shelf.gui.widget.overlay;

import java.util.List;
import java.util.function.Consumer;

import com.pug523.shelf.common.compat.GuiCompat;
import com.pug523.shelf.gui.layout.LayoutEngine;
import com.pug523.shelf.ui.view.widget.Widget;

import net.minecraft.client.gui.Font;

public class DropdownOverlay implements OverlayWidget {
    private final int x, y, width, height;
    private final List<? extends Widget> items;
    private final Runnable onDismiss;
    private final Consumer<Widget> onSelect;

    public DropdownOverlay(
        int x,
        int y,
        int width,
        int height,
        List<? extends Widget> items,
        Runnable onDismiss,
        Consumer<Widget> onSelect
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.items = items;
        this.onDismiss = onDismiss;
        this.onSelect = onSelect;
    }

    @Override
    public void render(Font font, GuiCompat gui, LayoutEngine layout, int x, int y, int width, int height, int mouseX, int mouseY) {
        int currentY = this.y;
        for (Widget item : items) {
            item.render(font, gui, layout, this.x, currentY, this.width, this.height, mouseX, mouseY);
            currentY += this.height;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int modifiers, LayoutEngine layout) {
        int totalHeight = items.size() * this.height;
        boolean inside = mouseX >= this.x && mouseX < this.x + this.width &&
            mouseY >= this.y && mouseY < this.y + totalHeight;

        if (inside) {
            int currentY = this.y;
            for (Widget item : items) {
                if (mouseY >= currentY && mouseY < currentY + this.height) {
                    if (item.mouseClicked(mouseX, mouseY, button, modifiers, layout)) {
                        if (onSelect != null) {
                            onSelect.accept(item);
                        }
                        return true;
                    }
                }
                currentY += this.height;
            }
        } else {
            if (onDismiss != null) {
                onDismiss.run();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keycode, int scancode, int modifiers, LayoutEngine layout) {
        for (Widget item : items) {
            if (item.keyPressed(keycode, scancode, modifiers, layout)) {
                return true;
            }
        }
        return false;
    }
}
