package com.cindea.pothub.map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.ImageButton;

import com.cindea.pothub.R;

public class VisualizePotholesInMapActivity extends AppCompatActivity {

    ImageButton button_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualize_potholes_in_map);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.activityVisualizePotholesInMap_fragment, new MapFragment());
            fragmentTransaction.commit();

            button_back = findViewById(R.id.activityVisualizePotholesInMap_back);
            button_back.setOnClickListener(v -> finish());

    }

}