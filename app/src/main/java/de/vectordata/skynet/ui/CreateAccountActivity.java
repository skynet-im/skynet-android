package de.vectordata.skynet.ui;

import androidx.appcompat.app.AppCompatActivity;
import de.vectordata.skynet.R;
import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.model.CreateAccountError;
import de.vectordata.skynet.net.model.CreateSessionError;
import de.vectordata.skynet.net.packet.P02CreateAccount;
import de.vectordata.skynet.net.packet.P03CreateAccountResponse;
import de.vectordata.skynet.net.response.ResponseHandler;
import de.vectordata.skynet.util.Dialogs;

import android.os.Bundle;

import java.time.format.ResolverStyle;

public class CreateAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
    }

    private void createAccount(String accountName, byte[] password) {
        SkynetContext.getCurrent().getNetworkManager()
                .sendPacket(new P02CreateAccount(accountName, password))
                .waitForPacket(P03CreateAccountResponse.class, packet -> {
                    if (packet.errorCode == CreateAccountError.ACCOUNT_NAME_TAKEN)
                        Dialogs.showMessageBox(this, R.string.error_header_create_acc, R.string.error_account_name_taken);
                    else if (packet.errorCode == CreateAccountError.INVALID_ACCOUNT_NAME)
                        Dialogs.showMessageBox(this, R.string.error_header_create_acc, R.string.error_account_name_invalid);
                    else if (packet.errorCode == CreateAccountError.SUCCESS)
                        Dialogs.showMessageBox(this, R.string.success_header_create_acc, R.string.success_create_acc);
                });
    }
}
