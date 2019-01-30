package de.vectordata.skynet.ui;

import android.os.Bundle;

import de.vectordata.skynet.R;
import de.vectordata.skynet.ui.base.ThemedActivity;
import de.vectordata.skynet.util.Activities;

public class NewGroupActivity extends ThemedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        Activities.enableUpButton(this);
    }
}
