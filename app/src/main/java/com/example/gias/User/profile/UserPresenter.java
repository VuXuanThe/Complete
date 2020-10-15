package com.example.gias.User.profile;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.gias.Helper;
import com.example.gias.Object.Post;
import com.example.gias.Object.Student;
import com.example.gias.Object.Teacher;
import com.example.gias.Object.UserChat;
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

public class UserPresenter {
    private Context context;
    private IProfileUser iProfileUser;
    private DatabaseReference mData;

    public UserPresenter(Context context, IProfileUser iProfileUser) {
        this.context = context;
        this.iProfileUser = iProfileUser;
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            getUserChatSender();
        }
    }

    public void getData(final String OBJECT){
        mData = FirebaseDatabase.getInstance().getReference(OBJECT).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (OBJECT.equals(Helper.TEACHER)){
                    Teacher teacher = snapshot.getValue(Teacher.class);
                    iProfileUser.getDataTeacher(teacher);
                }
                else{
                    Student student = snapshot.getValue(Student.class);
                    iProfileUser.getDataStudent(student);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
    }

    public void getPost(final String numberPhone){
        mData = FirebaseDatabase.getInstance().getReference("Posts");
        mData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Post> posts = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if(post.getNumberPhonePoster().equals(numberPhone))
                        posts.add(post);
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
        iProfileUser.getPostsUser(posts);
    }

    private void getUserChatSender(){
        DatabaseReference mData = FirebaseDatabase.getInstance().getReference(UserActivity.OBJECT);
        mData.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(UserActivity.OBJECT.equals(Helper.TEACHER)){
                    Teacher teacher = snapshot.getValue(Teacher.class);
                    UserChat userChatSender = new UserChat(teacher.getPhoneNumber(), teacher.getTeacherName(), teacher.getLinkAvatarTeacher(),
                            null, teacher.getObject(), null);
                    iProfileUser.getUserChatSender(userChatSender);
                }
                else {
                    Student student = snapshot.getValue(Student.class);
                    UserChat userChatSender = new UserChat(student.getPhoneNumber(), student.getStudentName(), student.getLinkAvatarStudent(),
                            null, student.getObject(), null);
                    iProfileUser.getUserChatSender(userChatSender );
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
    }
}
