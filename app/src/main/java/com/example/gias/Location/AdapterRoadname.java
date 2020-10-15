package com.example.gias.Location;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gias.R;

import java.util.List;

public class AdapterRoadname extends RecyclerView.Adapter<AdapterRoadname.Viewhorder>{
    Context context;
    List<String> roadNames;

    public AdapterRoadname(Context context, List<String> roadNames) {
        this.context = context;
        this.roadNames = roadNames;
    }

    @NonNull
    @Override
    public AdapterRoadname.Viewhorder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_direction, parent, false);
        AdapterRoadname.Viewhorder viewhorder = new AdapterRoadname.Viewhorder(view);
        return viewhorder;
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterRoadname.Viewhorder holder, int position) {
        holder.nameRoad.setText(roadNames.get(position));
    }

    @Override
    public int getItemCount() {
        return roadNames.size();
    }

    public class Viewhorder extends RecyclerView.ViewHolder {
        TextView nameRoad;
        public Viewhorder(@NonNull View itemView) {
            super(itemView);
            nameRoad = itemView.findViewById(R.id.nameRoad);
        }
    }
}
