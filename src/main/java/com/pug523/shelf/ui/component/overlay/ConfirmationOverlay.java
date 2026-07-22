package com.pug523.shelf.ui.component.overlay;

import com.pug523.shelf.common.compat.ComponentCompat;
import com.pug523.shelf.common.compat.GuiCompat;
import com.pug523.shelf.gui.layout.Bounds;
import com.pug523.shelf.ui.layout.LayoutConfig;
import com.pug523.shelf.ui.layout.LayoutEngine;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

// TODO: refactor
public class ConfirmationOverlay extends WindowOverlay {
    private static final int COLOR_BTN_CONFIRM_BG = 0xFFEF4444;

    // TODO: i18n
    private static final Component TITLE = ComponentCompat.literal("Unsaved Changes");
    private static final Component MESSAGE = ComponentCompat.literal("Are you sure you want to discard changes?");
    private static final Component BTN_YES = ComponentCompat.literal("Yes");
    private static final Component BTN_NO = ComponentCompat.literal("No");

    public ConfirmationOverlay(Consumer<ConfirmationOverlay> onConfirm, Consumer<ConfirmationOverlay> onCancel) {
        super(BTN_NO, BTN_YES);
        setCallbacks(
            () -> onConfirm.accept(this),
            () -> onCancel.accept(this)
        );
    }

    @Override
    public void render(Font font, GuiCompat gui, LayoutEngine layout, int x, int y, int width, int height, int mouseX,
                       int mouseY) {
        LayoutConfig cfg = layout.getConfig();
        Bounds dialog = layout.confirmDialog;

        gui.enableScissor(dialog.x, dialog.y, dialog.maxX, dialog.maxY);

        renderDialogFrame(font, gui, dialog, TITLE, cfg);

        int currentY = dialog.y + cfg.confirmPaddingInner + font.lineHeight + 6;
        gui.text(font, MESSAGE, dialog.x + cfg.confirmPaddingInner, currentY, cfg.colorTextSecondary, false);

        Bounds yesB = layout.confirmYesButton;
        okButton.renderWithBackground(font, gui, layout, yesB.x, yesB.y, yesB.width, yesB.height, mouseX, mouseY, COLOR_BTN_CONFIRM_BG);

        Bounds noB = layout.confirmNoButton;
        cancelButton.render(font, gui, layout, noB.x, noB.y, noB.width, noB.height, mouseX, mouseY);

        gui.disableScissor();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int modifiers, LayoutEngine layout) {
        okButton.mouseClicked(mouseX, mouseY, button, 0, layout);
        cancelButton.mouseClicked(mouseX, mouseY, button, 0, layout);
        return true;
    }
}
