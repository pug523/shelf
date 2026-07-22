package com.pug523.shelf.ui;

import com.pug523.shelf.common.compat.GuiCompat;
import com.pug523.shelf.core.geometry.MousePos;
import com.pug523.shelf.core.geometry.Rect;
import net.minecraft.client.gui.Font;

public interface Element {
    void render(Font font, GuiCompat gui, Rect rect, MousePos mousePos);
}
