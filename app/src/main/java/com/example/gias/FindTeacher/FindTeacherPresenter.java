package com.example.gias.FindTeacher;

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
import com.example.gias.Object.Teacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FindTeacherPresenter {
    private ILoadTeacher iLoadTeacher;
    private Context context;
    private List<Teacher> teachers = new ArrayList<>();
    private int setAvatar = 0;

    public FindTeacherPresenter(ILoadTeacher iLoadTeacher, Context context) {
        this.iLoadTeacher = iLoadTeacher;
        this.context = context;
        LoadTeacher();
    }

    private void LoadTeacher(){
        FirebaseDatabase.getInstance().getReference().child(Helper.TEACHER).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        Teacher teacher = dataSnapshot.getValue(Teacher.class);
                        if(FirebaseAuth.getInstance().getCurrentUser() != null &&
                                !teacher.getPhoneNumber().equals(
                                        FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))
                            teachers.add(teacher);
                        else if(FirebaseAuth.getInstance().getCurrentUser() == null)
                            teachers.add(teacher);
                    }
                    setAvatarUsers();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void setAvatarUsers(){
        for (final Teacher teacher: teachers) {
            Glide.with(context).asBitmap().load(teacher.getLinkAvatarTeacher()).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    teacher.setAvatarTeacher(Helper.getResizedBitmap(resource, 500));
                    setAvatar++;
                    if(setAvatar == teachers.size())
                        iLoadTeacher.loadSuccess(teachers);
                }
                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                }
            });
        }

    }

}
