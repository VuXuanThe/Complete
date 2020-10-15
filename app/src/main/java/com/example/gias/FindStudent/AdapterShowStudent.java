package com.example.gias.FindStudent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gias.Object.Student;
import com.example.gias.R;

import java.util.List;

public class AdapterShowStudent extends RecyclerView.Adapter<AdapterShowStudent.Viewhorder> {
    List<Student> students;
    ClickItemStudent clickItemStudent;
    Context context;

    public AdapterShowStudent(List<Student> students, Context context) {
        this.students = students;
        this.context = context;
    }

    public void setClickItemStudent(ClickItemStudent clickItemStudent) {
        this.clickItemStudent = clickItemStudent;
    }

    @NonNull
    @Override
    public Viewhorder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_list_student, parent, false);
        Viewhorder viewhorder = new Viewhorder(view);
        return viewhorder;
    }

    @Override
    public void onBindViewHolder(@NonNull Viewhorder holder, int position) {
        final Student student = students.get(position);
        holder.tvAcount_nane_Student.setText(student.getStudentName());
        holder.tvStudy.setText(student.getStudy());
        holder.tvAge.setText(student.getAge());
        holder.tvSubject.setText(student.getSubject());
        holder.tvGender.setText(student.getGender());
        holder.imgAvatar.setImageBitmap(student.getAvatarStudent());
        holder.view_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickItemStudent.onClick(student);
            }
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public class Viewhorder extends RecyclerView.ViewHolder {
        TextView tvAcount_nane_Student, tvStudy, tvSubject, tvGender, tvAge;
        ImageView imgAvatar;
        View view_item;
        public Viewhorder(@NonNull View itemView) {
            super(itemView);
            tvAcount_nane_Student = itemView.findViewById(R.id.tvAcount_name);
            tvStudy = itemView.findViewById(R.id.tvStudy);
            tvSubject = itemView.findViewById(R.id.tvSubjectStudent);
            tvGender = itemView.findViewById(R.id.tvGender);
            tvAge = itemView.findViewById(R.id.tvAge);
            imgAvatar = itemView.findViewById(R.id.imgAcountStudent);
            view_item = itemView.findViewById(R.id.view_item_student);
        }
    }
}
