package com.deathmotion.marlowcrystal.update;

import com.deathmotion.marlowcrystal.update.ReleaseChannel;
import com.deathmotion.marlowcrystal.version.ModVersion;

public record PublishedBuild(ModVersion version, String downloadUrl, ReleaseChannel channel) {
}

