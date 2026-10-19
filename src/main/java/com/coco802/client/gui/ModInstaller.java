package com.coco802.client.gui;

import com.coco802.client.Client802;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Installe des mods depuis Modrinth (officiel) quand TU cliques "Installer" :
 * HTTPS, vérification du hash SHA-512 donné par l'API, fichier écrit dans mods/, rien de caché.
 */
public final class ModInstaller {
    public record Entry(String slug, String modId, String name, String description) {}

    private static final Map<String, String> STATUS = new ConcurrentHashMap<>();
    private static final String UA = "802-client/1.0 (mod manager)";

    private static final List<Entry> CATALOG = List.of(
        new Entry("sodium", "sodium", "Sodium", "Moteur de rendu très rapide"),
        new Entry("lithium", "lithium", "Lithium", "Optimise la logique du jeu"),
        new Entry("ferrite-core", "ferritecore", "FerriteCore", "Réduit l'utilisation de la mémoire"),
        new Entry("immediatelyfast", "immediatelyfast", "ImmediatelyFast", "Accélère le rendu de l'interface"),
        new Entry("entityculling", "entityculling", "Entity Culling", "Ne dessine pas ce qui est caché"),
        new Entry("moreculling", "moreculling", "More Culling", "Cache plus de faces invisibles"),
        new Entry("modmenu", "modmenu", "Mod Menu", "Liste des mods installés")
    );

    private ModInstaller() {}

    public static List<Entry> entries() {
        List<Entry> all = new ArrayList<>(CATALOG);
        for (String slug : Client802.CONFIG.extraMods) {
            if (slug == null || slug.isBlank()) continue;
            all.add(new Entry(slug.trim(), "", slug.trim(), "Mod Modrinth ajouté dans 802client.json"));
        }
        return all;
    }

    public static String status(Entry e) {
        if (!e.modId().isEmpty() && FabricLoader.getInstance().isModLoaded(e.modId())) return "Installé";
        String s = STATUS.get(e.slug());
        return s == null ? "Installer" : s;
    }

    public static boolean canInstall(Entry e) {
        return status(e).equals("Installer") || status(e).startsWith("Échec") || status(e).startsWith("Indisponible");
    }

    public static void install(Entry e) {
        STATUS.put(e.slug(), "Téléchargement...");
        Thread t = new Thread(() -> {
            try {
                run(e);
            } catch (Exception ex) {
                STATUS.put(e.slug(), "Échec : " + ex.getClass().getSimpleName());
            }
        }, "802-mod-installer");
        t.setDaemon(true);
        t.start();
    }

    private static void run(Entry e) throws Exception {
        HttpClient http = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
        String games = URLEncoder.encode("[\"1.21.11\"]", StandardCharsets.UTF_8);
        String loaders = URLEncoder.encode("[\"fabric\"]", StandardCharsets.UTF_8);
        String url = "https://api.modrinth.com/v2/project/" + e.slug() + "/version?game_versions=" + games + "&loaders=" + loaders;
        HttpResponse<String> res = http.send(HttpRequest.newBuilder(URI.create(url)).header("User-Agent", UA).GET().build(),
            HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200) {
            STATUS.put(e.slug(), "Échec : HTTP " + res.statusCode());
            return;
        }
        JsonArray versions = JsonParser.parseString(res.body()).getAsJsonArray();
        if (versions.isEmpty()) {
            STATUS.put(e.slug(), "Indisponible en 1.21.11");
            return;
        }
        JsonObject version = versions.get(0).getAsJsonObject();
        for (JsonElement el : versions) {
            JsonObject v = el.getAsJsonObject();
            if ("release".equals(v.get("version_type").getAsString())) {
                version = v;
                break;
            }
        }
        JsonArray files = version.getAsJsonArray("files");
        JsonObject file = files.get(0).getAsJsonObject();
        for (JsonElement el : files) {
            JsonObject f = el.getAsJsonObject();
            if (f.has("primary") && f.get("primary").getAsBoolean()) {
                file = f;
                break;
            }
        }
        String fileUrl = file.get("url").getAsString();
        String filename = file.get("filename").getAsString();
        String sha512 = file.getAsJsonObject("hashes").get("sha512").getAsString();
        if (!fileUrl.startsWith("https://cdn.modrinth.com/") || !filename.endsWith(".jar")
            || filename.contains("/") || filename.contains("\\") || filename.contains("..")) {
            STATUS.put(e.slug(), "Échec : fichier refusé");
            return;
        }
        byte[] data = http.send(HttpRequest.newBuilder(URI.create(fileUrl)).header("User-Agent", UA).GET().build(),
            HttpResponse.BodyHandlers.ofByteArray()).body();
        StringBuilder hex = new StringBuilder();
        for (byte b : MessageDigest.getInstance("SHA-512").digest(data)) hex.append(String.format("%02x", b));
        if (!hex.toString().equalsIgnoreCase(sha512)) {
            STATUS.put(e.slug(), "Échec : hash invalide");
            return;
        }
        Path mods = FabricLoader.getInstance().getGameDir().resolve("mods");
        Files.createDirectories(mods);
        Files.write(mods.resolve(filename), data);
        STATUS.put(e.slug(), "Installé - redémarre");
    }
}
