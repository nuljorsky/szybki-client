package pl.szybki.client;

import java.util.HashMap;
import java.util.Map;

/** Tlumaczenia: 0 = Polski, 1 = English, 2 = Ukrainski. */
public final class Lang {
    public static final String[] NAMES = {"Polski", "English", "Українська (Nager1406)"};
    public static int current = 0;

    private static final Map<String, String[]> M = new HashMap<>();

    private static void a(String k, String pl, String en, String uk) {
        M.put(k, new String[]{pl, en, uk});
    }

    public static String t(String key) {
        String[] v = M.get(key);
        if (v == null) return key;
        return v[Math.max(0, Math.min(2, current))];
    }

    static {
        // kategorie
        a("cat.cheats", "Szybkie - CHEATY", "Fast - CHEATS", "Швидкі - ЧИТИ");
        a("cat.radar", "Szybkie - Radar", "Fast - Radar", "Швидкі - Радар");
        a("cat.helpers", "Szybkie - Wspomagacze", "Fast - Helpers", "Швидкі - Помічники");
        a("tab.cheats", "CHEATY", "CHEATS", "ЧИТИ");
        a("tab.radar", "RADAR", "RADAR", "РАДАР");
        a("tab.helpers", "WSPOMAGACZE", "HELPERS", "ПОМІЧНИКИ");
        a("tab.settings", "USTAWIENIA", "SETTINGS", "НАЛАШТУВАННЯ");

        // interfejs
        a("ui.active", "aktywne", "active", "активні");
        a("ui.settings", "Ustawienia ›", "Settings ›", "Налаштування ›");
        a("ui.back", "‹ Wróć", "‹ Back", "‹ Назад");
        a("ui.language", "Język", "Language", "Мова");
        a("ui.hint", "PRAWY SHIFT - menu", "RIGHT SHIFT - menu", "RIGHT SHIFT - меню");
        a("ui.hud_mark", "HUD: znak wodny", "HUD: watermark", "HUD: водяний знак");
        a("ui.hud_list", "HUD: lista modułów", "HUD: module list", "HUD: список модулів");
        a("ui.about", "Wersja 1.0.0 • Minecraft 1.21.4 • Fabric", "Version 1.0.0 • Minecraft 1.21.4 • Fabric", "Версія 1.0.0 • Minecraft 1.21.4 • Fabric");
        a("ui.title_btn", "SZYBKI CLIENT", "SZYBKI CLIENT", "SZYBKI CLIENT");

        // komunikaty
        a("alert.elytra", "UWAŻAJ WLOTA!!!", "WATCH OUT, ELYTRA!!!", "ОБЕРЕЖНО, ЛІТУН!!!");
        a("btn.save", "Zapisz ekwipunek", "Save inventory", "Зберегти інвентар");
        a("btn.restore", "Przywróć ekwipunek", "Restore inventory", "Відновити інвентар");
        a("msg.saved", "Ekwipunek zapisany", "Inventory saved", "Інвентар збережено");
        a("msg.restoring", "Przywracanie ekwipunku...", "Restoring inventory...", "Відновлення інвентаря...");
        a("msg.nosave", "Brak zapisanego ekwipunku", "No saved inventory", "Немає збереженого інвентаря");
        a("msg.nielot", "AutoNielot: wędka w lewej ręce", "AutoNielot: rod in left hand", "AutoNielot: вудка в лівій руці");
        a("msg.leave", "AutoLeave: wyszedłeś z serwera", "AutoLeave: left the server", "AutoLeave: вихід із сервера");
        a("msg.alias", "NameProtect: twój nick to teraz ", "NameProtect: your name is now ", "NameProtect: твій нік тепер ");
        a("hint.nameprotect", "Zmień nick komendą: /nameprotect <nick>", "Change the name with: /nameprotect <name>", "Зміни нік командою: /nameprotect <нік>");

        // moduly
        a("mod.hitboxes.name", "Hitboxes", "Hitboxes", "Hitboxes");
        a("mod.hitboxes.desc", "Bij graczy przez ściany. Działa po kliknięciu, nie automatycznie.", "Hit players through walls. Works on click, not automatic.", "Бий гравців крізь стіни. Працює по кліку, не автоматично.");
        a("mod.fastplace.name", "FastPlace", "FastPlace", "FastPlace");
        a("mod.fastplace.desc", "Stawiaj bloki bez opóźnienia.", "Place blocks without delay.", "Став блоки без затримки.");
        a("mod.freecam.name", "Freecam", "Freecam", "Freecam");
        a("mod.freecam.desc", "Lataj kamerą poza swoim ciałem.", "Fly the camera outside your body.", "Літай камерою поза своїм тілом.");

        a("mod.radar.name", "Radar", "Radar", "Радар");
        a("mod.radar.desc", "Minimapa graczy w pobliżu.", "Minimap of nearby players.", "Мінікарта гравців поблизу.");
        a("mod.tracers.name", "Tracery", "Tracers", "Трасери");
        a("mod.tracers.desc", "Linie do graczy w zasięgu do 100 m.", "Lines to players up to 100 m away.", "Лінії до гравців у межах 100 м.");
        a("mod.blockesp.name", "Block ESP", "Block ESP", "Block ESP");
        a("mod.blockesp.desc", "Podświetla cenne bloki przez ściany.", "Highlights valuable blocks through walls.", "Підсвічує цінні блоки крізь стіни.");
        a("mod.nametags.name", "Nametags PRO", "Nametags PRO", "Nametags PRO");
        a("mod.nametags.desc", "Nick, HP, dystans i ekwipunek gracza.", "Name, HP, distance and gear of players.", "Нік, HP, дистанція та спорядження гравця.");
        a("mod.elytra.name", "Elytra Detektor", "Elytra Detector", "Детектор елітр");
        a("mod.elytra.desc", "Ostrzega, gdy ktoś leci elytrą w zasięgu 100 m.", "Warns when someone flies with elytra within 100 m.", "Попереджає, коли хтось летить на елітрах у межах 100 м.");

        a("mod.antykostka.name", "AntyKostka", "AntyKostka", "AntyKostka");
        a("mod.antykostka.desc", "Zapisz ekwipunek i przywróć go, gdy się pomiesza.", "Save your inventory and restore it if it gets scrambled.", "Збережи інвентар і відновлюй, якщо він переплутався.");
        a("mod.autonielot.name", "AutoNielot", "AutoNielot", "AutoNielot");
        a("mod.autonielot.desc", "Gdy gracz przed tobą zakłada elytrę, wędka Nielotek leci do lewej ręki.", "When a player in front of you equips elytra, the Nielotek rod goes to your left hand.", "Коли гравець перед тобою вдягає елітри, вудка Nielotek йде в ліву руку.");
        a("mod.autoleave.name", "AutoLeave", "AutoLeave", "AutoLeave");
        a("mod.autoleave.desc", "Wychodzi z serwera przy niskim HP.", "Leaves the server at low HP.", "Виходить із сервера при низькому HP.");
        a("mod.nameprotect.name", "NameProtect", "NameProtect", "NameProtect");
        a("mod.nameprotect.desc", "Ukrywa twój nick w czacie i tabie.", "Hides your name in chat and tab list.", "Ховає твій нік у чаті та табі.");

        // ustawienia
        a("set.reach", "Zasięg", "Reach", "Дальність");
        a("set.delay", "Opóźnienie (tiki)", "Delay (ticks)", "Затримка (тіки)");
        a("set.speed", "Prędkość", "Speed", "Швидкість");
        a("set.range", "Zasięg (m)", "Range (m)", "Дальність (м)");
        a("set.size", "Rozmiar", "Size", "Розмір");
        a("set.ores", "Rudy", "Ores", "Руди");
        a("set.chests", "Skrzynie", "Chests", "Скрині");
        a("set.spawners", "Spawnery", "Spawners", "Спавнери");
        a("set.items", "Pokaż przedmioty", "Show items", "Показати предмети");
        a("set.hidevanilla", "Ukryj domyślne nametagi", "Hide vanilla nametags", "Сховати стандартні неймтеги");
        a("set.dist", "Dystans (m)", "Distance (m)", "Дистанція (м)");
        a("set.anyrod", "Dowolna wędka", "Any fishing rod", "Будь-яка вудка");
        a("set.hp", "Próg HP", "HP threshold", "Поріг HP");
        a("set.near", "Gracz w pobliżu (m, 0=wył.)", "Player nearby (m, 0=off)", "Гравець поруч (м, 0=вимк.)");
    }

    private Lang() {}
}
