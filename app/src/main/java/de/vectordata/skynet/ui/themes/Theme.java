package de.vectordata.skynet.ui.themes;

public class Theme {

    private String key;

    private int defaultRes;

    private int noActionBarRes;

    Theme(String key, int defaultRes, int noActionBarRes) {
        this.key = key;
        this.defaultRes = defaultRes;
        this.noActionBarRes = noActionBarRes;
    }

    public String getKey() {
        return key;
    }

    public int getDefaultRes() {
        return defaultRes;
    }

    public int getNoActionBarRes() {
        return noActionBarRes;
    }

}
