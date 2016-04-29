package com.natavit.cooklycook.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.natavit.cooklycook.fragment.MainFragment;
import com.natavit.cooklycook.fragment.MyRecipeFragment;

/**
 * This adapter is used to control 2 fragments which are MainFragment and MyRecipeFragment
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return MainFragment.newInstance();
            case 1:
                return MyRecipeFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}