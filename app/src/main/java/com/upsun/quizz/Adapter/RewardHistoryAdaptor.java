package com.upsun.quizz.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.upsun.quizz.Fragments.HistoryFragment;
import com.upsun.quizz.Fragments.RewardTabFragment;


public class RewardHistoryAdaptor extends FragmentPagerAdapter {

    public RewardHistoryAdaptor(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        RewardTabFragment rewardTabFragment = new RewardTabFragment();
        if (position == 1) {
            return new HistoryFragment();
        }
        return rewardTabFragment;
    }

    @Override
    public int getItemPosition(Object object) {
        // POSITION_NONE makes it possible to reload the PagerAdapter
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
