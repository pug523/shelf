package com.pug523.shelf.gui;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

import com.pug523.shelf.Shelf;
import com.pug523.shelf.config.Option;
import com.pug523.shelf.config.Profile;
import com.pug523.shelf.gui.widget.OptionWidget;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

//#if MC >= 12109
import net.minecraft.client.input.MouseButtonEvent;
//#endif

public class ConfigScreen extends Screen {
    // Localization Keys Constants
    private static final String LANG_KEY_DEFAULT_PROFILE = "default_profile";
    private static final String LANG_KEY_SEARCH = "search";
    private static final String LANG_KEY_DONE = "done";
    private static final String LANG_KEY_APPLY = "apply";
    private static final String LANG_KEY_UNDO = "undo";
    private static final String LANG_KEY_SELECT_OPTION = "select_an_option";

    // Structural Component Offsets and Layout Dimensions Constants
    private static final int MOCK_DROPDOWN_X_OFFSET = 10;
    private static final int MOCK_DROPDOWN_Y = 5;
    private static final int MOCK_DROPDOWN_WIDTH = 120;
    private static final int MOCK_DROPDOWN_HEIGHT = 20;

    private static final int SEARCH_BOX_WIDTH = 120;
    private static final int SEARCH_BOX_HEIGHT = 20;
    private static final int SEARCH_BOX_RIGHT_MARGIN = 130;
    private static final int SEARCH_BOX_Y = 5;

    private static final int BUTTON_WIDTH = 50;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_BOTTOM_MARGIN = 25;
    private static final int DONE_BUTTON_RIGHT_MARGIN = 60;
    private static final int APPLY_BUTTON_RIGHT_MARGIN = 115;
    private static final int UNDO_BUTTON_RIGHT_MARGIN = 170;

    private static final int TEXT_PADDING_X = 10;
    private static final int TEXT_PADDING_Y = 10;
    private static final int SCROLLBAR_WIDTH = 2;
    private static final int SCROLLBAR_MIN_HEIGHT = 20;

    private static final int TAB_ITEM_START_OFFSET_Y = 30;

    private static final int OPTION_HEADER_OFFSET_X = 10;
    private static final int OPTION_ITEM_START_OFFSET_Y = 15;
    private static final int OPTION_TEXT_OFFSET_X = 15;

    private static final int DESC_TEXT_OFFSET_X = 10;
    private static final int DESC_TEXT_OFFSET_Y = 15;
    private static final int DESC_TEXT_RIGHT_PADDING = 20;

    private final Component title;
    private final Screen parent;
    private final Runnable onApply;
    private final LayoutConfig layoutConfig;
    private ScreenLayout layout;

    private final List<Profile> profiles;

    private boolean dirty = false;
    private final List<OptionWidget.Memento> undoSnapshots = new ArrayList<>();
    private int selectedProfileIndex = 0;

    // TabTree Architecture State Trackers
    private List<TabNode> rootTreeNodes = new ArrayList<>();
    private final List<TabNode> flattenedVisibleTabs = new ArrayList<>();
    private TabNode selectedTabNode = null;

    private int focusedOptionIndex = -1;
    private final List<RenderableItem> cachedOptionItems = new ArrayList<>();
    private double tabScrollY = 0;
    private double optionScrollY = 0;

    private EditBox searchBox;
    private Button doneButton;
    private Button applyButton;
    private Button undoButton;
    private Button profileDropdownMock;

    public ConfigScreen(Component title, Screen parent, List<TabNode> rootTreeNodes, List<Profile> profiles, Runnable onApply) {
        this(title, parent, rootTreeNodes, profiles, onApply, new LayoutConfig());
    }

    public ConfigScreen(Component title, Screen parent, List<TabNode> rootTreeNodes, List<Profile> profiles, Runnable onApply, LayoutConfig layoutConfig) {
        super(title);
        this.title = title;
        this.parent = parent;
        this.rootTreeNodes = rootTreeNodes;
        this.profiles = profiles;
        this.onApply = onApply;
        this.layoutConfig = layoutConfig;
    }

