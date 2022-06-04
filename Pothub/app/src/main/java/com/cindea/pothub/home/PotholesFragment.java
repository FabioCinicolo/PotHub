package com.cindea.pothub.home;

import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cindea.pothub.R;
import com.cindea.pothub.entities.Pothole;

import java.util.ArrayList;

public class PotholesFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_potholes, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.potholesFragment_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //TEST
        ArrayList<Pothole> potholes = new ArrayList<>();
        for(int i=0;i<10;i++)
            potholes.add(new Pothole(5,5,"ciao", "user",2,"15"));
        recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), potholes);
        recyclerView.setAdapter(recyclerViewAdapter);
        //TEST

    }
}