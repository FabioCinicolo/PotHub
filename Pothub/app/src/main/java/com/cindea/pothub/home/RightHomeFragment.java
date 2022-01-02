package com.cindea.pothub.home;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cindea.pothub.R;

public class RightHomeFragment extends Fragment {

    Button[] buttons = new Button[5];
    int filter_position = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_right_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupComponents(view);
        customListeners();
    }

    private void setupComponents(View view) {

        buttons[0] = view.findViewById(R.id.fragmentRightHome_100mt);
        buttons[1] = view.findViewById(R.id.fragmentRightHome_250mt);
        buttons[2] = view.findViewById(R.id.fragmentRightHome_500mt);
        buttons[3] = view.findViewById(R.id.fragmentRightHome_1km);
        buttons[4] = view.findViewById(R.id.fragmentRightHome_5km);

    }

    private void customListeners() {

        buttons[0].setOnClickListener(view -> {

            if(!checkIfSamePosition(0)) {

                buttons[0].setTextColor(getContext().getResources().getColor(R.color.edit_text_orange));
                filter_position = 0;
                buttons[filter_position].setTextColor(Color.parseColor("#979797"));

            }

        });

        buttons[1].setOnClickListener(view -> {

            if(!checkIfSamePosition(1)) {

                buttons[1].setTextColor(getContext().getResources().getColor(R.color.edit_text_orange));
                filter_position = 1;
                buttons[filter_position].setTextColor(Color.parseColor("#979797"));

            }

        });

        buttons[2].setOnClickListener(view -> {

            if(!checkIfSamePosition(2)) {

                Log.e("ERROR", "In if");

                buttons[2].setTextColor(getContext().getResources().getColor(R.color.edit_text_orange));
                filter_position = 2;
                buttons[filter_position].setTextColor(Color.parseColor("#979797"));

            }

        });

        buttons[3].setOnClickListener(view -> {

            if(!checkIfSamePosition(3)) {

                buttons[3].setTextColor(Color.parseColor("#FD7739"));
                filter_position = 3;
                buttons[filter_position].setTextColor(Color.parseColor("#979797"));

            }

        });

        buttons[4].setOnClickListener(view -> {

            if(!checkIfSamePosition(4)) {

                buttons[4].setTextColor(Color.parseColor("#FD7739"));
                filter_position = 4;
                buttons[filter_position].setTextColor(Color.parseColor("#979797"));

            }

        });

    }

    private boolean checkIfSamePosition(int position) {

        return position == filter_position;

    }
}