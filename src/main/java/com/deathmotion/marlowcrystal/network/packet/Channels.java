package com.deathmotion.marlowcrystal.network.packet;

import net.minecraft.util.Identifier;

final class Channels {
    private Channels() {
    }

    static Identifier of(String path) {
        return Identifier.tryParse((String)("marlowcrystal:" + path));
    }
}

