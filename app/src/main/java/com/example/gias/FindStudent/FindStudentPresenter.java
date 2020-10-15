package com.example.gias.FindStudent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.gias.Helper;
import com.example.gias.Object.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FindStudentPresenter {
    private ILoadStudent iLoadStudent;
    private Context context;
    private List<Student> students = new ArrayList<>();
    private int setAvatar = 0;

    public FindStudentPresenter(ILoadStudent iLoadStudent, Context context) {
        this.iLoadStudent = iLoadStudent;
        this.context = context;
        LoadStudent();
    }

    private void LoadStudent(){
        FirebaseDatabase.getInstance().getReference().child(Helper.STUDENT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        Student student = dataSnapshot.getValue(Student.class);
                        if(FirebaseAuth.getInstance().getCurrentUser() != null &&
                                !student.getPhoneNumber().equals(
                                        FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))
                        students.add(student);
                        else if(FirebaseAuth.getInstance().getCurrentUser() == null)
                            students.add(student);
                    }
                    setAvatarUsers();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void setAvatarUsers(){
        for (final Student student: students) {
            Glide.with(context).asBitmap().load(student.getLinkAvatarStudent()).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    student.setAvatarStudent(Helper.getResizedBitmap(resource, 500));
                    setAvatar++;
                    if(setAvatar == students.size())
                        iLoadStudent.loadSuccess(students);
                }
                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {}
            });
        }
    }
}
