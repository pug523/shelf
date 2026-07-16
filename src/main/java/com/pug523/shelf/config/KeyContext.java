package com.pug523.shelf.config;

import com.pug523.shelf.compat.ScreenCompat;
import net.minecraft.client.Minecraft;

public enum KeyContext {
    IN_GAME {
        @Override
        public boolean isActive(Minecraft mc) {
            return ScreenCompat.getScreen(mc) == null;
        }
    },
    IN_GUI {
        @Override
        public boolean isActive(Minecraft mc) {
            return ScreenCompat.getScreen(mc) != null;
        }
    },
    ANYWHERE {
        @Override
        public boolean isActive(Minecraft mc) {
            return true;
        }
    };

    public abstract boolean isActive(Minecraft mc);
}
