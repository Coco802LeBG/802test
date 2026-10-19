package com.deathmotion.marlowcrystal.update;

public enum ReleaseChannel {
    RELEASE,
    BETA,
    ALPHA;


    public static ReleaseChannel fromName(String name) {
        for (ReleaseChannel channel : ReleaseChannel.values()) {
            if (!channel.name().equalsIgnoreCase(name)) continue;
            return channel;
        }
        return RELEASE;
    }
}

