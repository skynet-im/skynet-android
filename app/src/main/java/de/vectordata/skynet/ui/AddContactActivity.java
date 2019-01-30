package de.vectordata.skynet.ui;

import android.os.Bundle;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.vectordata.skynet.R;
import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.packet.P2DSearchAccount;
import de.vectordata.skynet.net.packet.P2ESearchAccountResponse;
import de.vectordata.skynet.ui.base.ThemedActivity;
import de.vectordata.skynet.ui.dialogs.Dialogs;
import de.vectordata.skynet.ui.dialogs.ProgressDialog;
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

        EditText searchInput = findViewById(R.id.input_search_user);
        findViewById(R.id.action_search).setOnClickListener(v -> {
            ProgressDialog progressDialog = Dialogs.showProgressDialog(this, R.string.progress_searching, true);
            SkynetContext.getCurrent().getNetworkManager().sendPacket(new P2DSearchAccount(searchInput.getText().toString()))
                    .waitForPacket(P2ESearchAccountResponse.class, p -> runOnUiThread(() -> {
                        if (progressDialog.isCancelled())
                            return;
                        progressDialog.dismiss();
                    }));
        });
    }

}
