package de.vectordata.skynet.ui;

import android.os.Bundle;
import android.widget.EditText;

import java.util.Objects;

import de.vectordata.skynet.R;
import de.vectordata.skynet.crypto.hash.HashProvider;
import de.vectordata.skynet.net.packet.P02CreateAccount;
import de.vectordata.skynet.net.packet.P03CreateAccountResponse;
import de.vectordata.skynet.net.packet.model.CreateAccountError;
import de.vectordata.skynet.ui.dialogs.Dialogs;
import de.vectordata.skynet.ui.dialogs.ProgressDialog;
import de.vectordata.skynet.util.Activities;

public class CreateAccountActivity extends SkynetActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        Activities.setStatusBarTranslucent(this);

        String email = ((EditText) findViewById(R.id.input_email)).getText().toString();
        String nickname = ((EditText) findViewById(R.id.input_nickname)).getText().toString();
        String password = ((EditText) findViewById(R.id.input_password)).getText().toString();
        String passwordConfirm = ((EditText) findViewById(R.id.input_password_confirm)).getText().toString();

        if (!Objects.equals(password, passwordConfirm)) {
            Dialogs.showMessageBox(this, R.string.error_header_create_acc, R.string.error_passwords_not_matching);
            return;
        }

        findViewById(R.id.button_login).setOnClickListener(v -> HashProvider.buildHashesAsync(email, password, result -> createAccount(email, result.getKeyHash())));
    }

    private void createAccount(String accountName, byte[] keyHash) {
        runOnUiThread(() ->
                progressDialog = Dialogs.showProgressDialog(this, R.string.progress_creating_account, false)
        );
        getSkynetContext().getNetworkManager()
                .sendPacket(new P02CreateAccount(accountName, keyHash))
                .waitForPacket(P03CreateAccountResponse.class, packet -> {
                    progressDialog.dismiss();
                    if (packet.errorCode == CreateAccountError.ACCOUNT_NAME_TAKEN)
                        Dialogs.showMessageBox(this, R.string.error_header_create_acc, R.string.error_account_name_taken);
                    else if (packet.errorCode == CreateAccountError.INVALID_ACCOUNT_NAME)
                        Dialogs.showMessageBox(this, R.string.error_header_create_acc, R.string.error_account_name_invalid);
                    else if (packet.errorCode == CreateAccountError.SUCCESS)
                        Dialogs.showMessageBox(this, R.string.success_header_create_acc, R.string.success_create_acc);
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressDialog != null)
            progressDialog.dismiss();
    }
}
