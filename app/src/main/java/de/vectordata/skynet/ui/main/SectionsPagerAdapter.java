package de.vectordata.skynet.ui.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import de.vectordata.skynet.ui.main.fragments.ChatsFragment;
import de.vectordata.skynet.ui.main.fragments.ContactsFragment;
import de.vectordata.skynet.ui.main.fragments.DaystreamFragment;

/**
 * Created by Twometer on 14.12.2018.
 * (c) 2018 Twometer
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    SectionsPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0)
            return new ContactsFragment();
        if (position == 1)
            return new ChatsFragment();
        if (position == 2)
            return new DaystreamFragment();
        throw new ArrayIndexOutOfBoundsException(position);
    }

    @Override
    public int getCount() {
        return 3;
    }

}
