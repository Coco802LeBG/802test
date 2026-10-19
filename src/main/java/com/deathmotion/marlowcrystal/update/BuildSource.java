package com.deathmotion.marlowcrystal.update;

import com.deathmotion.marlowcrystal.update.PublishedBuild;
import java.io.IOException;
import java.util.List;

public interface BuildSource {
    public List<PublishedBuild> builds(String var1) throws IOException, InterruptedException;
}

