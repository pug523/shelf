package com.pug523.shelf.common.compat;

import com.pug523.shelf.core.geometry.MousePos;
import com.pug523.shelf.core.geometry.Rect;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

//#if MC >= 12109
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
//#endif

public class EditBoxCompat {
    public final EditBox editBox;

    public EditBoxCompat(EditBox editBox) {
        this.editBox = editBox;
    }

    public EditBoxCompat(Font font, Rect rect, Component narration) {
        //#if MC >= 11600
        EditBox editBox = new EditBox(font, rect.x, rect.y, rect.width, rect.height, narration);
        //#else
        //$$ this.editBox = new EditBox(font, rect.x, rect.y, rect.width, rect.height, narration.getString());
        //#endif

        this(editBox);
    }

    public void hint(Component hint) {
        //#if MC >= 11900
        editBox.setHint(hint);
        //#endif
    }

    public void textShadow(boolean textShadow) {
        //#if MC >= 12106
        editBox.setTextShadow(textShadow);
        //#endif
    }

    public void addFormatter(EditBox.TextFormatter formatter) {
        //#if MC >= 12109
        editBox.addFormatter(formatter);
        //#else
        //$$ editBox.setFormatter(formatter);
        //#endif

    }

    public void setBound(Rect rect) {
        editBox.setX(rect.x);
        //#if MC >= 11900
        editBox.setY(rect.y);
        //#else
        //$$ editBox.y = rect.y;
        //#endif
        editBox.setWidth(rect.width);
        //#if MC >= 12002
        editBox.setHeight(rect.height);
        //#else
        //$$ editBox.height = rect.height;
        //#endif
    }

    public void moveCursor(int cursorPos) {
        //#if MC >= 12002
        editBox.moveCursor(cursorPos, false);
        //#else
        //$$ editBox.moveCursor(cursorPos);
        //#endif
    }

    public void setFocused(boolean focus) {
        //#if MC >= 11900
        editBox.setFocused(focus);
        //#else
        //$$ editBox.setFocus(focus);
        //#endif
    }

    public int x() {
        //#if MC >= 11900
        return editBox.getX();
        //#else
        //$$ return editBox.x;
        //#endif
    }

    public int y() {
        //#if MC >= 11900
        return editBox.getY();
        //#else
        //$$ return editBox.y;
        //#endif
    }

    public int highlightPos() {
        return editBox.highlightPos;
    }

    public void render(GuiCompat gui, MousePos mousePos, float alpha) {
        //#if MC >= 12000
        editBox.extractWidgetRenderState(gui.getGraphics(), (int) mousePos.x, (int) mousePos.y, alpha);
        //#else
        //$$ editBox.render(gui.getPoseStack(), (int) mousePos.x, (int) mousePos.y, alpha);
        //#endif
    }

    public void mouseClicked(MousePos mousePos, int button, int modifiers, boolean doubleClick) {
        //#if MC >= 12109
        editBox.mouseClicked(new MouseButtonEvent(mousePos.x, mousePos.y, new MouseButtonInfo(button, modifiers)),
                doubleClick);
        //#else
        //$$ editBox.mouseClicked(mousePos.x, mousePos.y, button);
        //#endif
    }

    public boolean keyPressed(int keycode, int scancode, int modifiers) {
        //#if MC >= 12109
        return editBox.keyPressed(new KeyEvent(keycode, scancode, modifiers));
        //#else
        //$$ return editBox.keyPressed(keycode, scancode, modifiers);
        //#endif
    }

    public boolean charTyped(int codepoint, int modifiers) {
        //#if MC >= 260000
        return editBox.charTyped(new CharacterEvent(codepoint));
        //#elseif MC >= 12109
        //$$ return editBox.charTyped(new CharacterEvent(codepoint, modifiers));
        //#else
        //$$ return editBox.charTyped((char) codepoint, modifiers);
        //#endif
    }
}
