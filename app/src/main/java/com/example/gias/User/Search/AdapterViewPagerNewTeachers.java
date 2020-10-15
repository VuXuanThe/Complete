package com.example.gias.User.Search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.gias.FindTeacher.ClickItemTeacher;
import com.example.gias.Helper;
import com.example.gias.Object.Teacher;
import com.example.gias.R;

import java.util.List;

public class AdapterViewPagerNewTeachers extends RecyclerView.Adapter<AdapterViewPagerNewTeachers.Viewhorder> {
    List<Teacher> teachers;
    ClickItemTeacher clickItemTeacher;
    Context context;
    private ViewPager2 viewPager2;

    public AdapterViewPagerNewTeachers(List<Teacher> teachers, Context context, ViewPager2 viewPager2) {
        this.teachers = teachers;
        this.context = context;
        this.viewPager2 = viewPager2;
    }

    public void setClickItemTeacher(ClickItemTeacher clickItemTeacher) {
        this.clickItemTeacher = clickItemTeacher;
    }

    @NonNull
    @Override
    public AdapterViewPagerNewTeachers.Viewhorder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_new_teacher, parent, false);
        AdapterViewPagerNewTeachers.Viewhorder viewhorder = new AdapterViewPagerNewTeachers.Viewhorder(view);
        return viewhorder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewPagerNewTeachers.Viewhorder holder, int position) {
        final Teacher teacher = teachers.get(position);
        holder.tvAcount_nane_Teacher.setText(teacher.getTeacherName());
        holder.tvExperient.setText(teacher.getExperient());
        holder.tvAge.setText(teacher.getAge());
        holder.tvSubject.setText(teacher.getSubject());
        holder.tvGender.setText(teacher.getGender());
        Glide.with(context).load(teacher.getLinkAvatarTeacher()).into(holder.imgAvatar);
        holder.view_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickItemTeacher.onClick(teacher);
            }
        });
        Glide.with(context).load(teacher.getLinkAvatarTeacher()).into(holder.imgView);
        holder.tvAdress.setText(Helper.Adress(context, teacher.getLongitude(), teacher.getLatitude()));
        if(position == teachers.size() - 2){
            viewPager2.post(runnable);
        }
    }

    @Override
    public int getItemCount() {
        return teachers.size();
    }

    public class Viewhorder extends RecyclerView.ViewHolder {
        TextView tvAcount_nane_Teacher,
                tvExperient,
                tvSubject,
                tvGender,
                tvAge,
                tvAdress;
        ImageView imgAvatar, imgView;
        View view_item;
        public Viewhorder(@NonNull View itemView) {
            super(itemView);
            tvAcount_nane_Teacher   = itemView.findViewById(R.id.tvAcount_name);
            tvExperient             = itemView.findViewById(R.id.tvExperient);
            tvSubject               = itemView.findViewById(R.id.tvSubjectStudent);
            tvGender                = itemView.findViewById(R.id.tvGender);
            tvAge                   = itemView.findViewById(R.id.tvAge);
            tvAdress                = itemView.findViewById(R.id.tvDetailAdress);
            imgAvatar               = itemView.findViewById(R.id.imgAcountTeacher);
            imgView               = itemView.findViewById(R.id.imgView);
            view_item               = itemView.findViewById(R.id.view_item);
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            teachers.addAll(teachers);
            notifyDataSetChanged();
        }
    };
}
