package de.vectordata.skynet.ui.base;

import android.os.Bundle;

import java.util.Objects;

import androidx.annotation.Nullable;
import de.vectordata.skynet.ui.themes.Theme;
import de.vectordata.skynet.ui.themes.ThemeManager;

public abstract class ThemedActivity extends SkynetActivity {

    private Theme currentTheme;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyTheme(ThemeManager.getTheme(this), false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Theme theme = ThemeManager.getTheme(this);
        if (!Objects.equals(theme.getKey(), currentTheme.getKey())) applyTheme(theme, true);
    }

    private void applyTheme(Theme theme, boolean recreate) {
        currentTheme = theme;
        if (hasCustomToolbar()) setTheme(theme.getNoActionBarRes());
        else setTheme(theme.getDefaultRes());
        if (recreate) recreate();
    }

    protected boolean hasCustomToolbar() {
        return false;
    }

}
