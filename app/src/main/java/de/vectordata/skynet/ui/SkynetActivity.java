package de.vectordata.skynet.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import de.vectordata.skynet.R;
import de.vectordata.skynet.net.NetworkManager;
import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.model.HandshakeState;
import de.vectordata.skynet.net.model.RestoreSessionError;
import de.vectordata.skynet.util.Dialogs;

/**
 * Created by Twometer on 12.12.2018.
 * (c) 2018 Twometer
 */
// This activity is a base class, so we don't register it in the manifest
@SuppressLint("Registered")
public class SkynetActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        registerDialogs();
    }

    SkynetContext getSkynetContext() {
        return SkynetContext.getCurrent();
    }

    void startActivity(Class<? extends Activity> clazz) {
        startActivity(new Intent(getApplicationContext(), clazz));
    }

    private void registerDialogs() {
        NetworkManager manager = SkynetContext.getCurrent().getNetworkManager();

        manager.setHandshakeListener((state, ver) -> {
            if (state == HandshakeState.CAN_UPGRADE)
                Dialogs.showMessageBox(this, R.string.error_header_connect, String.format(getString(R.string.warn_can_upgrade), ver));
            else if (state == HandshakeState.MUST_UPGRADE)
                Dialogs.showMessageBox(this, R.string.error_header_connect, String.format(getString(R.string.error_must_upgrade), ver));
        });

        manager.setAuthenticationListener((state) -> {
            if (state == RestoreSessionError.INVALID_CREDENTIALS)
                Dialogs.showMessageBox(this, R.string.error_header_connect, R.string.error_invalid_credentials_restore);
            else if (state == RestoreSessionError.INVALID_SESSION)
                Dialogs.showMessageBox(this, R.string.error_header_connect, R.string.error_invalid_session);
        });

    }
}
