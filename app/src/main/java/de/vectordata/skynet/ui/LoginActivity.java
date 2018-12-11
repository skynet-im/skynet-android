package de.vectordata.skynet.ui;

import androidx.appcompat.app.AppCompatActivity;
import de.vectordata.skynet.R;
import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.model.CreateSessionError;
import de.vectordata.skynet.net.packet.P06CreateSession;
import de.vectordata.skynet.net.packet.P07CreateSessionResponse;
import de.vectordata.skynet.util.Dialogs;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    private void login(String accountName, byte[] password) {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Failed to get Firebase Token");
                Dialogs.showMessageBox(this, R.string.error_header_login, R.string.error_firebase);
                return;
            }
            InstanceIdResult instanceIdResult = task.getResult();
            if (instanceIdResult == null) return;
            String token = instanceIdResult.getToken();

            SkynetContext.getCurrent().getNetworkManager()
                    .sendPacket(new P06CreateSession(accountName, password, token))
                    .waitForPacket(P07CreateSessionResponse.class, p -> {
                        if (p.errorCode == CreateSessionError.INVALID_FCM_TOKEN)
                            Dialogs.showMessageBox(this, R.string.error_header_login, R.string.error_firebase_token);
                        else if (p.errorCode == CreateSessionError.INVALID_CREDENTIALS)
                            Dialogs.showMessageBox(this, R.string.error_header_login, R.string.error_invalid_credentials);
                    });
        });
    }

}
