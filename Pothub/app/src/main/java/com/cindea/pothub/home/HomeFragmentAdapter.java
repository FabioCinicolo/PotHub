package com.cindea.pothub.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.cindea.pothub.home.views.LeftHomeFragment;
import com.cindea.pothub.home.views.MiddleHomeFragment;
import com.cindea.pothub.home.views.RightHomeFragment;

public class HomeFragmentAdapter extends FragmentStateAdapter {


    public HomeFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {

            case 1:
                return new MiddleHomeFragment();
            case 2:
                return new RightHomeFragment();

        }

        return new LeftHomeFragment();

    }
}
