package com.deathmotion.marlowcrystal.loader;

import com.deathmotion.marlowcrystal.update.PublishedBuild;
import java.nio.file.Path;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public interface LoaderAccess {
    public Path configDirectory();

    public Optional<String> minecraftVersion();

    public void showUpdate(@Nullable PublishedBuild var1);
}

