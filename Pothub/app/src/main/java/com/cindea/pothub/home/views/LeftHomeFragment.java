package com.cindea.pothub.home.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cindea.pothub.R;
import com.cindea.pothub.authentication.views.fragments.SigninFragment;
import com.cindea.pothub.entities.Pothole;
import com.cindea.pothub.home.contracts.LeftHomeContract;
import com.cindea.pothub.home.models.LeftHomeModel;
import com.cindea.pothub.home.presenters.LeftHomePresenter;
import com.cindea.pothub.map.MapFragment;
import com.cindea.pothub.map.VisualizePotholesInMapActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class LeftHomeFragment extends Fragment implements LeftHomeContract.View {

    private List<Pothole> potholes_14days;
    private View view;
    private LeftHomeContract.Presenter presenter;
    private Button button_visualize_in_map;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_left_home, container, false);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new LeftHomePresenter(this, new LeftHomeModel());
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        presenter.getUserPotholesByDays(SigninFragment.username, formatter.format(date));

        button_visualize_in_map = view.findViewById(R.id.fragmentLeftHome_VisualizeInMap);

        button_visualize_in_map.setOnClickListener(v -> {

            getActivity().startActivity(
                    new Intent(getActivity(), VisualizePotholesInMapActivity.class));

        });

    }

    public List<Pothole> getPotholes_14days() {
        return potholes_14days;
    }

    @Override
    public void onPotholesLoaded(List<Pothole> potholes) {
        getActivity().runOnUiThread(() -> {
            potholes_14days= potholes;

                ((TextView) view.findViewById(R.id.leftHome_username)).setText(SigninFragment.username);
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.leftHome_fragment, new UserPotholesFragment());
                fragmentTransaction.commit();
                MapFragment.map_potholes = (ArrayList<Pothole>) potholes;

        });

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("TEST", "TEST");
    }

    @Override
    public void onError(String message) {
        Toast.makeText(getContext(), "No potholes reported in the last 14 days", Toast.LENGTH_LONG).show();
    }
}