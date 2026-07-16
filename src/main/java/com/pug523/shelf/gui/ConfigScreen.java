package com.pug523.shelf.gui;

import java.util.List;

import com.pug523.shelf.gui.controller.*;
import com.pug523.shelf.gui.widget.SearchBarWidget;
import org.jspecify.annotations.NonNull;

import com.pug523.shelf.compat.GuiCompat;
import com.pug523.shelf.compat.JavaCompat;
import com.pug523.shelf.compat.ScreenCompat;
import com.pug523.shelf.config.Profile;
import com.pug523.shelf.gui.input.ConfigInputHandler;
import com.pug523.shelf.gui.layout.LayoutConfig;
import com.pug523.shelf.gui.layout.LayoutEngine;
import com.pug523.shelf.gui.model.OptionContextBuilder;
import com.pug523.shelf.gui.renderer.ConfigScreenRenderer;
import com.pug523.shelf.gui.text.TextUtil;
import com.pug523.shelf.gui.widget.ActionButtonWidget;
import com.pug523.shelf.gui.widget.ClickableWidget;

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

public class ConfigScreen extends Screen {
    private final Screen parent;
    private final LayoutEngine layout;

    private final TabTreeController tabController;
    private final ScrollController scrollController;
    private final OptionContextController optionContextController;
    private final OptionFocusController focusController;
    private final ConfigChangeController changeController;
    private final OverlayController overlayController;
    private final SearchBarController searchBarController;

    private final SearchBarWidget searchBarWidget;

    private final OptionContextBuilder contextBuilder;
    private final ConfigScreenRenderer renderer;
    private final ConfigInputHandler input;

    private final List<ActionButtonWidget> footerButtons;

    public ConfigScreen(Component title, Screen parent, List<TabNode> roots, List<Profile> profiles, Runnable onApply,
                        LayoutConfig layoutConfig) {
        super(title);
        this.parent = parent;
        this.layout = new LayoutEngine(layoutConfig);

        this.tabController = new TabTreeController(roots);
        this.scrollController = new ScrollController();
        this.optionContextController = new OptionContextController();
        this.focusController = new OptionFocusController();
        this.changeController = new ConfigChangeController(roots, onApply);
        this.overlayController = new OverlayController();
        this.searchBarController = new SearchBarController();

        this.searchBarController.setOnQueryChanged(str -> this.scrollController.reset());
        this.searchBarWidget = new SearchBarWidget(this.searchBarController);

        this.contextBuilder = new OptionContextBuilder();
        this.renderer = new ConfigScreenRenderer();
        this.input = new ConfigInputHandler(tabController, scrollController, optionContextController, focusController, overlayController, changeController, searchBarWidget);

        ActionButtonWidget undoButton = new ActionButtonWidget(TextUtil.guiText("undo"), btn -> this.changeController.undo());
        ActionButtonWidget applyButton = new ActionButtonWidget(TextUtil.guiText("apply"), btn -> this.changeController.apply());
        ActionButtonWidget doneButton = new ActionButtonWidget(TextUtil.guiText("done"), btn -> this.changeController.closeOrConfirm(this));

        this.footerButtons = JavaCompat.listOf(undoButton, applyButton, doneButton);
    }

    public void close() {
        ScreenCompat.setScreen(this.minecraft, this.parent);
    }

    public OverlayController getOverlayController() {
        return this.overlayController;
    }

    public ConfigChangeController getChangeController() {
        return this.changeController;
    }

    public SearchBarWidget getSearchBarWidget() {
        return this.searchBarWidget;
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

    @Override
    protected void init() {
        tabController.init();

        rebuildOptionContext();

        layout.rebuild(width, height, getFont(), footerButtons, tabController.getFlat(),
            optionContextController.getContext().items());
        renderer.rebuildWidgets(optionContextController.getContext(), layout);
        searchBarController.setMasterData(optionContextController.getContext().items(), tabController.getFlat());
    }

    private void rebuildOptionContext() {
        optionContextController.setContext(contextBuilder.build(tabController.getSelected()));
    }

    public List<ActionButtonWidget> getFooterButtons() {
        return footerButtons;
    }

    private void renderConfigScreen(GuiCompat gui, int mouseX, int mouseY, float partialTick) {
        renderer.render(gui, this, layout, mouseX, mouseY, partialTick, tabController, optionContextController.getContext(),
            focusController, scrollController, changeController, overlayController, searchBarController);
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
    //$$    // super.render(gui, mouseX, mouseY, partialTick);
    //$$ }
    //#elseif MC >= 11600
    //$$ public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
    //$$    this.renderBackground(poseStack);
    //$$    GuiCompat compat = new GuiCompat(poseStack);
    //$$    renderConfigScreen(compat, mouseX, mouseY, partialTick);
    //$$    // super.render(poseStack, mouseX, mouseY, partialTick);
    //$$ }
    //#else
    //$$ public void render(int mouseX, int mouseY, float partialTick) {
    //$$    this.renderBackground();
    //$$    GuiCompat compat = new GuiCompat();
    //$$    renderConfigScreen(compat, mouseX, mouseY, partialTick);
    //$$    // super.render(mouseX, mouseY, partialTick);
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
        for (ClickableWidget btn : footerButtons) {
            if (btn.mouseClicked(mouseX, mouseY, button, modifiers, layout)) {
                return true;
            }
        }

        TabNode before = tabController.getSelected();
        boolean result = input.mouseClicked(mouseX, mouseY, button, modifiers, layout);

        if (before != tabController.getSelected()) {
            rebuildOptionContext();
            layout.rebuild(this.width, this.height, this.getFont(), this.footerButtons, this.tabController.getFlat(),
                this.optionContextController.getContext().items());
        }

        if (result) {
            return true;
        }
        //#if MC >= 12109
        return super.mouseClicked(event, doubleClick);
        //#else
        //$$ return super.mouseClicked(mouseX, mouseY, button);
        //#endif
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
        input.mouseReleased(mouseX, mouseY, button, layout);
        //#if MC >= 12109
        return super.mouseReleased(event);
        //#else
        //$$ return super.mouseReleased(mouseX, mouseY, button);
        //#endif
    }

    @Override
    // @formatter:off
    //#if MC >= 12002
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return input.mouseScrolled(mouseX, mouseY, scrollX, scrollY, layout)
                || super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
    //#else
    //$$ public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
    //$$     return input.mouseScrolled(mouseX, mouseY, scrollY, layout)
    //$$             || super.mouseScrolled(mouseX, mouseY, scrollY);
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
        return input.mouseDragged(mouseX, mouseY, button, dragX, dragY, layout);
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
        return input.keyPressed(keycode, scancode, modifiers, layout);
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
        return input.charTyped(codepoint, modifiers, layout);
    }
}
