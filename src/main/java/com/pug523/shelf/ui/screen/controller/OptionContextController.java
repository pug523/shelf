package com.pug523.shelf.ui.screen.controller;

import com.pug523.shelf.common.compat.JavaCompat;
import com.pug523.shelf.ui.model.OptionContext;

// TODO: migrate to ConfigScreenViewModel
public final class OptionContextController {

    private OptionContext context = new OptionContext(JavaCompat.listOf());

    public OptionContext getContext() {
        return context;
    }

    public void setContext(OptionContext context) {
        this.context = context;
    }
}
