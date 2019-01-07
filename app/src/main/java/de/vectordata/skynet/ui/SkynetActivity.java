package de.vectordata.skynet.ui;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import de.vectordata.skynet.R;
import de.vectordata.skynet.net.NetworkManager;
import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.packet.model.HandshakeState;
import de.vectordata.skynet.net.packet.model.RestoreSessionError;
import de.vectordata.skynet.ui.dialogs.Dialogs;

/**
 * Created by Twometer on 12.12.2018.
 * (c) 2018 Twometer
 */
public abstract class SkynetActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        registerDialogs();
    }

    SkynetContext getSkynetContext() {
        return SkynetContext.getCurrent();
    }

    protected void startActivity(Class<? extends Activity> clazz) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
