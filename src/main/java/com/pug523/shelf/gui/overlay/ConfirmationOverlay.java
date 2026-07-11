package com.pug523.shelf.gui.overlay;

import com.pug523.shelf.compat.ComponentCompat;
import com.pug523.shelf.compat.GuiCompat;
import com.pug523.shelf.gui.Colors;
import com.pug523.shelf.gui.layout.Bounds;
import com.pug523.shelf.gui.layout.LayoutConfig;
import com.pug523.shelf.gui.layout.LayoutEngine;
import com.pug523.shelf.gui.renderer.RenderUtil;
import com.pug523.shelf.gui.sound.SoundUtil;

import com.pug523.shelf.gui.widget.ActionButtonWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class ConfirmationOverlay implements ScreenOverlay {
    private static final int COLOR_BG_OUTLINE = 0xAF11131E;
    private static final int COLOR_BG_INNER = 0xAF161923;
    private static final int COLOR_BTN_CONFIRM_BG = 0xFFEF4444;

    // TODO: i18n
    private static final Component TITLE = ComponentCompat.literal("Unsaved Changes");
    private static final Component MESSAGE = ComponentCompat.literal("Are you sure you want to discard changes?");
    private static final Component BTN_YES = ComponentCompat.literal("Yes");
    private static final Component BTN_NO = ComponentCompat.literal("No");

    private final Runnable onConfirm;
    private final Runnable onCancel;

    private final ActionButtonWidget yesButton = new ActionButtonWidget(BTN_YES, (btn) -> confirm());
    private final ActionButtonWidget noButton = new ActionButtonWidget(BTN_NO, (btn) -> cancel());

    public ConfirmationOverlay(Runnable onConfirm, Runnable onCancel) {
        this.onConfirm = onConfirm;
        this.onCancel = onCancel;
    }

    @Override
    public void render(Font font, GuiCompat gui, int mouseX, int mouseY, float partialTicks, LayoutEngine layout) {
        LayoutConfig cfg = layout.getConfig();
        Bounds dialog = layout.confirmDialog;

        gui.enableScissor(dialog.x, dialog.y, dialog.maxX, dialog.maxY);

        final int BORDER_THICKNESS = 1;
        RenderUtil.renderOutline(gui, dialog.x, dialog.y, dialog.maxX, dialog.maxY, BORDER_THICKNESS, COLOR_BG_OUTLINE);
        RenderUtil.renderInner(gui, dialog.x, dialog.y, dialog.maxX, dialog.maxY, BORDER_THICKNESS, COLOR_BG_INNER);

        int currentY = dialog.y + cfg.confirmPaddingInner;
        gui.text(font, TITLE, dialog.x + cfg.confirmPaddingInner, currentY, Colors.WHITE, false);

        currentY += font.lineHeight + 6;
        gui.text(font, MESSAGE, dialog.x + cfg.confirmPaddingInner, currentY, cfg.colorTextSecondary, false);

        Bounds yesB = layout.confirmYesButton;
        yesButton.renderWithBackground(font, gui, layout, yesB.x, yesB.y, yesB.width, yesB.height, mouseX, mouseY, COLOR_BTN_CONFIRM_BG);

        Bounds noB = layout.confirmNoButton;
        noButton.render(font, gui, layout, noB.x, noB.y, noB.width, noB.height, mouseX, mouseY);

        gui.disableScissor();
    }

    private void confirm() {
        SoundUtil.clickSound();
        this.onConfirm.run();
    }

    private void cancel() {
        SoundUtil.clickSound();
        this.onCancel.run();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, LayoutEngine layout) {
        yesButton.mouseClicked(mouseX, mouseY, button, 0, layout);
        noButton.mouseClicked(mouseX, mouseY, button, 0, layout);
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button, LayoutEngine layout) {
        return true;
    }

    @Override
    public boolean keyPressed(int keycode, int scancode, int modifiers, LayoutEngine layout) {
        return true;
    }

    @Override
    public boolean charTyped(int codepoint, int modifiers, LayoutEngine layout) {
        return true;
    }
}
