package com.cindea.pothub.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.cindea.pothub.R;
import com.cindea.pothub.changepassword.activities.ChangePasswordActivity;

public class RightHomeFragment extends Fragment {

    private View view;
    private Button button_100mt, button_250mt, button_500mt, button_1km, button_5km;
    private Button position_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_right_home, container, false);

        setupComponents();
        listeners();
        filterListeners();

        return view;
    }


    private void setupComponents() {

        button_100mt = view.findViewById(R.id.fragmentRightHome_100mt);
        position_button = button_100mt;
        button_250mt  = view.findViewById(R.id.fragmentRightHome_250mt);
        button_500mt  = view.findViewById(R.id.fragmentRightHome_500mt);
        button_1km = view.findViewById(R.id.fragmentRightHome_1km);
        button_5km = view.findViewById(R.id.fragmentRightHome_5km);

    }

    private void listeners() {



    }

    private boolean checkIfSamePosition(Button button) {

        return (position_button.getCurrentTextColor() == button.getCurrentTextColor()) ? true : false;

    }

    private void filterListeners() {

        button_100mt.setOnClickListener(view -> {

            if(!checkIfSamePosition(button_100mt)) {

                button_100mt.setTextColor(getResources().getColor(R.color.edit_text_orange));
                position_button.setTextColor(getResources().getColor(R.color.text_in_edit));
                position_button = button_100mt;

            }

        });

        button_250mt.setOnClickListener(view -> {

            if(!checkIfSamePosition(button_250mt)) {

                button_250mt.setTextColor(getResources().getColor(R.color.edit_text_orange));
                position_button.setTextColor(getResources().getColor(R.color.text_in_edit));
                position_button = button_250mt;
                startActivity(new Intent(getActivity(), ChangePasswordActivity.class));

            }

        });

        button_500mt.setOnClickListener(view -> {

            if(!checkIfSamePosition(button_500mt)) {

                button_500mt.setTextColor(getResources().getColor(R.color.edit_text_orange));
                position_button.setTextColor(getResources().getColor(R.color.text_in_edit));
                position_button = button_500mt;

            }

        });

        button_1km.setOnClickListener(view -> {

            if(!checkIfSamePosition(button_1km)) {

                button_1km.setTextColor(getResources().getColor(R.color.edit_text_orange));
                position_button.setTextColor(getResources().getColor(R.color.text_in_edit));
                position_button = button_1km;

            }

        });

        button_5km.setOnClickListener(view -> {

            if(!checkIfSamePosition(button_5km)) {

                button_5km.setTextColor(getResources().getColor(R.color.edit_text_orange));
                position_button.setTextColor(getResources().getColor(R.color.text_in_edit));
                position_button = button_5km;

            }

        });

    }

}