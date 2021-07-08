package com.android.onlymsg.adapter;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.android.onlymsg.fragment.ContatsFragment;
import com.android.onlymsg.fragment.ConversationsFragment;

public class TabAdapter extends FragmentStatePagerAdapter {

    private String[] titleTabs = {"Conversas", "Contatos"};

    public TabAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch ( position ){
            case 0:
                fragment = new ConversationsFragment();
                break;
            case 1:
                fragment = new ContatsFragment();
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return titleTabs.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleTabs[position];
    }
}
