package de.vectordata.skynet.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import de.vectordata.skynet.R;
import de.vectordata.skynet.auth.Session;
import de.vectordata.skynet.crypto.hash.HashProvider;
import de.vectordata.skynet.crypto.hash.KeyCollection;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.net.NetworkManager;
import de.vectordata.skynet.net.packet.P06CreateSession;
import de.vectordata.skynet.net.packet.P07CreateSessionResponse;
import de.vectordata.skynet.net.packet.model.CreateSessionError;
import de.vectordata.skynet.ui.base.SkynetActivity;
import de.vectordata.skynet.ui.dialogs.Dialogs;
import de.vectordata.skynet.ui.dialogs.ProgressDialog;
import de.vectordata.skynet.util.Activities;

public class LoginActivity extends SkynetActivity {

    private static final String TAG = "LoginActivity";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Activities.setStatusBarTranslucent(this);

        EditText emailInput = findViewById(R.id.input_email);
        EditText passwordInput = findViewById(R.id.input_password);

        findViewById(R.id.button_login).setOnClickListener(v -> {
            String username = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            progressDialog = Dialogs.showProgressDialog(this, R.string.progress_login_preparing, false);
            HashProvider.buildHashesAsync(username, password,
                    result -> firebaseAndLogin(emailInput.getText().toString(), result)
            );
        });
        findViewById(R.id.link_create_account).setOnClickListener(v -> startActivity(CreateAccountActivity.class));
    }

    private void firebaseAndLogin(String accountName, KeyCollection keys) {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Failed to get Firebase Token");
                Dialogs.showMessageBox(this, R.string.error_header_login, R.string.error_firebase);
                return;
            }
            InstanceIdResult instanceIdResult = task.getResult();
            if (instanceIdResult == null) return;
            String token = instanceIdResult.getToken();

            Log.i(TAG, "Firebase token: " + token);
            login(accountName, keys, token);
        });
    }

    private void login(String accountName, KeyCollection keys, String token) {
        progressDialog.setMessage(R.string.progress_logging_in);
        NetworkManager networkManager = getSkynetContext().getNetworkManager();
        networkManager.connect();

        networkManager.setErrorListener(() -> runOnUiThread(() -> {
            progressDialog.dismiss();
            Dialogs.showMessageBox(this, R.string.error_header_login, R.string.error_no_connection);
            networkManager.setErrorListener(null);
        }));

        Session session = new Session(keys);

        networkManager.sendPacket(new P06CreateSession(accountName, keys.getKeyHash(), token))
                .waitForPacket(P07CreateSessionResponse.class, p -> {
                    progressDialog.dismiss();
                    if (p.errorCode == CreateSessionError.INVALID_FCM_TOKEN)
                        Dialogs.showMessageBox(this, R.string.error_header_login, R.string.error_firebase_token);
                    else if (p.errorCode == CreateSessionError.INVALID_CREDENTIALS)
                        Dialogs.showMessageBox(this, R.string.error_header_login, R.string.error_invalid_credentials);
                    else if (p.errorCode == CreateSessionError.SUCCESS) {
                        session.setSessionId(p.sessionId);
                        session.setAccountId(p.accountId);
                        Storage.setSession(session);
                        finish();
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressDialog != null)
            progressDialog.dismiss();
    }
}
