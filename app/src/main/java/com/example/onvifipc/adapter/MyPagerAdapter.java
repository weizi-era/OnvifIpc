package com.example.onvifipc.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MyPagerAdapter extends FragmentPagerAdapter {


    private final String[] mTitles;

    List<Fragment> mFragments;

    public MyPagerAdapter(@NonNull @NotNull FragmentManager fm, List<Fragment> mFragments, String[] mTitles) {
        super(fm);
        this.mFragments = mFragments;
        this.mTitles = mTitles;
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
