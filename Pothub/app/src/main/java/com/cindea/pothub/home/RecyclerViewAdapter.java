package com.cindea.pothub.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cindea.pothub.R;
import com.cindea.pothub.entities.Pothole;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    Context context;
    ArrayList<Pothole> potholes;

    public RecyclerViewAdapter(Context context, ArrayList<Pothole> potholes) {

        this.context = context;
        this.potholes = potholes;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {



    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return potholes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView address;
        TextView latlng;
        TextView date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            address = itemView.findViewById(R.id.item_address);
            latlng = itemView.findViewById(R.id.item_latlng);
            date = itemView.findViewById(R.id.item_date);

        }
    }

}
