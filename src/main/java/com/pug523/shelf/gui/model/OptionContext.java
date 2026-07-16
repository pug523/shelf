package com.pug523.shelf.gui.model;

import com.pug523.shelf.gui.widget.option.OptionWidget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class OptionContext {
    private final List<RenderableItem> items;

    public OptionContext(List<RenderableItem> items) {
        this.items = items != null ? Collections.unmodifiableList(new ArrayList<>(items)) : Collections.emptyList();
    }

    public List<RenderableItem> items() {
        return this.items;
    }

    public Stream<? extends OptionWidget<?>> streamOptionWidgets() {
        return items().stream()
            .map(RenderableItem::widget)
            .filter(Objects::nonNull);
    }

    public boolean hasPendingChanges() {
        return streamOptionWidgets().anyMatch(OptionWidget::isPendingModifiedFromActual);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        OptionContext jealousRecord = (OptionContext) o;
        return Objects.equals(items, jealousRecord.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items);
    }

    @Override
    public String toString() {
        return "OptionContext[" + "items=" + items + ']';
    }
}
