package com.deathmotion.marlowcrystal.version;

import com.deathmotion.marlowcrystal.version.ModLoader;
import com.deathmotion.marlowcrystal.version.ModVersion;
import java.time.Instant;

public final class CurrentBuild {
    public static final ModVersion VERSION = new ModVersion(3, 0, 0, false, "d66f534");
    public static final String MINECRAFT_RANGE = "1.21.11";
    public static final ModLoader LOADER = ModLoader.FABRIC;
    public static final String COMMIT = "d66f53423636a276d9c8b8da3dc9b180586d022d";
    public static final boolean DIRTY = true;
    public static final Instant TIMESTAMP = Instant.ofEpochMilli(1790529447334L);

    private CurrentBuild() {
        throw new IllegalStateException();
    }
}

