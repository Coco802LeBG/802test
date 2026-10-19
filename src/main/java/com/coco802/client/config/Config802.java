package com.coco802.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Config 802 Client : .minecraft/config/802client.json (modifiable à la main). */
public final class Config802 {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("802client.json");

    /** Modules actifs (nom -> on/off). */
    public Map<String, Boolean> modules = new LinkedHashMap<>();
    /** GUI scale par type d'écran : 0 = celui des options Minecraft, sinon 1..6. */
    public Map<String, Integer> guiScale = new LinkedHashMap<>();
    /** Réglages des modules (clé "Module.reglage"). */
    public Map<String, Boolean> bools = new LinkedHashMap<>();
    public Map<String, Double> numbers = new LinkedHashMap<>();
    /** Profils sauvegardés (setups complets). */
    public List<Profile> profiles = new ArrayList<>();
    /** Slugs Modrinth supplémentaires à proposer dans l'onglet Mods. */
    public List<String> extraMods = new ArrayList<>();

    public static final class Profile {
        public String name = "Profile";
        public Map<String, Boolean> modules = new LinkedHashMap<>();
        public Map<String, Boolean> bools = new LinkedHashMap<>();
        public Map<String, Double> numbers = new LinkedHashMap<>();
        public Map<String, Integer> guiScale = new LinkedHashMap<>();

        public int modulesOn() {
            int n = 0;
            for (Boolean b : modules.values()) if (b != null && b) n++;
            return n;
        }
    }

    public Profile snapshot(String name) {
        Profile p = new Profile();
        p.name = name;
        p.modules.putAll(modules);
        p.bools.putAll(bools);
        p.numbers.putAll(numbers);
        p.guiScale.putAll(guiScale);
        return p;
    }

    public void apply(Profile p) {
        modules = new LinkedHashMap<>(p.modules);
        bools = new LinkedHashMap<>(p.bools);
        numbers = new LinkedHashMap<>(p.numbers);
        guiScale = new LinkedHashMap<>(p.guiScale);
    }

    /** Touche du menu (GLFW). 344 = Shift droit. */
    public int menuKey = 344;

    public int scaleFor(String kind) {
        if (guiScale == null) return 0;
        Integer v = guiScale.get(kind);
        return v == null ? 0 : v;
    }

    public boolean bool(String id, boolean def) {
        if (bools == null) return def;
        Boolean v = bools.get(id);
        return v == null ? def : v;
    }

    public double number(String id, double def) {
        if (numbers == null) return def;
        Double v = numbers.get(id);
        return v == null ? def : v;
    }

    public boolean moduleEnabled(String name, boolean def) {
        if (modules == null) return def;
        Boolean v = modules.get(name);
        return v == null ? def : v;
    }

    public static Config802 load() {
        try {
            if (Files.exists(FILE)) {
                Config802 c = GSON.fromJson(Files.readString(FILE, StandardCharsets.UTF_8), Config802.class);
                if (c != null) {
                    if (c.modules == null) c.modules = new LinkedHashMap<>();
                    if (c.guiScale == null) c.guiScale = new LinkedHashMap<>();
                    if (c.bools == null) c.bools = new LinkedHashMap<>();
                    if (c.numbers == null) c.numbers = new LinkedHashMap<>();
                    if (c.profiles == null) c.profiles = new ArrayList<>();
                    if (c.extraMods == null) c.extraMods = new ArrayList<>();
                    return c;
                }
            }
        } catch (Exception e) {
            System.err.println("[802 Client] Could not read config: " + e.getMessage());
        }
        Config802 c = new Config802();
        c.save();
        return c;
    }

    public void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(this), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("[802 Client] Could not save config: " + e.getMessage());
        }
    }
}
