package pl.szybki.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Config {
    public static boolean hudMark = true;
    public static boolean hudList = true;
    public static String alias = "Gracz";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static boolean loading = false;

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("szybkiclient.json");
    }

    public static void load() {
        try {
            Path p = path();
            if (!Files.exists(p)) return;
            loading = true;
            JsonObject o = JsonParser.parseString(Files.readString(p, StandardCharsets.UTF_8)).getAsJsonObject();
            if (o.has("language")) Lang.current = Math.max(0, Math.min(2, o.get("language").getAsInt()));
            if (o.has("alias")) alias = o.get("alias").getAsString();
            if (o.has("hudMark")) hudMark = o.get("hudMark").getAsBoolean();
            if (o.has("hudList")) hudList = o.get("hudList").getAsBoolean();
            if (o.has("modules")) {
                JsonObject mods = o.getAsJsonObject("modules");
                for (Module m : ModuleManager.ALL) {
                    if (!mods.has(m.id)) continue;
                    JsonObject mo = mods.getAsJsonObject(m.id);
                    if (m.persists() && mo.has("enabled")) m.enabled = mo.get("enabled").getAsBoolean();
                    if (mo.has("settings")) {
                        JsonObject so = mo.getAsJsonObject("settings");
                        for (Setting s : m.settings) {
                            if (!so.has(s.key)) continue;
                            if (s.isBool) s.flag = so.get(s.key).getAsBoolean();
                            else s.value = Math.max(s.min, Math.min(s.max, so.get(s.key).getAsDouble()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[SZYBKI CLIENT] Blad wczytywania configu: " + e);
        } finally {
            loading = false;
        }
    }

    public static void save() {
        if (loading) return;
        try {
            JsonObject o = new JsonObject();
            o.addProperty("language", Lang.current);
            o.addProperty("alias", alias);
            o.addProperty("hudMark", hudMark);
            o.addProperty("hudList", hudList);
            JsonObject mods = new JsonObject();
            for (Module m : ModuleManager.ALL) {
                JsonObject mo = new JsonObject();
                mo.addProperty("enabled", m.persists() && m.enabled);
                JsonObject so = new JsonObject();
                for (Setting s : m.settings) {
                    if (s.isBool) so.addProperty(s.key, s.flag);
                    else so.addProperty(s.key, s.value);
                }
                mo.add("settings", so);
                mods.add(m.id, mo);
            }
            o.add("modules", mods);
            Files.writeString(path(), GSON.toJson(o), StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("[SZYBKI CLIENT] Blad zapisu configu: " + e);
        }
    }

    private Config() {}
}
