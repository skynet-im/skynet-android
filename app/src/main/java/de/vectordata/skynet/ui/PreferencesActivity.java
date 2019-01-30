package de.vectordata.skynet.ui;

import android.os.Bundle;

import com.takisoft.preferencex.PreferenceFragmentCompat;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import de.vectordata.skynet.R;
import de.vectordata.skynet.ui.base.ThemedActivity;
import de.vectordata.skynet.util.Activities;

/**
 * Created by Twometer on 21.01.2019.
 * (c) 2019 Twometer
 */
public class PreferencesActivity extends ThemedActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        Activities.enableUpButton(this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.preferences_container, new PreferencesFragment())
                .commit();
    }

    public static class PreferencesFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
            Preference colorTheme = findPreference("color_theme");
            colorTheme.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
            colorTheme.setOnPreferenceChangeListener((preference, newValue) -> {
                Objects.requireNonNull(getActivity()).recreate();
                return true;
            });
        }
    }

}
