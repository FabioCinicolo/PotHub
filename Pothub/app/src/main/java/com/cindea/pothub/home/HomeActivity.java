package com.cindea.pothub.home;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.cindea.pothub.R;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

public class HomeActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupComponents();
        viewPager2.setCurrentItem(1, false);


    }

    private void setupComponents() {

        viewPager2 = findViewById(R.id.activityHome_viewpager2);
        HomeFragmentAdapter homeFragmentAdapter = new HomeFragmentAdapter(this);

        viewPager2.setAdapter(homeFragmentAdapter);
        DotsIndicator dotsIndicator = findViewById(R.id.activityHome_dotsindicators);
        dotsIndicator.setViewPager2(viewPager2);

    }

}