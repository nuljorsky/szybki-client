# SZYBKI CLIENT (Minecraft Java 1.21.4, Fabric)

## Jak zbudować .jar przez GitHub
1. Wrzuć cały ten folder do nowego repo na GitHubie (branch `main`).
2. Wejdź w **Actions** -> workflow **Build SZYBKI CLIENT** (odpala się sam po pushu, albo "Run workflow").
3. Po zakończeniu pobierz artefakt **SZYBKI-CLIENT-jar** -> w środku `szybki-client-1.0.0.jar`.
4. Wrzuć .jar do `mods` razem z **Fabric API** (0.119.2+1.21.4) i Fabric Loader >= 0.16.10.

Lokalnie: `gradle build` (Java 21) -> `build/libs/`.

## Sterowanie
- **PRAWY SHIFT** - menu klienta (też przycisk w menu głównym).
- `/nameprotect <nick>` - alias dla NameProtect.
- Klawisze AntyKostki (zapis/przywracanie) - Opcje -> Sterowanie -> SZYBKI CLIENT (domyślnie nieprzypisane).
  Przyciski są też w ekwipunku (lewy górny róg), gdy AntyKostka jest włączona.

## Moduły
- **Szybkie - CHEATY:** Hitboxes, FastPlace, Freecam
- **Szybkie - Radar:** Radar, Tracery (100 m), Block ESP, Nametags PRO, Elytra Detektor
- **Szybkie - Wspomagacze:** AntyKostka, AutoNielot, AutoLeave, NameProtect

Config: `config/szybkiclient.json`. Czcionka: Inter (SIL OFL 1.1).
