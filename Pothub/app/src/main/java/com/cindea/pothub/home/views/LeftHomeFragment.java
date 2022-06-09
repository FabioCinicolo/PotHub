package com.cindea.pothub.home.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class LeftHomeFragment extends Fragment implements LeftHomeContract.View {

    private List<Pothole> potholes_14days;
    private View view;
    private LeftHomeContract.Presenter presenter;


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

    }

    public List<Pothole> getPotholes_14days() {
        return potholes_14days;
    }

    @Override
    public void onPotholesLoaded(List<Pothole> potholes) {
        getActivity().runOnUiThread(() -> {
            potholes_14days= potholes;

            if(potholes != null) {

                ((TextView) view.findViewById(R.id.leftHome_username)).setText(SigninFragment.username);
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.leftHome_fragment, new UserPotholesFragment());
                fragmentTransaction.commit();

            }

        });

    }

    @Override
    public void onError(String message) {
        //NON SONO RIUSCITO A CARICARE LE POTHOLE
    }
}