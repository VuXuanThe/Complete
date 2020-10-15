package com.example.gias.User.Manager.DataConnect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gias.Helper;
import com.example.gias.Object.DataConnect;
import com.example.gias.R;

import java.util.List;

public class AdapterDataConnect extends RecyclerView.Adapter<AdapterDataConnect.Viewhorder> {
    List<DataConnect> dataConnects;
    Context context;
    String object;
    IClickItemDataConnect iClickItemDataConnect;

    public AdapterDataConnect(List<DataConnect> dataConnects, Context context, String object) {
        this.dataConnects = dataConnects;
        this.context = context;
        this.object = object;
    }

    public void setiClickItemDataConnect(IClickItemDataConnect iClickItemDataConnect){
        this.iClickItemDataConnect = iClickItemDataConnect;
    }

    @NonNull
    @Override
    public AdapterDataConnect.Viewhorder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_dataconnect, parent, false);
        AdapterDataConnect.Viewhorder viewhorder = new AdapterDataConnect.Viewhorder(view);
        return viewhorder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDataConnect.Viewhorder holder, int position) {
        final DataConnect dataConnect = dataConnects.get(position);
        if(dataConnect.getUpdate().equals("no")){
            holder.viewUpdate.setVisibility(View.VISIBLE);
            holder.tvSubject.setText("Cần cập nhật");
        }
        else if(dataConnect.getUpdate().equals("Wait Student")){
            holder.viewUpdate.setText("Chờ " + dataConnect.getStudent().getStudentName() + " xác nhận");
            holder.viewUpdate.setVisibility(View.VISIBLE);
            holder.tvSubject.setText("Môn " + dataConnect.getSubject());
        }
        else if(dataConnect.getUpdate().equals("Wait Teacher")){
            holder.viewUpdate.setText("Chờ " + dataConnect.getTeacher().getTeacherName() + " xác nhận");
            holder.viewUpdate.setVisibility(View.VISIBLE);
            holder.tvSubject.setText("Môn " + dataConnect.getSubject());
        }
        else {
            holder.viewUpdate.setVisibility(View.GONE);
            holder.tvSubject.setText(dataConnect.getSubject());
        }

        holder.tvPhoneNumberText.setText(Helper.setUnderlineString("Số điện thoại"));
        if(object.equals(Helper.TEACHER)){
            holder.tvObject.setText(Helper.setUnderlineString(Helper.TEACHER_TV));
            holder.tvName.setText(dataConnect.getTeacher().getTeacherName());
            holder.tvPhoneNumber.setText("0" + dataConnect.getTeacher().getPhoneNumber().substring(3));
            Glide.with(context).load(dataConnect.getTeacher().getLinkAvatarTeacher()).into(holder.imgAcount);
        }
        else{
            holder.tvObject.setText(Helper.setUnderlineString(Helper.STUDENT_TV));
            holder.tvName.setText(dataConnect.getStudent().getStudentName());
            holder.tvPhoneNumber.setText("0" + dataConnect.getStudent().getPhoneNumber().substring(3));
            Glide.with(context).load(dataConnect.getStudent().getLinkAvatarStudent()).into(holder.imgAcount);
        }

        holder.view_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickItemDataConnect.onClick(dataConnect);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataConnects.size();
    }

    public class Viewhorder extends RecyclerView.ViewHolder {
        ImageView imgAcount;
        TextView tvObject, tvName, tvPhoneNumberText, tvPhoneNumber, viewUpdate, tvSubject;
        View view_item;
        public Viewhorder(@NonNull View itemView) {
            super(itemView);
            imgAcount = itemView.findViewById(R.id.imgAcount);
            tvObject = itemView.findViewById(R.id.tvObject);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhoneNumberText = itemView.findViewById(R.id.tvPhoneNumberText);
            tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
            viewUpdate = itemView.findViewById(R.id.viewUpdate);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            view_item = itemView.findViewById(R.id.view_item);
        }
    }
}
