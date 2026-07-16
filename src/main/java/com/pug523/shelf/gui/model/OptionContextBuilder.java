package com.pug523.shelf.gui.model;

import java.util.ArrayList;
import java.util.List;

import com.pug523.shelf.compat.ComponentCompat;
import com.pug523.shelf.gui.OptionGroup;
import com.pug523.shelf.gui.TabNode;
import com.pug523.shelf.gui.widget.option.OptionWidget;

import net.minecraft.network.chat.Component;

public class OptionContextBuilder {

    public OptionContext build(TabNode root) {
        List<RenderableItem> items = new ArrayList<>();
        if (root == null) {
            return new OptionContext(items);
        }

        collect(root, "", items);
        return new OptionContext(items);
    }

    private void collect(TabNode node, String path, List<RenderableItem> out) {
        for (OptionGroup group : node.getOptionGroups()) {
            Component title = path.isEmpty() ? group.getName()
                    : ComponentCompat.literal(path + " > ").append(group.getName());
            out.add(RenderableItem.header(title));
            for (OptionWidget<?> w : group.getOptionWidgets()) {
                out.add(RenderableItem.option(w));
            }
        }

        for (TabNode child : node.getChildren()) {
            String next = path.isEmpty() ? child.getName().getString() : path + " > " + child.getName().getString();
            collect(child, next, out);
        }
    }
}
