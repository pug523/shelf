package com.pug523.shelf.gui.controller;

import java.util.List;

import com.pug523.shelf.gui.model.OptionContext;

public final class OptionContextController {

    private OptionContext context = new OptionContext(List.of());

    public OptionContext getContext() {
        return context;
    }

    public void setContext(OptionContext context) {
        this.context = context;
    }
}
