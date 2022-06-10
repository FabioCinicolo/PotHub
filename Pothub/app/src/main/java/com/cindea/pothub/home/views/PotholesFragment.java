package com.cindea.pothub.home.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cindea.pothub.R;
import com.cindea.pothub.entities.Pothole;
import com.cindea.pothub.home.RecyclerViewAdapter;

import java.util.List;

public class PotholesFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_potholes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        recyclerView = view.findViewById(R.id.potholesFragment_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //TEST
        List<Pothole> potholes = ((RightHomeFragment)getParentFragment()).getPotholes();
        recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), potholes);
        recyclerView.setAdapter(recyclerViewAdapter);
        //TEST

    }

}