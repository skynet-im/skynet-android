package de.vectordata.skynet.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import de.vectordata.skynet.R;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.messages.ChannelMessageConfig;
import de.vectordata.skynet.net.packet.P0CChannelMessageResponse;
import de.vectordata.skynet.net.packet.P25Nickname;
import de.vectordata.skynet.net.packet.model.MessageFlags;
import de.vectordata.skynet.util.Activities;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Activities.setStatusBarTranslucent(this);

        EditText nicknameInput = findViewById(R.id.input_nickname);
        Button continueButton = findViewById(R.id.button_continue);

        continueButton.setOnClickListener(v -> {
            String nn = nicknameInput.getText().toString();
            new Thread(() -> {
                Channel accountDataChannel = Storage.getDatabase().channelDao().getByType(Storage.getSession().getAccountId(), ChannelType.ACCOUNT_DATA);
                P25Nickname packet = new P25Nickname(nn);
                SkynetContext.getCurrent().getMessageInterface().sendChannelMessage(accountDataChannel, new ChannelMessageConfig().addFlag(MessageFlags.UNENCRYPTED), packet)
                        .waitForPacket(P0CChannelMessageResponse.class, p -> runOnUiThread(this::finish));
            }).start();
        });
    }

}
