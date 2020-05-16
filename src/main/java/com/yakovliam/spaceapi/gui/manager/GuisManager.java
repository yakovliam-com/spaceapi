package com.yakovliam.spaceapi.gui.manager;

import com.yakovliam.spaceapi.gui.Gui;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuisManager {

    private static Map<UUID, Gui> openGuis = new HashMap<>();

    public static void add(UUID u, Gui g) {
        openGuis.put(u, g);
    }

    public static void close(UUID u) {
        openGuis.remove(u);
    }

    public static boolean hasOpen(UUID u) {
        return openGuis.containsKey(u);
    }

    public static Gui get(UUID u) {
        if (openGuis.containsKey(u)) return openGuis.get(u);
        return null;
    }
}
