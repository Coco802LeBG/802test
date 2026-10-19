package com.deathmotion.marlowcrystal.config;

import com.deathmotion.marlowcrystal.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SettingsFile {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Path path;

    public SettingsFile(Path path) {
        this.path = path;
    }

    public Settings load() {
        if (Files.notExists(this.path)) {
            return new Settings();
        }
        try (BufferedReader reader = Files.newBufferedReader(this.path, StandardCharsets.UTF_8)) {
            Settings settings = GSON.fromJson(reader, Settings.class);
            return settings != null ? settings : new Settings();
        } catch (JsonParseException | IOException e) {
            Log.warn("Could not read " + this.path.getFileName() + ", using defaults: " + e.getMessage());
            return new Settings();
        }
    }

    public synchronized void save(Settings settings) {
        try {
            Files.createDirectories(this.path.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(this.path, StandardCharsets.UTF_8)) {
                GSON.toJson(settings, writer);
            }
        } catch (JsonParseException | IOException e) {
            Log.error("Could not save " + this.path.getFileName() + ": " + e.getMessage());
        }
    }
}
