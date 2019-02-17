package de.vectordata.skynet.ui;

import android.os.Bundle;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.vectordata.libjvsl.util.cscompat.DateTime;
import de.vectordata.skynet.R;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.messages.MessageInterface;
import de.vectordata.skynet.net.packet.P0ACreateChannel;
import de.vectordata.skynet.net.packet.P2DSearchAccount;
import de.vectordata.skynet.net.packet.P2ESearchAccountResponse;
import de.vectordata.skynet.net.packet.P2FCreateChannelResponse;
import de.vectordata.skynet.net.packet.base.Packet;
import de.vectordata.skynet.ui.base.ThemedActivity;
import de.vectordata.skynet.ui.dialogs.Dialogs;
import de.vectordata.skynet.ui.dialogs.ProgressDialog;
import de.vectordata.skynet.ui.main.recycler.ChatsAdapter;
import de.vectordata.skynet.ui.main.recycler.ChatsItem;
import de.vectordata.skynet.util.Activities;

public class AddContactActivity extends ThemedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        Activities.enableUpButton(this);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        List<ChatsItem> dataset = new ArrayList<>();
        ChatsAdapter adapter = new ChatsAdapter(dataset);
        recyclerView.setAdapter(adapter);

        adapter.setItemClickListener(item -> {
            long accountId = dataset.get(item).getChannelId(); // TODO: Naming is bad. But we have to change the UI here anyways.
            Packet packet = new P0ACreateChannel(MessageInterface.newId(), ChannelType.DIRECT, Storage.getSession().getAccountId(), accountId);
            ProgressDialog dialog = Dialogs.showProgressDialog(this, R.string.progress_creating_channel, false);
            SkynetContext.getCurrent().getNetworkManager().sendPacket(packet).waitForPacket(P2FCreateChannelResponse.class, px -> runOnUiThread(dialog::dismiss));
        });

        EditText searchInput = findViewById(R.id.input_search_user);
        findViewById(R.id.action_search).setOnClickListener(v -> {
            ProgressDialog progressDialog = Dialogs.showProgressDialog(this, R.string.progress_searching, true);
            SkynetContext.getCurrent().getNetworkManager().sendPacket(new P2DSearchAccount(searchInput.getText().toString()))
                    .waitForPacket(P2ESearchAccountResponse.class, p -> runOnUiThread(() -> {
                        if (progressDialog.isCancelled())
                            return;
                        progressDialog.dismiss();
                        List<P2ESearchAccountResponse.Result> results = p.results;
                        dataset.clear();
                        for (P2ESearchAccountResponse.Result result : results) {
                            ChatsItem item = new ChatsItem(result.accountName, Long.toHexString(result.accountId), DateTime.now(), 0, 0, result.accountId);
                            dataset.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    }));
        });
    }

}
