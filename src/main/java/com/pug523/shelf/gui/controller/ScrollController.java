package com.pug523.shelf.gui.controller;

import net.minecraft.util.Mth;

public final class ScrollController {

    private double tabScroll;
    private double optionScroll;

    public double getTabScroll() {
        return tabScroll;
    }

    public double getOptionScroll() {
        return optionScroll;
    }

    public void scrollTabs(double amount, int contentHeight, int viewportHeight) {
        tabScroll = Mth.clamp(tabScroll + amount, 0, Math.max(0, contentHeight - viewportHeight));
    }

    public void scrollOptions(double amount, int contentHeight, int viewportHeight) {
        optionScroll = Mth.clamp(optionScroll + amount, 0, Math.max(0, contentHeight - viewportHeight));
    }

    public void setTabScroll(double value, int contentHeight, int viewportHeight) {
        tabScroll = Mth.clamp(value, 0, Math.max(0, contentHeight - viewportHeight));
    }

    public void setOptionScroll(double value, int contentHeight, int viewportHeight) {
        optionScroll = Mth.clamp(value, 0, Math.max(0, contentHeight - viewportHeight));
    }
}
