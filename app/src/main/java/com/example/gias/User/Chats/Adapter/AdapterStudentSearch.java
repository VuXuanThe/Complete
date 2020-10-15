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
import com.example.gias.FindStudent.ClickItemStudent;
import com.example.gias.Helper;
import com.example.gias.Object.Student;
import com.example.gias.R;

import java.util.List;

public class AdapterStudentSearch extends RecyclerView.Adapter<AdapterStudentSearch.Viewhorder> {
    List<Student> students;
    ClickItemStudent clickItemStudent;
    Context context;

    public AdapterStudentSearch(List<Student> students, Context context) {
        this.students = students;
        this.context = context;
    }

    public void setClickItemStudent(ClickItemStudent clickItemStudent){
        this.clickItemStudent = clickItemStudent;
    }

    @NonNull
    @Override
    public AdapterStudentSearch.Viewhorder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_user_search, parent, false);
        AdapterStudentSearch.Viewhorder viewhorder = new AdapterStudentSearch.Viewhorder(view);
        return viewhorder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterStudentSearch.Viewhorder holder, int position) {
        final Student student = students.get(position);
        holder.tvUserName.setText(student.getStudentName());
        holder.gender.setText(student.getGender());
        holder.object.setText(Helper.STUDENT_TV);
        if(student.getStatus().equals("online"))
            holder.online.setVisibility(View.VISIBLE);
        else
            holder.online.setVisibility(View.GONE);
        Glide.with(context).load(student.getLinkAvatarStudent()).into(holder.imgAvatarUser);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
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

