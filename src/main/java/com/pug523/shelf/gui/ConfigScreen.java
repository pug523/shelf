package com.pug523.shelf.gui;

import java.util.List;

import org.jspecify.annotations.NonNull;

import com.pug523.shelf.compat.GuiCompat;
import com.pug523.shelf.config.Profile;
import com.pug523.shelf.gui.builder.OptionContextBuilder;
import com.pug523.shelf.gui.controller.ConfigChangeController;
import com.pug523.shelf.gui.controller.OptionContextController;
import com.pug523.shelf.gui.controller.OptionFocusController;
import com.pug523.shelf.gui.controller.ScrollController;
import com.pug523.shelf.gui.controller.TabTreeController;
import com.pug523.shelf.gui.input.ConfigInputHandler;
import com.pug523.shelf.gui.layout.LayoutConfig;
import com.pug523.shelf.gui.layout.LayoutEngine;
import com.pug523.shelf.gui.renderer.ConfigScreenRenderer;
import com.pug523.shelf.gui.text.TextUtil;
import com.pug523.shelf.gui.widget.ActionButtonWidget;
import com.pug523.shelf.gui.widget.ClickableWidget;

import net.minecraft.client.gui.Font;
//#if MC >= 12000
import net.minecraft.client.gui.GuiGraphicsExtractor;
//#else
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif
import net.minecraft.client.gui.screens.Screen;
//#if MC >= 12109
import net.minecraft.client.input.MouseButtonEvent;
//#endif
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen {
    private final TabTreeController tabController;
    private final ScrollController scrollController;
    private final OptionContextController optionContextController;
    private final OptionFocusController focusController;
    private final ConfigChangeController changeController;

    private final OptionContextBuilder contextBuilder;
    private final ConfigScreenRenderer renderer;
    private final ConfigInputHandler input;

    private LayoutEngine layout;
    private final Screen parent;

    private final ActionButtonWidget undoButton;
    private final ActionButtonWidget applyButton;
    private final ActionButtonWidget doneButton;
    private final List<ClickableWidget> footerButtons;

    public ConfigScreen(Component title, Screen parent, List<TabNode> roots, List<Profile> profiles, Runnable onApply,
            LayoutConfig config) {
        super(title);
        this.parent = parent;

        this.tabController = new TabTreeController(roots);
        this.scrollController = new ScrollController();
        this.optionContextController = new OptionContextController();
        this.focusController = new OptionFocusController();
        this.changeController = new ConfigChangeController(roots, onApply);

        this.contextBuilder = new OptionContextBuilder();
        this.renderer = new ConfigScreenRenderer();
        this.input = new ConfigInputHandler(tabController, scrollController, optionContextController, focusController,
                changeController);

        this.layout = new LayoutEngine(config);
        this.undoButton = new ActionButtonWidget(TextUtil.guiText("undo"), btn -> this.changeController.undo());
        this.applyButton = new ActionButtonWidget(TextUtil.guiText("apply"), btn -> this.changeController.apply());
        this.doneButton = new ActionButtonWidget(TextUtil.guiText("done"), btn -> this.close());

        this.footerButtons = List.of(undoButton, applyButton, doneButton);
    }

    private void close() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }

    //#if MC >= 12102
    @Override
    //#endif
    public Font getFont() {
        //#if MC >= 12102
        return super.getFont();
        //#else
        //$$ return super.font;
        //#endif
    }

    @Override
    protected void init() {
        layout.rebuild(width, height);

        tabController.init();
        changeController.init();

        rebuildOptionContext();
    }

    private void rebuildOptionContext() {
        optionContextController.setContext(contextBuilder.build(tabController.getSelected()));
    }

    private void updateButtonStates() {
        boolean hasChanges = changeController.isDirty();
        this.undoButton.setEnabled(hasChanges);
        this.applyButton.setEnabled(hasChanges);
    }

    public List<ClickableWidget> getFooterButtons() {
        return footerButtons;
    }

    // @formatter:off
    //#if MC >= 12106
    @Override
    public void extractRenderState(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTick) {
        updateButtonStates();
        GuiCompat compat = new GuiCompat(gui);
        renderer.render(compat, this, layout, mouseX, mouseY, tabController, optionContextController.getContext(), focusController, scrollController);
        super.extractRenderState(gui, mouseX, mouseY, partialTick);
    }
    //#elseif MC >= 12000
    //$$ @Override
    //$$ public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
    //$$    super.render(gui, mouseX, mouseY, partialTick);
    //$$    updateButtonStates();
    //$$    GuiCompat compat = new GuiCompat(gui);
    //$$    renderer.render(compat, this, layout, mouseX, mouseY, tabController, optionContextController.getContext(), focusController, scrollController);
    //$$ }
    //#else
    //$$ @Override
    //$$ public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
    //$$    updateButtonStates();
    //$$    GuiCompat compat = new GuiCompat(poseStack);
    //$$    renderer.render(compat, this, layout, mouseX, mouseY, tabController, optionContextController.getContext(), focusController, scrollController);
    //$$    super.render(poseStack, mouseX, mouseY, partialTick);
    //$$ }
    //#endif
    // @formatter:on

    @Override
    // @formatter:off
    //#if MC >= 12109
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (super.mouseClicked(event, doubleClick)) return true;
        int button = event.button();
        double mouseX = event.x();
        double mouseY = event.y();
    //#else
    //$$ public boolean mouseClicked(double mouseX, double mouseY, int button) {
    //#endif
    // @formatter:on
        for (ClickableWidget btn : footerButtons) {
            if (btn.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }

        TabNode before = tabController.getSelected();

        boolean handled = input.mouseClicked(mouseX, mouseY, button, layout);

        if (before != tabController.getSelected()) {
            rebuildOptionContext();
        }

        if (handled) {
            return handled;
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
        input.mouseReleased(mouseX, mouseY, button);
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
        return input.mouseScrolled(mouseX, mouseY, scrollY, layout)
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
        if (super.mouseDragged(event, dragX, dragY)) return true;
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
    //#else
    //$$ public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
    //#endif
    // @formatter:on
        return input.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
}
