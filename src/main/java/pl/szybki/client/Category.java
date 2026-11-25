package pl.szybki.client;

public enum Category {
    CHEATS("cat.cheats", "tab.cheats"),
    RADAR("cat.radar", "tab.radar"),
    HELPERS("cat.helpers", "tab.helpers");

    public final String nameKey;
    public final String tabKey;

    Category(String nameKey, String tabKey) {
        this.nameKey = nameKey;
        this.tabKey = tabKey;
    }

    public String title() {
        return Lang.t(nameKey);
    }

    public String tab() {
        return Lang.t(tabKey);
    }
}
