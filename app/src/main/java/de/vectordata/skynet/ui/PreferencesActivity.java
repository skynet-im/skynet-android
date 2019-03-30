package de.vectordata.skynet.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.takisoft.preferencex.PreferenceFragmentCompat;

import java.util.Locale;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import de.psdev.licensesdialog.LicensesDialog;
import de.vectordata.skynet.BuildConfig;
import de.vectordata.skynet.R;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.ui.base.ThemedActivity;
import de.vectordata.skynet.ui.dialogs.Dialogs;
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

            Activity activity = Objects.requireNonNull(getActivity());

            Preference colorTheme = findPreference("color_theme");
            colorTheme.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
            colorTheme.setOnPreferenceChangeListener((preference, newValue) -> {
                activity.recreate();
                return true;
            });

            findPreference("licenses").setOnPreferenceClickListener(preference -> {
                new LicensesDialog.Builder(activity)
                        .setTitle(R.string.pref_licenses)
                        .setNotices(R.raw.licenses)
                        .setIncludeOwnLicense(true)
                        .build()
                        .show();
                return true;
            });

            findPreference("version").setSummary(String.format(Locale.getDefault(), "%s (build %d)", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));

            findPreference("logoff").setOnPreferenceClickListener(preference -> {
                Dialogs.showYesNoBox(activity, R.string.question_header_logoff, R.string.question_logoff, (dialog, which) -> new Thread(() -> {
                    Storage.clear();
                    SkynetContext.getCurrent().getNetworkManager().disconnect();
                    new Handler(activity.getApplicationContext().getMainLooper()).post(() -> SkynetContext.getCurrent().recreateNetworkManager());
                    startActivity(new Intent(activity, LoginActivity.class));
                    activity.finish();
                }).start(), null);
                return true;
            });
        }
    }

}
