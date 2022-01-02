package com.cindea.pothub.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.cindea.pothub.R;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

public class HomeActivity extends AppCompatActivity {

    ViewPager2 pager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupComponents();
        pager2.setCurrentItem(1, false);


    }

    private void setupComponents() {

        pager2 = findViewById(R.id.activityHome_viewpager2);
        HomeFragmentAdapter homeFragmentAdapter = new HomeFragmentAdapter(this);

        pager2.setAdapter(homeFragmentAdapter);
        DotsIndicator dotsIndicator = findViewById(R.id.activityHome_dotsindicators);
        dotsIndicator.setViewPager2(pager2);

    }

}