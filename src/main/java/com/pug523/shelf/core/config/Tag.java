package com.pug523.shelf.core.config;

import net.minecraft.network.chat.Component;

public class Tag {
    private final Component name;

    public Tag(Component name) {
        this.name = name;
    }

    public Component getName() {
        return name;
    }
}
