package de.vectordata.skynet.ui.main.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.vectordata.libjvsl.util.cscompat.DateTime;
import de.vectordata.skynet.R;
import de.vectordata.skynet.ui.chat.ChatActivityBase;
import de.vectordata.skynet.ui.chat.ChatActivityDirect;
import de.vectordata.skynet.ui.main.recycler.ChatsAdapter;
import de.vectordata.skynet.ui.main.recycler.ChatsItem;
import de.vectordata.skynet.ui.util.MessageSide;
import de.vectordata.skynet.ui.util.MessageState;

/**
 * Created by Twometer on 14.12.2018.
 * (c) 2018 Twometer
 */
public class ChatsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);

        List<ChatsItem> dataset = new ArrayList<>();

        dataset.add(new ChatsItem("Philipp", "Kannst du mir bei Informatik helfen?", DateTime.now(), 0, 2, 0));
        dataset.add(new ChatsItem("Lea \uD83D\uDC96", "Hey wie geht's? :)", ago(0, 1, 0), 0, 1, 1));
        dataset.add(new ChatsItem("Daniel", "Im Protokoll haben wir noch ein Problem, können wir da nacher drüber reden", ago(0, 8, 0), 0, MessageSide.RIGHT, MessageState.SENT, 0, 2));
        dataset.add(new ChatsItem("Jan", "Am Wochenende Lan Party?", ago(3, 8, 1), 0, 0, 3));
        dataset.add(new ChatsItem("Timon", "Ich muss mal dringend neuen RAM kaufen...", ago(5, 8, 0), 0, MessageSide.RIGHT, MessageState.SEEN, 0, 4));
        dataset.add(new ChatsItem("Max", "Wann geht dein Flieger?", ago(24, 8, 1), 0, MessageSide.RIGHT, MessageState.SENT, 0, 5));
        dataset.add(new ChatsItem("Test", "Test", ago(48, 8, 1), 0, MessageSide.RIGHT, MessageState.SENT, 0, 6));

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ChatsAdapter adapter = new ChatsAdapter(dataset);
        adapter.setItemClickListener(idx -> {
            ChatsItem item = dataset.get(idx);
            Intent intent = new Intent(getContext(), ChatActivityDirect.class);
            intent.putExtra(ChatActivityBase.EXTRA_CHANNEL_ID, item.getChannelId());
            getContext().startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    private DateTime ago(int hr, int min, int sec) {
        return DateTime.fromMillis(System.currentTimeMillis() - sec * 1000 - min * 60000 - hr * 3600000);
    }

}
