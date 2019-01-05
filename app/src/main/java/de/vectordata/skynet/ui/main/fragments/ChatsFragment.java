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
import de.vectordata.skynet.ui.chat.ChatActivity;
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

        dataset.add(new ChatsItem("Message header", "This is a test message", DateTime.now(), 0, 0, MessageState.DELIVERED, MessageSide.RIGHT));
        dataset.add(new ChatsItem("Message header", "This is a test message with unread messages", DateTime.now(), 0, 1, MessageState.SEEN, MessageSide.RIGHT));
        dataset.add(new ChatsItem("Message header", "This is a very long test message so that i can see if this will look good even if there is a long message like this in the box", DateTime.now(), 0, 0, MessageState.SENDING, MessageSide.RIGHT));
        dataset.add(new ChatsItem("What about making the header extremely long, so that it will overlap the date field", "This is a test message", DateTime.now(), 0, 0, MessageState.SENT, MessageSide.RIGHT));
        dataset.add(new ChatsItem("Message header", "This is a test message", DateTime.now(), 0, 5199912, MessageState.DELIVERED, MessageSide.LEFT));
        dataset.add(new ChatsItem("Message header", "This is a very long test message so that i can see if this will look good even if there is a long message like this in the box", DateTime.now(), 0, 123, MessageState.DELIVERED, MessageSide.LEFT));

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ChatsAdapter adapter = new ChatsAdapter(dataset);
        adapter.setItemClickListener(item -> getContext().startActivity(new Intent(getContext(), ChatActivity.class)));
        recyclerView.setAdapter(adapter);

        return rootView;
    }

}