    @Override
    protected void init() {
        this.layout = new ScreenLayout(this.width, this.height, this.layoutConfig);

        if (profiles.isEmpty()) {
            profiles.add(new Profile(text(LANG_KEY_DEFAULT_PROFILE)));
        }

        Profile profile = getCurrentProfile();
        this.profileDropdownMock = Button.builder(profile.getName(), b -> {})
            .bounds(layout.tabAreaWidth + MOCK_DROPDOWN_X_OFFSET, MOCK_DROPDOWN_Y, MOCK_DROPDOWN_WIDTH, MOCK_DROPDOWN_HEIGHT).build();

        this.searchBox = new EditBox(this.font, layout.tabAreaWidth + layout.optionAreaWidth - SEARCH_BOX_RIGHT_MARGIN, SEARCH_BOX_Y, SEARCH_BOX_WIDTH, SEARCH_BOX_HEIGHT, text(LANG_KEY_SEARCH));

        this.doneButton = Button.builder(text(LANG_KEY_DONE), b -> this.minecraft.setScreen(this.parent))
            .bounds(this.width - DONE_BUTTON_RIGHT_MARGIN, this.height - BUTTON_BOTTOM_MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT).build();

        this.applyButton = Button.builder(text(LANG_KEY_APPLY), b -> applyChanges())
            .bounds(this.width - APPLY_BUTTON_RIGHT_MARGIN, this.height - BUTTON_BOTTOM_MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT).build();

        this.undoButton = Button.builder(text(LANG_KEY_UNDO), b -> undoChanges())
            .bounds(this.width - UNDO_BUTTON_RIGHT_MARGIN, this.height - BUTTON_BOTTOM_MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT).build();

        this.addRenderableWidget(this.profileDropdownMock);
        this.addRenderableWidget(this.searchBox);
        this.addRenderableWidget(this.doneButton);
        this.addRenderableWidget(this.applyButton);
        this.addRenderableWidget(this.undoButton);

        // Build visible linear list cache from tree roots
        updateFlattenedTabsCache();

        // Safe focus fallback
        if (selectedTabNode == null && !flattenedVisibleTabs.isEmpty()) {
            updateActiveTabContext(flattenedVisibleTabs.get(0));
        } else {
            refreshCurrentTabContext();
        }

        captureConfigSnapshot();
        updateButtonStates();
    }

    private void updateFlattenedTabsCache() {
        this.flattenedVisibleTabs.clear();
        for (TabNode node : rootTreeNodes) {
            node.flattenVisible(this.flattenedVisibleTabs, 0);
        }
    }

    private void updateActiveTabContext(TabNode newNode) {
        this.selectedTabNode = newNode;
        refreshCurrentTabContext();
    }

    private void refreshCurrentTabContext() {
        this.focusedOptionIndex = -1;
        this.optionScrollY = 0;
        this.cachedOptionItems.clear();

        if (selectedTabNode != null) {
            List<OptionGroup> aggregatedGroups = new ArrayList<>();

            // Pass an empty string as the base path so the selected node's name isn't repeated in the header
            collectOptionGroupsRecursive(selectedTabNode, "", aggregatedGroups);

            for (OptionGroup group : aggregatedGroups) {
                this.cachedOptionItems.add(RenderableItem.createHeader(group.getName()));
                for (Option<?> option : group.getOptions()) {
                    this.cachedOptionItems.add(RenderableItem.createOption(option));
                }
            }
        }
    }

    private void collectOptionGroupsRecursive(TabNode node, String currentPath, List<OptionGroup> destination) {
        for (OptionGroup group : node.getOptionGroups()) {
            Component fullyQualifiedTitle;

            if (currentPath.isEmpty()) {
                fullyQualifiedTitle = group.getName().copy().withStyle(ChatFormatting.BOLD);
            } else {
                String pathString = currentPath + " > ";
                fullyQualifiedTitle = Component.literal(pathString).withStyle(ChatFormatting.BOLD)
                    .append(group.getName().copy().withStyle(ChatFormatting.RESET, ChatFormatting.BOLD));
            }

            destination.add(new OptionGroup(fullyQualifiedTitle, group.getOptions()));
        }

        for (TabNode child : node.getChildren()) {
            // Only append to the path if it isn't the active selected node to prevent duplication.
            String nextPath = currentPath.isEmpty() ? child.getName().getString() : currentPath + " > " + child.getName().getString();
            collectOptionGroupsRecursive(child, nextPath, destination);
        }
    }

