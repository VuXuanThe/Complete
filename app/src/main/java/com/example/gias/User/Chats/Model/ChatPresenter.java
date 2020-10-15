package com.example.gias.User.Chats.Model;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.gias.User.Chats.Interface.IGetDataChatFragment;
import com.example.gias.Helper;
import com.example.gias.Object.Student;
import com.example.gias.Object.Teacher;
import com.example.gias.Object.UserChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatPresenter {
    private Context context;
    private IGetDataChatFragment iGetDataChatFragment;
    private List <Student> studentsSearch = new ArrayList<>();
    private List <Teacher> teachersSearch = new ArrayList<>();
    private DatabaseReference mData;

    private List <UserChat> userChats;

    public ChatPresenter(Context context, IGetDataChatFragment iGetDataChatFragment) {
        this.context = context;
        this.iGetDataChatFragment = iGetDataChatFragment;
        getMessage();
    }

    public void getData(final String OBJECT){
        mData = FirebaseDatabase.getInstance().getReference(OBJECT).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (OBJECT.equals(Helper.TEACHER)){
                    Teacher teacher = snapshot.getValue(Teacher.class);
                    iGetDataChatFragment.getDataTeacher(teacher);
                }
                else{
                    Student student = snapshot.getValue(Student.class);
                    iGetDataChatFragment.getDataStudent(student);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
    }

    public void getMessage(){
        userChats = new ArrayList<>();
        mData = FirebaseDatabase.getInstance().getReference("UserChat");
        mData.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userChats.clear();
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        UserChat userChat = dataSnapshot.getValue(UserChat.class);
                        userChats.add(userChat);
                    }
                    iGetDataChatFragment.LoadUserChat(handlerSortUserChat(userChats));
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
    }

    private List <UserChat> handlerSortUserChat(List<UserChat> userChats){
        List<UserChat> sortUserChat = new ArrayList<>();
        for(int i = 1; i <= userChats.size(); i++){
            for (UserChat userChat: userChats ) {
                if(Integer.parseInt(userChat.getIndex()) == i)
                    sortUserChat.add(userChat);
            }
        }
        return sortUserChat;
    }

    public void searchName(final String s){
        Query query = FirebaseDatabase.getInstance().getReference(Helper.STUDENT).orderByChild("studentName")
                .startAt(s)
                .endAt(s + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentsSearch.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    if (!dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        Student student = dataSnapshot.getValue(Student.class);
                        studentsSearch.add(student);
                    }
                }
                Query query1 = FirebaseDatabase.getInstance().getReference(Helper.TEACHER).orderByChild("teacherName")
                        .startAt(s)
                        .endAt(s + "\uf8ff");
                query1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        teachersSearch.clear();
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                            if (!dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                Teacher teacher = dataSnapshot.getValue(Teacher.class);
                                teachersSearch.add(teacher);
                            }
                        }
                        iGetDataChatFragment.searchUser(studentsSearch, teachersSearch);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}});
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});

    }
}
