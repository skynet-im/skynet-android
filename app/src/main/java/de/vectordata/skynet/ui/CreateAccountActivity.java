package de.vectordata.skynet.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

import de.vectordata.skynet.R;
import de.vectordata.skynet.crypto.hash.HashProvider;
import de.vectordata.skynet.event.ConnectionFailedEvent;
import de.vectordata.skynet.net.NetworkManager;
import de.vectordata.skynet.net.packet.P02CreateAccount;
import de.vectordata.skynet.net.packet.P03CreateAccountResponse;
import de.vectordata.skynet.net.packet.model.CreateAccountStatus;
import de.vectordata.skynet.ui.base.SkynetActivity;
import de.vectordata.skynet.ui.dialogs.Dialogs;
import de.vectordata.skynet.ui.dialogs.ProgressDialog;
import de.vectordata.skynet.util.android.Activities;

public class CreateAccountActivity extends SkynetActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        Activities.setStatusBarTranslucent(this);

        EditText email = findViewById(R.id.input_email);
        EditText nickname = findViewById(R.id.input_nickname);
        EditText password = findViewById(R.id.input_password);
        EditText passwordConfirm = findViewById(R.id.input_password_confirm);

        findViewById(R.id.button_login).setOnClickListener(v -> {
            if (!Objects.equals(password.getText().toString(), passwordConfirm.getText().toString())) {
                Dialogs.showMessageBox(this, R.string.error_header_create_acc, R.string.error_passwords_not_matching);
                return;
            }
            HashProvider.buildHashesAsync(email.getText().toString(), password.getText().toString(), result -> createAccount(email.getText().toString(), result.getKeyHash()));
        });
    }

    private void createAccount(String accountName, byte[] keyHash) {
        runOnUiThread(() ->
                progressDialog = Dialogs.showProgressDialog(this, R.string.progress_creating_account, false)
        );
        Log.d("CreateAccountActivity", "Creating user with " + accountName + " and " + keyHash.length);
        NetworkManager networkManager = getSkynetContext().getNetworkManager();
        networkManager.connect();

        networkManager.sendPacket(new P02CreateAccount(accountName, keyHash))
                .waitForPacket(P03CreateAccountResponse.class, packet -> runOnUiThread(() -> {
                    progressDialog.dismiss();
                    if (packet.statusCode == CreateAccountStatus.ACCOUNT_NAME_TAKEN)
                        Dialogs.showMessageBox(this, R.string.error_header_create_acc, R.string.error_account_name_taken);
                    else if (packet.statusCode == CreateAccountStatus.INVALID_ACCOUNT_NAME)
                        Dialogs.showMessageBox(this, R.string.error_header_create_acc, R.string.error_account_name_invalid);
                    else if (packet.statusCode == CreateAccountStatus.SUCCESS)
                        Dialogs.showMessageBox(this, R.string.success_header_create_acc, R.string.success_create_acc, (x, y) -> finish());
                }));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionFailed(ConnectionFailedEvent event) {
        if (progressDialog != null)
            progressDialog.dismiss();
        Dialogs.showMessageBox(this, R.string.error_header_create_acc, R.string.error_no_connection);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressDialog != null)
            progressDialog.dismiss();
    }
}
