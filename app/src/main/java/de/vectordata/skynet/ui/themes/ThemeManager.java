package de.vectordata.skynet.ui.themes;

import android.content.Context;
import android.preference.PreferenceManager;

import java.util.HashMap;
import java.util.Map;

import de.vectordata.skynet.R;

public class ThemeManager {

    private static Map<String, Theme> themes = new HashMap<>();

    static {
        registerTheme("default", R.style.AppTheme, R.style.AppTheme_NoActionBar);
        registerTheme("light", R.style.AppTheme_Light, R.style.AppTheme_Light_NoActionBar);
        registerTheme("dark", R.style.AppTheme_Dark, R.style.AppTheme_Dark_NoActionBar);
        registerTheme("blue", R.style.AppTheme_Blue, R.style.AppTheme_Blue_NoActionBar);
        registerTheme("red", R.style.AppTheme_Red, R.style.AppTheme_Red_NoActionBar);
        registerTheme("green", R.style.AppTheme_Green, R.style.AppTheme_Green_NoActionBar);
        registerTheme("purple", R.style.AppTheme_Purple, R.style.AppTheme_Purple_NoActionBar);
        registerTheme("pink", R.style.AppTheme_Pink, R.style.AppTheme_Pink_NoActionBar);
    }

    private static void registerTheme(String key, int defaultRes, int noActionBarRes) {
        Theme theme = new Theme(key, defaultRes, noActionBarRes);
        themes.put(theme.getKey(), theme);
    }

    public static Theme getTheme(Context context) {
        String themeKey = PreferenceManager.getDefaultSharedPreferences(context).getString("color_theme", "default");
        return themes.get(themeKey);
    }

}
