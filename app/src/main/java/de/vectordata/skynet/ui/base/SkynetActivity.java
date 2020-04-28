package de.vectordata.skynet.ui.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.vectordata.skynet.R;
import de.vectordata.skynet.event.AuthenticationFailedEvent;
import de.vectordata.skynet.event.CorruptedMessageEvent;
import de.vectordata.skynet.event.HandshakeFailedEvent;
import de.vectordata.skynet.event.SyncFinishedEvent;
import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.packet.model.HandshakeState;
import de.vectordata.skynet.net.packet.model.MessageFlags;
import de.vectordata.skynet.net.packet.model.RestoreSessionStatus;
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

    protected void startBrowser(String link) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHandshakeFailed(HandshakeFailedEvent event) {
        if (event.getState() == HandshakeState.CAN_UPGRADE)
            Dialogs.showMessageBox(this, R.string.error_header_connect, String.format(getString(R.string.warn_can_upgrade), event.getNewVersion()));
        else if (event.getState() == HandshakeState.MUST_UPGRADE)
            Dialogs.showMessageBox(this, R.string.error_header_connect, String.format(getString(R.string.error_must_upgrade), event.getNewVersion()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAuthenticationFailed(AuthenticationFailedEvent event) {
        if (event.getError() == RestoreSessionStatus.INVALID_SESSION)
            Dialogs.showMessageBox(this, R.string.error_header_connect, R.string.error_invalid_session);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSyncFinished(SyncFinishedEvent event) {
        int corruptedMessages = SkynetContext.getCurrent().getNetworkManager().getLastSyncCorruptedMessages();
        if (corruptedMessages > 0) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.error_header_sync)
                    .setMessage(getString(R.string.error_corrupted_message_multi, corruptedMessages))
                    .setPositiveButton(R.string.ok, null)
                    .setNeutralButton(R.string.action_report_issue, (dialog, which) -> startBrowser("https://www.skynet.app/bug"))
                    .show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCorruptedMessage(CorruptedMessageEvent event) {
        if (event.getPacket().hasFlag(MessageFlags.LOOPBACK) && SkynetContext.getCurrent().isInSync())
            new AlertDialog.Builder(this)
                    .setTitle(R.string.error_header_sync)
                    .setMessage(R.string.error_corrupted_message_single)
                    .setPositiveButton(R.string.ok, null)
                    .setNeutralButton(R.string.action_report_issue, (dialog, which) -> startBrowser("https://www.skynet.app/bug"))
                    .show();
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
