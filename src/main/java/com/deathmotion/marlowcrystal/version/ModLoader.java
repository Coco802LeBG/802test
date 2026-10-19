package com.deathmotion.marlowcrystal.version;

import org.jetbrains.annotations.Nullable;

public enum ModLoader {
    FABRIC(0, "fabric"),
    NEOFORGE(1, "neoforge");

    private final int networkId;
    private final String id;

    private ModLoader(int networkId, String id) {
        this.networkId = networkId;
        this.id = id;
    }

    @Nullable
    public static ModLoader byNetworkId(int networkId) {
        for (ModLoader loader : ModLoader.values()) {
            if (loader.networkId != networkId) continue;
            return loader;
        }
        return null;
    }

    public int networkId() {
        return this.networkId;
    }

    
    public String id() {
        return this.id;
    }
}

