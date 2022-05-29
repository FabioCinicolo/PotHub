package com.cindea.pothub.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.cindea.pothub.R;
import com.cindea.pothub.map.LiveMapActivity;

public class MiddleHomeFragment extends Fragment {

    private View view;
    private Button button_start;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_middle_home, container, false);
        button_start = view.findViewById(R.id.middleHome_start);
        listeners();

        return view;
    }

    private void listeners() {

        button_start.setOnClickListener(v -> {

            getActivity().startActivity(
                    new Intent(getActivity(), LiveMapActivity.class));

        });

    }

}