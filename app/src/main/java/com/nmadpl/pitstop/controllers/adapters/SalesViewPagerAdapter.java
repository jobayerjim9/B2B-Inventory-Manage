package com.nmadpl.pitstop.controllers.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.nmadpl.pitstop.ui.fragment.AcceptedFragment;
import com.nmadpl.pitstop.ui.fragment.DeliveredFragment;
import com.nmadpl.pitstop.ui.fragment.UpcomingFragment;

public class SalesViewPagerAdapter extends FragmentPagerAdapter {
    public SalesViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position==0) {
            return new UpcomingFragment();
        }
        else if (position==1) {
            return new AcceptedFragment();
        }
        else if (position==2) {
            return new DeliveredFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position==0) {
            return "Upcoming";
        }
        else if (position==1) {
            return "Accepted";
        }
        else {
            return "Delivered";
        }
    }
}
