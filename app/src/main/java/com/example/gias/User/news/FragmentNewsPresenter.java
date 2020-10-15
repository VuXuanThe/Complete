package com.example.gias.User.news;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.example.gias.Helper;
import com.example.gias.Object.Post;
import com.example.gias.Object.Student;
import com.example.gias.Object.Teacher;
import com.example.gias.User.UserActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FragmentNewsPresenter {
    private Context context;
    private ILoadDataNews iLoadDataNews;
    private DatabaseReference mData;

    public FragmentNewsPresenter(Context context, ILoadDataNews iLoadDataNews) {
        this.context = context;
        this.iLoadDataNews = iLoadDataNews;
        getData(UserActivity.OBJECT);
        getPost();
    }

    private void getData(final String OBJECT){
        mData = FirebaseDatabase.getInstance().getReference(OBJECT).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (OBJECT.equals(Helper.TEACHER)){
                    Teacher teacher = snapshot.getValue(Teacher.class);
                    iLoadDataNews.getDataTeacher(teacher);
                }
                else{
                    Student student = snapshot.getValue(Student.class);
                    iLoadDataNews.getDataStudent(student);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
    }

    private void getPost(){
        mData = FirebaseDatabase.getInstance().getReference("Posts");
        mData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List <Post> posts = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    posts.add(dataSnapshot.getValue(Post.class));
                }
                handlerSortPost(posts);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
    }

    private void handlerSortPost(List < Post > posts){
        Collections.sort(posts, new Comparator<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
                return o2.getTimePost().compareTo(o1.getTimePost());
            }
        });
        iLoadDataNews.getListNews(posts);
    }
}
