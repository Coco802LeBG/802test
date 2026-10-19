package com.deathmotion.marlowcrystal.update;

import com.deathmotion.marlowcrystal.Log;
import com.deathmotion.marlowcrystal.config.Settings;
import com.deathmotion.marlowcrystal.config.UpdateSource;
import com.deathmotion.marlowcrystal.loader.LoaderAccess;
import com.deathmotion.marlowcrystal.update.BuildSource;
import com.deathmotion.marlowcrystal.update.PublishedBuild;
import com.deathmotion.marlowcrystal.update.ReleaseChannel;
import com.deathmotion.marlowcrystal.update.source.GitHubSource;
import com.deathmotion.marlowcrystal.update.source.ModrinthSource;
import com.deathmotion.marlowcrystal.version.CurrentBuild;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class UpdateCheck {
    private final Settings settings;
    private final LoaderAccess loader;
    private final Map<UpdateSource, List<PublishedBuild>> builds = new ConcurrentHashMap<UpdateSource, List<PublishedBuild>>();

    public UpdateCheck(Settings settings, LoaderAccess loader) {
        this.settings = settings;
        this.loader = loader;
    }

    private static BuildSource sourceFor(UpdateSource source) {
        return switch (source) {
            default -> throw new MatchException(null, null);
            case UpdateSource.MODRINTH -> new ModrinthSource();
            case UpdateSource.GITHUB -> new GitHubSource();
        };
    }

    public synchronized void run() {
        UpdateSource source = this.settings.getUpdateSource();
        if (!this.builds.containsKey((Object)source)) {
            try {
                this.builds.put(source, this.fetch(source));
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        Optional<PublishedBuild> update = this.available();
        update.ifPresent(build -> Log.info("Version " + build.version().toDisplayString() + " is available at " + build.downloadUrl()));
        this.loader.showUpdate(update.orElse(null));
    }

    public Optional<PublishedBuild> available() {
        List<PublishedBuild> published = this.builds.get((Object)this.settings.getUpdateSource());
        if (published == null) {
            return Optional.empty();
        }
        return published.stream().filter(build -> this.settings.isExperimentalBuilds() || build.channel() == ReleaseChannel.RELEASE).max(Comparator.comparing(PublishedBuild::version)).filter(build -> build.version().isNewerThan(CurrentBuild.VERSION));
    }

    public void settingsSaved() {
        if (this.builds.isEmpty()) {
            return;
        }
        if (this.builds.containsKey((Object)this.settings.getUpdateSource())) {
            this.loader.showUpdate(this.available().orElse(null));
        } else {
            CompletableFuture.runAsync(this::run);
        }
    }

    private List<PublishedBuild> fetch(UpdateSource source) throws InterruptedException {
        Optional<String> minecraftVersion = this.loader.minecraftVersion();
        if (minecraftVersion.isEmpty()) {
            return List.of();
        }
        Log.info("Checking " + source.displayName() + " for updates");
        try {
            return UpdateCheck.sourceFor(source).builds(minecraftVersion.get());
        }
        catch (IOException | RuntimeException e) {
            Log.warn("Could not check " + source.displayName() + " for updates: " + e.getMessage());
            return List.of();
        }
    }
}