    private void applyChanges() {
        rootTreeNodes.stream()
            .flatMap(TabNode::streamAllNodes)
            .flatMap(node -> node.getOptionGroups().stream())
            .flatMap(group -> group.getOptionWidgets().stream())
            .map(OptionWidget::getOption)
            .filter(option -> option.isPendingModifiedFromActual())
            .forEach(option -> {
                option.applyPendingToActual();
            });
        onApply.run();
        this.dirty = false;
        updateButtonStates();
    }

    private void undoChanges() {
        if (!this.dirty) return;
        rootTreeNodes.stream()
            .flatMap(TabNode::streamAllNodes)
            .flatMap(node -> node.getOptionGroups().stream())
            .flatMap(group -> group.getOptionWidgets().stream())
            .map(OptionWidget::getOption)
            .filter(option -> option.isPendingModifiedFromActual())
            .forEach(option -> {
                option.discardPending();
            });

        this.dirty = false;
        updateButtonStates();

        // Refresh display states.
        refreshCurrentTabContext();
    }

    private void markDirty() {
        this.dirty = true;
        updateButtonStates();
    }

    private void updateButtonStates() {
        this.undoButton.active = this.dirty;
        this.undoButton.visible = this.dirty;
        this.applyButton.active = this.dirty;
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTick) {
        //#if MC <= 12105
        //$$ super.render(gui, mouseX, mouseY, partialTick);
        //#endif

        // Render Canvas Screen Base Tint
        gui.fill(0, 0, this.width, this.height, layoutConfig.colorScreenBaseBackground);

        // Render Frame Windows
        gui.fill(0, 0, this.width, layoutConfig.topBarHeight, layoutConfig.colorHeaderBackground);
        gui.fill(0, this.height - layoutConfig.bottomBarHeight, this.width, this.height, layoutConfig.colorFooterBackground);
        gui.fill(0, layoutConfig.topBarHeight, layout.tabAreaWidth, this.height - layoutConfig.bottomBarHeight, layoutConfig.colorTabPanelBackground);
        gui.fill(layout.tabAreaWidth, layoutConfig.topBarHeight, layout.descAreaX, this.height - layoutConfig.bottomBarHeight, layoutConfig.colorOptionPanelBackground);
        gui.fill(layout.descAreaX, layoutConfig.topBarHeight, this.width, this.height - layoutConfig.bottomBarHeight, layoutConfig.colorDescriptionPanelBackground);

        gui.text(this.font, this.title, TEXT_PADDING_X, TEXT_PADDING_Y, layoutConfig.colorTextPrimary, false);

        // 1: Render Tab Tree Directory Column (Left Panel)
        gui.enableScissor(0, layoutConfig.topBarHeight + 1, layout.tabAreaWidth, this.height - layoutConfig.bottomBarHeight);

        int tabContentHeight = flattenedVisibleTabs.size() * layoutConfig.tabItemHeight;
        this.tabScrollY = Mth.clamp(this.tabScrollY, 0, Math.max(0, tabContentHeight - layout.mainContentHeight));

        int tabTextVerticalOffset = (layoutConfig.tabItemHeight - this.font.lineHeight) / 2;

        for (int i = 0; i < flattenedVisibleTabs.size(); i++) {
            TabNode node = flattenedVisibleTabs.get(i);
            int yPos = layoutConfig.topBarHeight + TAB_ITEM_START_OFFSET_Y + (i * layoutConfig.tabItemHeight) - (int) tabScrollY;
            int color = (node == selectedTabNode) ? layoutConfig.colorItemSelectedText : layoutConfig.colorItemUnselectedText;

            // Indent padding shift derived dynamically from current tree depth
            int drawX = TEXT_PADDING_X + (node.getDepth() * 10);

            if (node == selectedTabNode) {
                gui.fill(0, yPos, layout.tabAreaWidth, yPos + layoutConfig.tabItemHeight, layoutConfig.colorItemSelectedBackground);
            }

            int itemCenterY = yPos + (layoutConfig.tabItemHeight / 2);

            if (node.hasChildren()) {
                if (node.isExpanded()) {
                    RenderUtil.renderDownwardArrow(gui, drawX + 2, itemCenterY - 3, color);
                } else {
                    RenderUtil.renderRightwardArrow(gui, drawX + 3, itemCenterY - 4, color);
                }
            }

            drawX += 12;
            gui.text(this.font, node.getName(), drawX, yPos + tabTextVerticalOffset, color, false);
        }
        gui.disableScissor();
        drawScrollBar(gui, layout.tabAreaWidth - SCROLLBAR_WIDTH, layoutConfig.topBarHeight + 1, layout.mainContentHeight, tabScrollY, tabContentHeight + TAB_ITEM_START_OFFSET_Y);

        // 2: Render Configuration Content Options Grid (Middle Panel)
        gui.enableScissor(layout.tabAreaWidth + 1, layoutConfig.topBarHeight + 1, layout.descAreaX, this.height - layoutConfig.bottomBarHeight);

        int totalExtraPadding = 0;
        for (int i = 0; i < cachedOptionItems.size(); i++) {
            if (cachedOptionItems.get(i).isHeader() && i > 0) {
                totalExtraPadding += 12;
            }
        }
        int optionContentHeight = (cachedOptionItems.size() * layoutConfig.optionItemHeight) + totalExtraPadding;
        this.optionScrollY = Mth.clamp(this.optionScrollY, 0, Math.max(0, optionContentHeight - layout.mainContentHeight));

        int optionTextVertOffset = (layoutConfig.optionItemHeight - this.font.lineHeight) / 2;
        int extraPadding = 0;

        for (int i = 0; i < cachedOptionItems.size(); i++) {
            RenderableItem renderItem = cachedOptionItems.get(i);

            // Inject 12 pixels of extra space before headers (except the very first one)
            if (renderItem.isHeader() && i > 0) {
                extraPadding += 12;
            }

            int yPos = layoutConfig.topBarHeight + OPTION_ITEM_START_OFFSET_Y + (i * layoutConfig.optionItemHeight) + extraPadding - (int) optionScrollY;

            if (renderItem.isHeader()) {
                //#if MC >= 260000
                gui.text(this.font, renderItem.text(), layout.tabAreaWidth + OPTION_HEADER_OFFSET_X, yPos + optionTextVertOffset, layoutConfig.colorTextMuted, false);
                //#else
                //$$ gui.drawString(this.font, renderItem.text(), layout.tabAreaWidth + OPTION_HEADER_OFFSET_X, yPos + optionTextVertOffset, layoutConfig.colorTextMuted, false);
                //#endif
            } else {
                Option<?> option = renderItem.option();
                int color = (i == focusedOptionIndex) ? layoutConfig.colorItemSelectedText : layoutConfig.colorItemUnselectedText;

                // Handle interactive mouse state bounding over specific active rows
                if (mouseX >= layout.tabAreaWidth && mouseX < layout.descAreaX && mouseY >= yPos && mouseY < yPos + layoutConfig.optionItemHeight) {
                    gui.fill(layout.tabAreaWidth + 1, yPos, layout.descAreaX, yPos + layoutConfig.optionItemHeight, layoutConfig.colorItemHoverBackground);
                }
                if (i == focusedOptionIndex) {
                    gui.fill(layout.tabAreaWidth + 1, yPos, layout.descAreaX, yPos + layoutConfig.optionItemHeight, layoutConfig.colorItemSelectedBackground);
                }

                // Render dynamic generic option display name label
                gui.text(this.font, option.getName(), layout.tabAreaWidth + OPTION_TEXT_OFFSET_X, yPos + optionTextVertOffset, color, false);

                // Safely handoff rendering pipeline control parameters to injected layout widget decoupling
                OptionWidget widget = option.getWidget();
                if (widget != null) {
                    widget.render(this.font, gui, layout.tabAreaWidth, yPos, layout.optionAreaWidth, layoutConfig.optionItemHeight, mouseX, mouseY);
                }
            }
        }
        gui.disableScissor();

        drawScrollBar(gui, layout.descAreaX - SCROLLBAR_WIDTH, layoutConfig.topBarHeight + 1, layout.mainContentHeight, optionScrollY, optionContentHeight + OPTION_ITEM_START_OFFSET_Y);

        // 3: Render Selection Help Text Documentation Sidebar (Right Panel)
        if (focusedOptionIndex >= 0 && focusedOptionIndex < cachedOptionItems.size()) {
            RenderableItem activeSelection = cachedOptionItems.get(focusedOptionIndex);

            if (!activeSelection.isHeader() && activeSelection.option() != null) {
                Component name = activeSelection.option().getName();
                Component description = activeSelection.option().getDescription();

                int startX = layout.descAreaX + DESC_TEXT_OFFSET_X;
                int startY = layoutConfig.topBarHeight + DESC_TEXT_OFFSET_Y;
                int maxTextWidth = this.width - layout.descAreaX - DESC_TEXT_RIGHT_PADDING;

                gui.text(this.font, name.copy().withStyle(ChatFormatting.BOLD), startX, startY, layoutConfig.colorTextPrimary, false);

                // + 12px extra spacing gap
                int descriptionOffsetY = startY + this.font.lineHeight + 12;

                // Render descriptive body context
                gui.textWithWordWrap(this.font, description, startX, descriptionOffsetY, maxTextWidth, layoutConfig.colorTextSecondary);
            } else {
                gui.text(this.font, text(LANG_KEY_SELECT_OPTION), layout.descAreaX + DESC_TEXT_OFFSET_X, layoutConfig.topBarHeight + DESC_TEXT_OFFSET_Y, layoutConfig.colorTextDisabled, false);
            }
        } else {
            gui.text(this.font, text(LANG_KEY_SELECT_OPTION), layout.descAreaX + DESC_TEXT_OFFSET_X, layoutConfig.topBarHeight + DESC_TEXT_OFFSET_Y, layoutConfig.colorTextDisabled, false);
        }

        //#if MC >= 12106
        super.extractRenderState(gui, mouseX, mouseY, partialTick);
        //#endif
    }

