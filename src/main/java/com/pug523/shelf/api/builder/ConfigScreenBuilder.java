package com.pug523.shelf.api.builder;

import com.pug523.shelf.core.config.Profile;
import com.pug523.shelf.ui.screen.ConfigScreen;
import com.pug523.shelf.ui.screen.OptionGroup;
import com.pug523.shelf.ui.screen.TabNode;
import com.pug523.shelf.ui.layout.LayoutConfig;
import com.pug523.shelf.ui.option.OptionWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConfigScreenBuilder {
    private final Component title;
    private final Screen parent;
    private final List<TabNode> roots = new ArrayList<>();
    private final List<Profile> profiles = new ArrayList<>();
    private Runnable onSave = () -> {
    };
    private LayoutConfig layoutConfig;

    private ConfigScreenBuilder(Component title, Screen parent) {
        this.title = title;
        this.parent = parent;
    }

    public static ConfigScreenBuilder create(Component title, Screen parent) {
        return new ConfigScreenBuilder(title, parent);
    }

    public ConfigScreenBuilder onSave(Runnable onSave) {
        this.onSave = onSave;
        return this;
    }

    public ConfigScreenBuilder layout(LayoutConfig layoutConfig) {
        this.layoutConfig = layoutConfig;
        return this;
    }

    public ConfigScreenBuilder addProfile(Profile profile) {
        this.profiles.add(profile);
        return this;
    }

    public ConfigScreenBuilder category(Component name, Consumer<CategoryBuilder> consumer) {
        TabNode rootNode = new TabNode(name);
        CategoryBuilder categoryBuilder = new CategoryBuilder(rootNode);
        consumer.accept(categoryBuilder);
        this.roots.add(rootNode);
        return this;
    }

    public ConfigScreen build() {
        return new ConfigScreen(title, parent, roots, profiles, onSave, layoutConfig);
    }

    public static class CategoryBuilder {
        private final TabNode node;

        public CategoryBuilder(TabNode node) {
            this.node = node;
        }

        public CategoryBuilder subCategory(Component name, Consumer<CategoryBuilder> consumer) {
            TabNode childNode = new TabNode(name);
            CategoryBuilder subBuilder = new CategoryBuilder(childNode);
            consumer.accept(subBuilder);
            this.node.addNode(childNode);
            return this;
        }

        public CategoryBuilder group(Component groupName, Consumer<GroupBuilder> consumer) {
            List<OptionWidget<?>> widgets = new ArrayList<>();
            GroupBuilder groupBuilder = new GroupBuilder(widgets);
            consumer.accept(groupBuilder);
            this.node.addGroup(new OptionGroup(groupName, widgets));
            return this;
        }
    }

    public static class GroupBuilder {
        private final List<OptionWidget<?>> widgets;

        public GroupBuilder(List<OptionWidget<?>> widgets) {
            this.widgets = widgets;
        }

        public GroupBuilder add(OptionWidget<?> widget) {
            this.widgets.add(widget);
            return this;
        }

        public GroupBuilder addAll(List<OptionWidget<?>> widgets) {
            this.widgets.addAll(widgets);
            return this;
        }
    }
}
