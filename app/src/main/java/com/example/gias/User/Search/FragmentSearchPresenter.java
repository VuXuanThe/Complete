package com.example.gias.User.Search;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.gias.Object.Student;
import com.example.gias.Object.Teacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentSearchPresenter {
    private Context context;
    private ILoadNewStudentAndTeacher iLoadNewStudentAndTeacher;
    private DatabaseReference mData;

    public FragmentSearchPresenter(Context context,
                                   ILoadNewStudentAndTeacher iLoadNewStudentAndTeacher) {
        this.context = context;
        this.iLoadNewStudentAndTeacher = iLoadNewStudentAndTeacher;
        getNewStudents();
        getNewTeachers();
    }

    private void getNewStudents(){
        mData = FirebaseDatabase.getInstance().getReference("NewStudents");
        mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Student> newStudents = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    if(!dataSnapshot.getValue(Student.class).getPhoneNumber().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))
                        newStudents.add(dataSnapshot.getValue(Student.class));
                }
                iLoadNewStudentAndTeacher.loadNewStudents(newStudents);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
    }

    private void getNewTeachers(){
        mData = FirebaseDatabase.getInstance().getReference("NewTeachers");
        mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Teacher> newTeachers = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    if(!dataSnapshot.getValue(Teacher.class).getPhoneNumber().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))
                        newTeachers.add(dataSnapshot.getValue(Teacher.class));
                }
                iLoadNewStudentAndTeacher.loadNewTeachers(newTeachers);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
    }
}
