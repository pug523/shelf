package com.pug523.shelf.ui.screen;

import com.pug523.shelf.ui.screen.input.ConfigInputHandler;
import com.pug523.shelf.ui.screen.vm.ConfigScreenViewModel;
import org.jspecify.annotations.NonNull;

import com.pug523.shelf.common.compat.GuiCompat;
import com.pug523.shelf.common.compat.ScreenCompat;
import com.pug523.shelf.ui.layout.LayoutEngine;
import com.pug523.shelf.ui.screen.renderer.ConfigScreenRenderer;

import net.minecraft.client.gui.Font;
//#if MC >= 12000
import net.minecraft.client.gui.GuiGraphicsExtractor;
//#elseif MC >= 11500
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif
import net.minecraft.client.gui.screens.Screen;
//#if MC >= 12109
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
//#endif
import net.minecraft.network.chat.Component;

// TODO: refactor
public class ConfigScreen extends Screen {
    private final Screen parent;
    private final ConfigScreenViewModel viewModel;

    private final LayoutEngine layout;
    private final ConfigScreenRenderer renderer;
    private final ConfigInputHandler inputHandler;

    public ConfigScreen(Component title, Screen parent, ConfigScreenViewModel viewModel, LayoutEngine layout) {
        super(title);
        this.parent = parent;
        this.viewModel = viewModel;
        this.layout = layout;

        this.renderer = new ConfigScreenRenderer();
        this.inputHandler = new ConfigInputHandler(this, viewModel);
    }

    @Override
    protected void init() {
        layout.rebuild(width, height, getFont(), viewModel);
        renderer.rebuildWidgets(viewModel, layout);
    }

    public void close() {
        ScreenCompat.setScreen(this.minecraft, this.parent);
    }

    public void handleCloseOrConfirm() {
        if (viewModel.isDirty()) {
            // TODO: close or confirm
        } else {
            close();
        }
    }

    //#if MC >= 12102
    @Override
    //#endif
    public @NonNull Font getFont() {
        //#if MC >= 12102
        return super.getFont();
        //#else
        //$$ return super.font;
        //#endif
    }

    private void renderConfigScreen(GuiCompat gui, int mouseX, int mouseY, float partialTick) {
        renderer.render(gui, viewModel, layout, mouseX, mouseY, partialTick);
    }

    @Override
    // @formatter:off
    //#if MC >= 12106
    public void extractRenderState(@NonNull GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTick) {
        GuiCompat compat = new GuiCompat(gui);
        renderConfigScreen(compat, mouseX, mouseY, partialTick);
        super.extractRenderState(gui, mouseX, mouseY, partialTick);
    }
    //#elseif MC >= 12000
    //$$ public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        //#if MC >= 12002
        //$$ this.renderBackground(gui, mouseX, mouseY, partialTick);
        //#else
        //$$ this.renderBackground(gui);
        //#endif
    //$$    GuiCompat compat = new GuiCompat(gui);
    //$$    renderConfigScreen(compat, mouseX, mouseY, partialTick);
    //$$ }
    //#elseif MC >= 11600
    //$$ public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
    //$$    this.renderBackground(poseStack);
    //$$    GuiCompat compat = new GuiCompat(poseStack);
    //$$    renderConfigScreen(compat, mouseX, mouseY, partialTick);
    //$$ }
    //#else
    //$$ public void render(int mouseX, int mouseY, float partialTick) {
    //$$    this.renderBackground();
    //$$    GuiCompat compat = new GuiCompat();
    //$$    renderConfigScreen(compat, mouseX, mouseY, partialTick);
    //$$ }
    //#endif
    // @formatter:on

    @Override
    // @formatter:off
    //#if MC >= 12109
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        int button = event.button();
        double mouseX = event.x();
        double mouseY = event.y();
        int modifiers = event.modifiers();
    //#else
    //$$ public boolean mouseClicked(double mouseX, double mouseY, int button) {
    //$$     int modifiers = 0;
    //#endif
    // @formatter:on
        return inputHandler.mouseClicked(mouseX, mouseY, button, modifiers);
    }

    @Override
    // @formatter:off
    //#if MC >= 12109
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
    //#else
    //$$ public boolean mouseReleased(double mouseX, double mouseY, int button) {
    //#endif
    // @formatter:on
        return inputHandler.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    // @formatter:off
    //#if MC >= 12002
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return inputHandler.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
    //#else
    //$$ public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
    //$$     return inputHandler.mouseScrolled(mouseX, mouseY, 0.0f, scrollY);
    //$$ }
    //#endif
    // @formatter:on

    @Override
    // @formatter:off
    //#if MC >= 12109
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dragX, double dragY) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
    //#else
    //$$ public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
    //#endif
    // @formatter:on
        return inputHandler.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    // @formatter:off
    //#if MC >= 12109
    public boolean keyPressed(@NonNull KeyEvent event) {
        int keycode = event.key();
        int scancode = event.scancode();
        int modifiers = event.modifiers();
    //#else
    //$$ public boolean keyPressed(int keycode, int scancode, int modifiers) {
    //#endif
    // @formatter:on
        return inputHandler.keyPressed(keycode, scancode, modifiers);
    }

    @Override
    // @formatter:off
    //#if MC >= 12109
    public boolean charTyped(@NonNull CharacterEvent event) {
        int codepoint = event.codepoint();
        //#if MC >= 260000
        int modifiers = 0;
        //#else
        //$$ int modifiers = event.modifiers();
        //#endif
    //#else
    //$$ public boolean charTyped(char cp, int modifiers) {
    //$$     int codepoint = (int) cp;
    //#endif
    // @formatter:on
        return inputHandler.charTyped(codepoint, modifiers);
    }
}
