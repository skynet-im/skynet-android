package de.vectordata.skynet.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import de.vectordata.skynet.R;
import de.vectordata.skynet.crypto.hash.HashProvider;
import de.vectordata.skynet.crypto.hash.HashResult;
import de.vectordata.skynet.net.model.CreateSessionError;
import de.vectordata.skynet.net.packet.P06CreateSession;
import de.vectordata.skynet.net.packet.P07CreateSessionResponse;
import de.vectordata.skynet.util.Activities;
import de.vectordata.skynet.util.Dialogs;

public class LoginActivity extends SkynetActivity {

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Activities.setStatusBarTranslucent(this);

        EditText emailInput = findViewById(R.id.input_email);
        EditText passwordInput = findViewById(R.id.input_password);

        String username = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        findViewById(R.id.button_login).setOnClickListener(v -> HashProvider.buildHashesAsync(username, password,
                result -> login(emailInput.getText().toString(), result)
        ));
        findViewById(R.id.link_create_account).setOnClickListener(v -> startActivity(CreateAccountActivity.class));
    }

    private void login(String accountName, HashResult result) {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Failed to get Firebase Token");
                Dialogs.showMessageBox(this, R.string.error_header_login, R.string.error_firebase);
                return;
            }
            InstanceIdResult instanceIdResult = task.getResult();
            if (instanceIdResult == null) return;
            String token = instanceIdResult.getToken();

            getSkynetContext().getNetworkManager()
                    .sendPacket(new P06CreateSession(accountName, result.getKeyHash(), token))
                    .waitForPacket(P07CreateSessionResponse.class, p -> {
                        if (p.errorCode == CreateSessionError.INVALID_FCM_TOKEN)
                            Dialogs.showMessageBox(this, R.string.error_header_login, R.string.error_firebase_token);
                        else if (p.errorCode == CreateSessionError.INVALID_CREDENTIALS)
                            Dialogs.showMessageBox(this, R.string.error_header_login, R.string.error_invalid_credentials);
                    });
        });
    }

}
