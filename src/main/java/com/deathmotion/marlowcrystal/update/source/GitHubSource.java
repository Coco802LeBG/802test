package com.deathmotion.marlowcrystal.update.source;

import com.deathmotion.marlowcrystal.update.BuildSource;
import com.deathmotion.marlowcrystal.update.PublishedBuild;
import com.deathmotion.marlowcrystal.update.ReleaseChannel;
import com.deathmotion.marlowcrystal.update.source.JsonHttp;
import com.deathmotion.marlowcrystal.version.CurrentBuild;
import com.deathmotion.marlowcrystal.version.ModLoader;
import com.deathmotion.marlowcrystal.version.ModVersion;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class GitHubSource
implements BuildSource {
    private static final URI RELEASES = URI.create("https://api.github.com/repos/Bram1903/MarlowsCrystalOptimizer/releases?per_page=100");
    private static final Pattern RELEASE_VERSION = Pattern.compile("\\d+(?:\\.\\d+)*");
    private static final Pattern ASSET_RANGE = Pattern.compile("\\+mc(\\d+(?:\\.\\d+)*)(?:-(\\d+(?:\\.\\d+)*))?(?:-([a-z]+))?\\.jar$");
    private static final String LOADER_SUFFIX = CurrentBuild.LOADER == ModLoader.FABRIC ? null : CurrentBuild.LOADER.id();

    private static boolean supports(JsonArray assets, String minecraftVersion) {
        boolean ranged = false;
        for (JsonElement asset : assets != null ? assets : new JsonArray()) {
            String upper;
            Matcher matcher = ASSET_RANGE.matcher(asset.getAsJsonObject().get("name").getAsString());
            if (!matcher.find()) continue;
            ranged = true;
            if (!Objects.equals(matcher.group(3), LOADER_SUFFIX)) continue;
            if (!RELEASE_VERSION.matcher(minecraftVersion).matches()) {
                return true;
            }
            String lower = matcher.group(1);
            String string = upper = matcher.group(2) != null ? matcher.group(2) : lower;
            if (GitHubSource.compare(minecraftVersion, lower) < 0 || GitHubSource.compare(minecraftVersion, upper) > 0 && !minecraftVersion.startsWith(upper + ".")) continue;
            return true;
        }
        return !ranged && CurrentBuild.LOADER == ModLoader.FABRIC;
    }

    private static int compare(String left, String right) {
        String[] a = left.split("\\.");
        String[] b = right.split("\\.");
        for (int i = 0; i < Math.max(a.length, b.length); ++i) {
            int c = Integer.compare(i < a.length ? Integer.parseInt(a[i]) : 0, i < b.length ? Integer.parseInt(b[i]) : 0);
            if (c == 0) continue;
            return c;
        }
        return 0;
    }

    @Override
    public List<PublishedBuild> builds(String minecraftVersion) throws IOException, InterruptedException {
        ArrayList<PublishedBuild> builds = new ArrayList<PublishedBuild>();
        for (JsonElement element : JsonHttp.get(RELEASES).getAsJsonArray()) {
            JsonObject release = element.getAsJsonObject();
            Optional<ModVersion> version = ModVersion.parse(release.get("tag_name").getAsString());
            if (!version.isPresent() || !GitHubSource.supports(release.getAsJsonArray("assets"), minecraftVersion)) continue;
            builds.add(new PublishedBuild(version.get(), release.get("html_url").getAsString(), release.get("prerelease").getAsBoolean() ? ReleaseChannel.BETA : ReleaseChannel.RELEASE));
        }
        return builds;
    }
}

