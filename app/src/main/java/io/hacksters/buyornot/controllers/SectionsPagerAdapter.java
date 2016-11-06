package io.hacksters.buyornot.controllers;

/**
 * Created by Rahimli Rahim on 05/11/2016.
 * ragim95@gmail.com
 * https://github.com/ragim/
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> fragmentList=new ArrayList<>();

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment){
        fragmentList.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "VOTE";
            case 1:
                return "COMPARE";
            case 2:
                return "UPLOAD";
            case 3:
                return "MY UPLOADS";
        }
        return null;
    }

}
