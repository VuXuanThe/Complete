package com.example.gias.User.Chats.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gias.FindTeacher.ClickItemTeacher;
import com.example.gias.Helper;
import com.example.gias.Object.Teacher;
import com.example.gias.R;

import java.util.List;

public class AdapterTeacherSearch extends RecyclerView.Adapter<AdapterTeacherSearch.Viewhorder> {
    List<Teacher> teachers;
    ClickItemTeacher clickItemTeacher;
    Context context;

    public AdapterTeacherSearch(List<Teacher> teachers, Context context) {
        this.teachers = teachers;
        this.context = context;
    }

    public void setClickItemTeacher(ClickItemTeacher clickItemTeacher){
        this.clickItemTeacher = clickItemTeacher;
    }

    @NonNull
    @Override
    public AdapterTeacherSearch.Viewhorder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_user_search, parent, false);
        Viewhorder viewhorder = new Viewhorder(view);
        return viewhorder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterTeacherSearch.Viewhorder holder, int position) {
        final Teacher teacher = teachers.get(position);
        holder.tvUserName.setText(teacher.getTeacherName());
        holder.gender.setText(teacher.getGender());
        holder.object.setText(Helper.TEACHER_TV);
        if(teacher.getStatus().equals("online"))
            holder.online.setVisibility(View.VISIBLE);
        else
            holder.online.setVisibility(View.GONE);
        Glide.with(context).load(teacher.getLinkAvatarTeacher()).into(holder.imgAvatarUser);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickItemTeacher.onClick(teacher);
            }
        });
    }

    @Override
    public int getItemCount() {
        return teachers.size();
    }

    public class Viewhorder extends RecyclerView.ViewHolder {
        TextView tvUserName, gender, object;
        ImageView imgAvatarUser, online;
        View view_item;
        public Viewhorder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            gender = itemView.findViewById(R.id.tvGender);
            object = itemView.findViewById(R.id.tvObject);
            imgAvatarUser = itemView.findViewById(R.id.imgAvatarUser);
            online = itemView.findViewById(R.id.online);
            view_item = itemView.findViewById(R.id.view_item);
        }
    }
}
