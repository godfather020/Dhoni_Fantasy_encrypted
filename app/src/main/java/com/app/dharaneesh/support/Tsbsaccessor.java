package com.app.dharaneesh.support;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class Tsbsaccessor extends FragmentPagerAdapter {

    public Tsbsaccessor(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
            fragment = new chatfragment();
        else if (position == 1)
            fragment = new groups();

        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        String title = null;
        if(position == 0){
            title = "Chat";
        }else if(position==1){
            title = "Groups";
        }


        return title;
    }
}
