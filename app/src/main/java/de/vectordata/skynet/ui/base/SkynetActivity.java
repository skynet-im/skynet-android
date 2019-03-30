package de.vectordata.skynet.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.appcompat.app.AppCompatActivity;
import de.vectordata.skynet.R;
import de.vectordata.skynet.event.AuthenticationFailedEvent;
import de.vectordata.skynet.event.HandshakeFailedEvent;
import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.packet.model.HandshakeState;
import de.vectordata.skynet.net.packet.model.RestoreSessionError;
import de.vectordata.skynet.ui.dialogs.Dialogs;

/**
 * Created by Twometer on 12.12.2018.
 * (c) 2018 Twometer
 */
public abstract class SkynetActivity extends AppCompatActivity {

    protected SkynetContext getSkynetContext() {
        return SkynetContext.getCurrent();
    }

    protected void startActivity(Class<? extends Activity> clazz) {
        startActivity(new Intent(getApplicationContext(), clazz));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHandshakeFailed(HandshakeFailedEvent event){
        if (event.getState() == HandshakeState.CAN_UPGRADE)
            Dialogs.showMessageBox(this, R.string.error_header_connect, String.format(getString(R.string.warn_can_upgrade), event.getNewVersion()));
        else if (event.getState() == HandshakeState.MUST_UPGRADE)
            Dialogs.showMessageBox(this, R.string.error_header_connect, String.format(getString(R.string.error_must_upgrade), event.getNewVersion()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAuthenticationFailed(AuthenticationFailedEvent event){
        if (event.getError() == RestoreSessionError.INVALID_CREDENTIALS)
            Dialogs.showMessageBox(this, R.string.error_header_connect, R.string.error_invalid_credentials_restore);
        else if (event.getError() == RestoreSessionError.INVALID_SESSION)
            Dialogs.showMessageBox(this, R.string.error_header_connect, R.string.error_invalid_session);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

}