    private void drawScrollBar(GuiGraphicsExtractor gui, int x, int y, int height, double scroll, int contentHeight) {
        if (contentHeight <= height) return;
        int barHeight = Math.max(SCROLLBAR_MIN_HEIGHT, (int) ((height / (float) contentHeight) * height));
        int maxScroll = contentHeight - height;
        int barY = y + (int) ((scroll / maxScroll) * (height - barHeight));

        gui.fill(x, y, x + SCROLLBAR_WIDTH, y + height, layoutConfig.colorScrollBarTrack);
        gui.fill(x, barY, x + SCROLLBAR_WIDTH, barY + barHeight, layoutConfig.colorScrollBarThumb);
    }

    @Override
    //#if MC >= 12109
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (super.mouseClicked(event, doubleClick)) return true;
        int button = event.button();
        double mouseX = event.x();
        double mouseY = event.y();
    //#else
    //$$ public boolean mouseClicked(double mouseX, double mouseY, int button) {
    //#endif
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && layout.isWithinContentArea(mouseY)) {
            if (layout.isMouseOverTabs(mouseX)) {
                for (int i = 0; i < flattenedVisibleTabs.size(); i++) {
                    TabNode node = flattenedVisibleTabs.get(i);
                    int yPos = layoutConfig.topBarHeight + TAB_ITEM_START_OFFSET_Y + (i * layoutConfig.tabItemHeight) - (int) tabScrollY;

                    if (mouseY >= yPos && mouseY < yPos + layoutConfig.tabItemHeight) {
                        int toggleStartX = TEXT_PADDING_X + (node.getDepth() * 10);

                        // Trigger visual branch node toggle expands
                        if (node.hasChildren() && mouseX >= toggleStartX && mouseX <= toggleStartX + 16) {
                            node.toggleExpanded();
                            updateFlattenedTabsCache();
                        } else {
                            updateActiveTabContext(node);
                        }
                        return true;
                    }
                }
            }
            else if (layout.isMouseOverOptions(mouseX)) {
                int extraPadding = 0;
                for (int i = 0; i < cachedOptionItems.size(); i++) {
                    RenderableItem item = cachedOptionItems.get(i);

                    if (item.isHeader() && i > 0) {
                        extraPadding += 12;
                    }

                    // Header click boundaries are processed to keep padding aligned, but input handling is skipped.
                    if (item.isHeader()) continue;

                    int yPos = layoutConfig.topBarHeight + OPTION_ITEM_START_OFFSET_Y + (i * layoutConfig.optionItemHeight) + extraPadding - (int) optionScrollY;
                    if (mouseY >= yPos && mouseY < yPos + layoutConfig.optionItemHeight) {
                        this.focusedOptionIndex = i;
                        Option<?> option = item.option();

                        if (option != null) {
                            int resetBtnX = layout.descAreaX - RESET_BUTTON_WIDTH - 6;
                            int resetBtnY = yPos + (layoutConfig.optionItemHeight - 16) / 2;

                            // Click Collision Interception: Check if user targeted the row reset button specifically
                            if (mouseX >= resetBtnX && mouseX < resetBtnX + RESET_BUTTON_WIDTH && mouseY >= resetBtnY && mouseY < resetBtnY + 16) {
                                if (option.isModified()) {
                                    option.resetToDefault();
                                    markDirty();
                                }
                                return true;
                            }

                            // Regular fallback: pass event downward into specific active Widget configuration inputs
                            if (option.getWidget() != null && option.getWidget().mouseClicked(mouseX, mouseY, button)) {
                                markDirty();
                                return true;
                            }
                        }
                    }
                }
            }
        }
        //#if MC >= 12109
        return super.mouseClicked(event, doubleClick);
        //#else
        //$$ return super.mouseClicked(mouseX, mouseY, button);
        //#endif
    }

    @Override
    //#if MC >= 12109
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        int button = event.button();
        double mouseX = event.x();
        double mouseY = event.y();
    //#else
    //$$ public boolean mouseReleased(double mouseX, double mouseY, int button) {
    //#endif
        for (RenderableItem item : cachedOptionItems) {
            if (!item.isHeader() && item.option() != null && item.option().getWidget() != null) {
                item.option().getWidget().mouseReleased(mouseX, mouseY, button);
            }
        }
        //#if MC >= 12109
        return super.mouseReleased(event);
        //#else
        //$$ return super.mouseReleased(mouseX, mouseY, button);
        //#endif
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (layout.isWithinContentArea(mouseY)) {
            if (layout.isMouseOverTabs(mouseX)) {
                this.tabScrollY -= scrollY * layoutConfig.tabScrollSpeed;
                return true;
            } else if (layout.isMouseOverOptions(mouseX)) {
                this.optionScrollY -= scrollY * layoutConfig.optionScrollSpeed;
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    //#if MC >= 12109
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dragX, double dragY) {
        if (super.mouseDragged(event, dragX, dragY)) return true;
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
    //#else
    //$$ public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
    //#endif
        if (focusedOptionIndex >= 0 && focusedOptionIndex < cachedOptionItems.size()) {
            RenderableItem item = cachedOptionItems.get(focusedOptionIndex);
            if (!item.isHeader() && item.option() != null && item.option().getWidget() != null) {
                if (item.option().getWidget().mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
                    markDirty();
                    return true;
                }
            }
        }
        //#if MC >= 12109
        return super.mouseDragged(event, dragX, dragY);
        //#else
        //$$ return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        //#endif
    }

    private Profile getCurrentProfile() {
        return profiles.get(selectedProfileIndex);
    }

    private static Component text(String languageKey) {
        return Component.translatable(Shelf.MOD_ID + ".lib.config.gui." + languageKey);
    }
}
