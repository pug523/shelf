package com.pug523.shelf.config;

import java.io.Serializable;

public interface ConfigManager<T extends Serializable> {
    T getConfig();

    void load();

    void save();
}
