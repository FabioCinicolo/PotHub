package com.cindea.pothub.home.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cindea.pothub.R;
import com.cindea.pothub.entities.Pothole;
import com.cindea.pothub.home.contracts.RightHomeContract;
import com.cindea.pothub.home.models.RightHomeModel;
import com.cindea.pothub.home.presenters.RightHomePresenter;
import com.cindea.pothub.map.LiveMapActivity;
import com.cindea.pothub.map.MapFragment;
import com.cindea.pothub.map.VisualizePotholesInMapActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RightHomeFragment extends Fragment implements RightHomeContract.View {

    private View view;
    private Button button_100mt, button_250mt, button_500mt, button_1km, button_5km;
    private Button position_button;
    private Button button_visualize_in_map;
    private RightHomeContract.Presenter presenter;
    private List<Pothole> potholes;
    private double latitude, longitude;
    private boolean first_load = true;

    private FusedLocationProviderClient fusedLocationClient;

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_right_home, container, false);

        presenter = new RightHomePresenter(this, new RightHomeModel());
        setupComponents();
        listeners();
        filterListeners();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        button_100mt.performClick();
                    }
                });

        return view;
    }

    private void setupComponents() {

        button_100mt = view.findViewById(R.id.fragmentRightHome_100mt);
        position_button = button_100mt;
        button_250mt  = view.findViewById(R.id.fragmentRightHome_250mt);
        button_500mt  = view.findViewById(R.id.fragmentRightHome_500mt);
        button_1km = view.findViewById(R.id.fragmentRightHome_1km);
        button_5km = view.findViewById(R.id.fragmentRightHome_5km);
        button_visualize_in_map = view.findViewById(R.id.fragmentRightHome_VisualizeInMap);

    }

    private void listeners() {

        button_visualize_in_map.setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), VisualizePotholesInMapActivity.class);

            MapFragment.map_potholes = (ArrayList<Pothole>) potholes;

            getActivity().startActivity(intent);

        });

    }

    private boolean checkIfSamePosition(Button button) {

        return (position_button.getCurrentTextColor() == button.getCurrentTextColor()) ? true : false;

    }

    private void filterListeners() {

        button_100mt.setOnClickListener(view -> {

            if(first_load)
                presenter.getPotholesByRange(100, latitude, longitude);

            if(!checkIfSamePosition(button_100mt)) {

                button_100mt.setTextColor(getResources().getColor(R.color.edit_text_orange));
                position_button.setTextColor(getResources().getColor(R.color.text_in_edit));
                position_button = button_100mt;


                presenter.getPotholesByRange(100, latitude, longitude);
                Log.e("LATLNG", latitude + " " + longitude);

            }

        });

        button_250mt.setOnClickListener(view -> {

            if(!checkIfSamePosition(button_250mt)) {

                button_250mt.setTextColor(getResources().getColor(R.color.edit_text_orange));
                position_button.setTextColor(getResources().getColor(R.color.text_in_edit));
                position_button = button_250mt;

                presenter.getPotholesByRange(250, latitude, longitude);
                Log.e("LATLNG", latitude + " " + longitude);

            }

        });

        button_500mt.setOnClickListener(view -> {

            if(!checkIfSamePosition(button_500mt)) {

                button_500mt.setTextColor(getResources().getColor(R.color.edit_text_orange));
                position_button.setTextColor(getResources().getColor(R.color.text_in_edit));
                position_button = button_500mt;

                presenter.getPotholesByRange(500, latitude, longitude);
                Log.e("LATLNG", latitude + " " + longitude);

            }

        });

        button_1km.setOnClickListener(view -> {

            if(!checkIfSamePosition(button_1km)) {

                button_1km.setTextColor(getResources().getColor(R.color.edit_text_orange));
                position_button.setTextColor(getResources().getColor(R.color.text_in_edit));
                position_button = button_1km;

                presenter.getPotholesByRange(1000, latitude, longitude);
                Log.e("LATLNG", latitude + " " + longitude);

            }

        });

        button_5km.setOnClickListener(view -> {

            if(!checkIfSamePosition(button_5km)) {

                button_5km.setTextColor(getResources().getColor(R.color.edit_text_orange));
                position_button.setTextColor(getResources().getColor(R.color.text_in_edit));
                position_button = button_5km;

                presenter.getPotholesByRange(200000, latitude, longitude);

            }

        });

    }

    public List<Pothole> getPotholes() {
        return potholes;
    }

    @Override
    public void onPotholesLoaded(List<Pothole> potholes) {
        getActivity().runOnUiThread(() -> {

            this.potholes = potholes;

                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.rightHome_fragment, new PotholesFragment());
                fragmentTransaction.commit();

        });
    }

    @Override
    public void onError(String message) {
        Toast.makeText(getContext(), "No potholes in this range", Toast.LENGTH_LONG).show();
    }
}