package com.deathmotion.marlowcrystal.version;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.Nullable;

public record ModVersion(int major, int minor, int patch, boolean snapshot, @Nullable String commit) implements Comparable<ModVersion>
{
    private static final Pattern VERSION_PATTERN = Pattern.compile("v?(\\d+)\\.(\\d+)(?:\\.(\\d+))?(-SNAPSHOT)?(?:\\+([0-9a-z.-]+))?", 2);
    private static final Pattern COMMIT_PATTERN = Pattern.compile("[0-9a-f]{7,40}");

    
    public static Optional<ModVersion> parse(String version) {
        Matcher matcher = VERSION_PATTERN.matcher(version.trim());
        if (!matcher.matches()) {
            return Optional.empty();
        }
        String metadata = matcher.group(5);
        return Optional.of(new ModVersion(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0, matcher.group(4) != null, metadata != null && COMMIT_PATTERN.matcher(metadata).matches() ? metadata : null));
    }

    @Override
    public int compareTo(ModVersion other) {
        int c = Integer.compare(this.major, other.major);
        if (c != 0) {
            return c;
        }
        c = Integer.compare(this.minor, other.minor);
        if (c != 0) {
            return c;
        }
        c = Integer.compare(this.patch, other.patch);
        if (c != 0) {
            return c;
        }
        return Boolean.compare(other.snapshot, this.snapshot);
    }

    public boolean isNewerThan(ModVersion other) {
        return this.compareTo(other) > 0;
    }

    
    public String toDisplayString() {
        String release = this.major + "." + this.minor + "." + this.patch;
        return this.snapshot ? release + "-SNAPSHOT" : release;
    }

    @Override
    
    public String toString() {
        return this.toDisplayString() + (String)(this.commit != null ? "+" + this.commit : "");
    }
}

