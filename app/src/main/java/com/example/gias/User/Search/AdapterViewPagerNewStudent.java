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
import com.example.gias.FindStudent.ClickItemStudent;
import com.example.gias.Helper;
import com.example.gias.Object.Student;
import com.example.gias.R;

import java.util.List;

public class AdapterViewPagerNewStudent extends RecyclerView.Adapter<AdapterViewPagerNewStudent.Viewhorder> {
    List<Student> students;
    ClickItemStudent clickItemStudent;
    Context context;
    private ViewPager2 viewPager2;

    public AdapterViewPagerNewStudent(List<Student> students, Context context, ViewPager2 viewPager2) {
        this.students = students;
        this.context = context;
        this.viewPager2 = viewPager2;
    }

    public void setClickItemStudent(ClickItemStudent clickItemStudent) {
        this.clickItemStudent = clickItemStudent;
    }

    @NonNull
    @Override
    public AdapterViewPagerNewStudent.Viewhorder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_new_student, parent, false);
        AdapterViewPagerNewStudent.Viewhorder viewhorder = new AdapterViewPagerNewStudent.Viewhorder(view);
        return viewhorder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewPagerNewStudent.Viewhorder holder, int position) {
        final Student student = students.get(position);
        holder.tvAcount_nane_Student.setText(student.getStudentName());
        holder.tvStudy.setText(student.getStudy());
        holder.tvAge.setText(student.getAge());
        holder.tvSubject.setText(student.getSubject());
        holder.tvGender.setText(student.getGender());
        Glide.with(context).load(student.getLinkAvatarStudent()).into(holder.imgAvatar);
        Glide.with(context).load(student.getLinkAvatarStudent()).into(holder.imgView);
        holder.view_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickItemStudent.onClick(student);
            }
        });
        holder.tvAdress.setText(Helper.Adress(context, student.getLongitude(), student.getLatitude()));
        if(position == students.size() - 2){
            viewPager2.post(runnable);
        }
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public class Viewhorder extends RecyclerView.ViewHolder {
        TextView tvAcount_nane_Student, tvStudy, tvSubject, tvGender, tvAge, tvAdress;
        ImageView imgAvatar, imgView;
        View view_item;
        public Viewhorder(@NonNull View itemView) {
            super(itemView);
            tvAcount_nane_Student = itemView.findViewById(R.id.tvAcount_name);
            tvStudy = itemView.findViewById(R.id.tvStudy);
            tvSubject = itemView.findViewById(R.id.tvSubjectStudent);
            tvGender = itemView.findViewById(R.id.tvGender);
            tvAge = itemView.findViewById(R.id.tvAge);
            tvAdress = itemView.findViewById(R.id.tvDetailAdress);
            imgAvatar = itemView.findViewById(R.id.imgAcountStudent);
            imgView = itemView.findViewById(R.id.imgView);
            view_item = itemView.findViewById(R.id.view_item);
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            students.addAll(students);
            notifyDataSetChanged();
        }
    };
}
