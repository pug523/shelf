package com.pug523.shelf.core.config;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.client.Minecraft;

public class KeybindManager {
    private static final Set<Integer> ACTIVE_KEYS = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static final List<KeyAction> KEYBINDS = new CopyOnWriteArrayList<>();

    public static class KeyAction {
        private final List<Integer> keycodes;
        private final KeyContext context;
        private final Runnable action;

        public KeyAction(List<Integer> keycodes, KeyContext context, Runnable action) {
            if (keycodes == null || keycodes.isEmpty()) {
                throw new IllegalArgumentException("Key codes list cannot be null or empty.");
            }
            this.keycodes = new ArrayList<>(keycodes);
            this.context = context;
            this.action = action;
        }

        public List<Integer> getKeycodes() {
            return keycodes;
        }

        public KeyContext getContext() {
            return context;
        }

        public void execute() {
            this.action.run();
        }

        public boolean isTriggered(int triggerKey, Set<Integer> activeKeys) {
            int lastExpectedKey = keycodes.get(keycodes.size() - 1);
            if (lastExpectedKey != triggerKey) {
                return false;
            }

            for (int keycode : keycodes) {
                if (!activeKeys.contains(keycode)) {
                    return false;
                }
            }

            // if (activeKeys.size() != keycodes.size()) return false;

            return true;
        }
    }

    public static void register(List<Integer> keycodes, KeyContext context, Runnable action) {
        KEYBINDS.add(new KeyAction(keycodes, context, action));
    }

    public static void registerSingle(int keycode, KeyContext context, Runnable action) {
        register(Collections.singletonList(keycode), context, action);
    }

    public static void clearKeybinds() {
        KEYBINDS.clear();
    }

    public static boolean onKeyPressed(int keycode) {
        ACTIVE_KEYS.add(keycode);

        Minecraft mc = Minecraft.getInstance();

        for (KeyAction keyAction : KEYBINDS) {
            if (keyAction.getContext().isActive(mc) && keyAction.isTriggered(keycode, ACTIVE_KEYS)) {
                mc.execute(keyAction::execute);
                return true;
            }
        }

        return false;
    }

    public static void onKeyReleased(int keycode) {
        ACTIVE_KEYS.remove(keycode);
    }

    public static void releaseAll() {
        ACTIVE_KEYS.clear();
    }
}
