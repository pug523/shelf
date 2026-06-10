package com.pug523.shelf.config;

public interface ConfigManager<T> {
    T getConfig();

    void load();

    void save();
}
