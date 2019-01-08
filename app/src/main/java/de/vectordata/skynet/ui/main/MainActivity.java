package de.vectordata.skynet.ui.main;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import de.vectordata.skynet.R;
import de.vectordata.skynet.ui.AddContactActivity;
import de.vectordata.skynet.ui.LoginActivity;
import de.vectordata.skynet.ui.NewGroupActivity;
import de.vectordata.skynet.ui.SkynetActivity;
import de.vectordata.skynet.ui.main.fab.FabController;
import de.vectordata.skynet.ui.main.fab.FabState;

public class MainActivity extends SkynetActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        ViewPager viewPager = findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        Objects.requireNonNull(tabLayout.getTabAt(1)).select();

        FloatingActionButton fab = findViewById(R.id.fab);
        FabController.with(fab)
                .addState(new FabState(R.drawable.ic_person_add, v -> startActivity(AddContactActivity.class)))
                .addState(FabState.invisible())
                .addState(new FabState(R.drawable.ic_add_a_photo_black_24dp, v -> {

                }))
                .setInitialState(1)
                .attach(tabLayout);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_contact) {
            startActivity(AddContactActivity.class);
            return true;
        }

        if (id == R.id.action_new_group) {
            startActivity(NewGroupActivity.class);
            return true;
        }

        if (id == R.id.action_settings) {
            startActivity(LoginActivity.class);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
