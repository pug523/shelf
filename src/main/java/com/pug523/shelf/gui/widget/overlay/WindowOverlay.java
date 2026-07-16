package com.pug523.shelf.gui.widget.overlay;

import com.pug523.shelf.gui.Colors;
import com.pug523.shelf.gui.layout.Bounds;
import com.pug523.shelf.gui.layout.LayoutConfig;
import com.pug523.shelf.gui.widget.ActionButtonWidget;
import com.pug523.shelf.compat.ComponentCompat;
import com.pug523.shelf.compat.GuiCompat;
import com.pug523.shelf.gui.sound.SoundUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public abstract class WindowOverlay implements OverlayWidget {
    // TODO: move to layout config
    protected static final int COLOR_BG_OUTLINE = 0xAF11131E;
    protected static final int COLOR_BG_INNER = 0xAF161923;
    protected static final int COLOR_BTN_OK_BG = 0xFF2563EB;

    protected final ActionButtonWidget cancelButton;
    protected final ActionButtonWidget okButton;

    protected Runnable onOk;
    protected Runnable onCancel;

    public WindowOverlay(Component cancelLabel, Component okLabel) {
        this.cancelButton = new ActionButtonWidget(cancelLabel, (btn) -> cancel());
        this.okButton = new ActionButtonWidget(okLabel, (btn) -> ok());
    }

    protected void setCallbacks(Runnable onOk, Runnable onCancel) {
        this.onOk = onOk;
        this.onCancel = onCancel;
    }

    @Override
    public boolean shouldDimBackground() {
        return true;
    }

    protected void renderDialogFrame(Font font, GuiCompat gui, Bounds bound, Component title, LayoutConfig config) {
        gui.fill(bound.x, bound.y, bound.maxX, bound.maxY, COLOR_BG_OUTLINE);
        gui.fill(bound.x + 1, bound.y + 1, bound.maxX - 1, bound.maxY - 1, COLOR_BG_INNER);

        int titleY = bound.y + config.pickerPaddingInner;
        gui.text(font, title, bound.x + config.pickerPaddingInner, titleY, Colors.WHITE, false);
    }

    protected void cancel() {
        SoundUtil.clickSound();
        if (onCancel != null) {
            onCancel.run();
        }
    }

    protected void ok() {
        SoundUtil.clickSound();
        if (onOk != null) {
            onOk.run();
        }
    }
}
