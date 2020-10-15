package com.example.gias.FindTeacher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gias.Object.Teacher;
import com.example.gias.R;

import java.util.List;

public class AdapterShowTeacher extends RecyclerView.Adapter<AdapterShowTeacher.Viewhorder>{
    List<Teacher> teachers;
    ClickItemTeacher clickItemTeacher;
    Context context;

    public AdapterShowTeacher(List<Teacher> teachers, Context context) {
        this.teachers   = teachers;
        this.context    = context;
    }

    public void setClickItemTeacher(ClickItemTeacher clickItemTeacher){
        this.clickItemTeacher = clickItemTeacher;
    }

    @NonNull
    @Override
    public Viewhorder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_list_teacher, parent, false);
        Viewhorder viewhorder = new Viewhorder(view);
        return viewhorder;
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewhorder holder, int position) {
        final Teacher teacher = teachers.get(position);
        holder.tvAcount_name_Teacher.setText(teacher.getTeacherName());
        holder.tvExperient.setText(teacher.getExperient());
        holder.tvSubject.setText(teacher.getSubject());
        holder.tvGender.setText(teacher.getGender());
        holder.tvAge.setText(String.valueOf(teacher.getAge()));
        holder.imgAvatar.setImageBitmap(teacher.getAvatarTeacher());
        holder.view_item.setOnClickListener( new View.OnClickListener() {
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
        TextView tvAcount_name_Teacher, tvExperient, tvSubject, tvGender, tvAge;
        ImageView imgAvatar;
        View view_item;
        public Viewhorder(@NonNull View itemView) {
            super(itemView);
            tvAcount_name_Teacher = itemView.findViewById(R.id.tvAcount_name);
            tvExperient = itemView.findViewById(R.id.tvExperient);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvGender = itemView.findViewById(R.id.tvGender);
            tvAge = itemView.findViewById(R.id.tvAge);
            imgAvatar = itemView.findViewById(R.id.imgAcountTeacher);
            view_item = itemView.findViewById(R.id.view_item_teacher);
        }
    }
}
