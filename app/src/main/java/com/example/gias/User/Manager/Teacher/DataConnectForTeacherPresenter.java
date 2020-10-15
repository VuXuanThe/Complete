package com.example.gias.User.Manager.Teacher;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.example.gias.Object.DataConnect;
import com.example.gias.Object.Notice;
import com.example.gias.Object.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DataConnectForTeacherPresenter {
    private Context context;
    private IHandlerAndLoadNotice iHandlerAndLoadNotice;
    private DataConnect dataConnect;

    public DataConnectForTeacherPresenter(Context context, IHandlerAndLoadNotice iHandlerAndLoadNotice, DataConnect dataConnect) {
        this.context = context;
        this.iHandlerAndLoadNotice = iHandlerAndLoadNotice;
        this.dataConnect = dataConnect;
        loadNotice(dataConnect.getUidTeacher() + dataConnect.getUidStudent());
    }

    private void loadNotice(String uid){
        DatabaseReference mData = FirebaseDatabase.getInstance().getReference("Notices")
                .child(uid);
        mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Notice> notices = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    notices.add(dataSnapshot.getValue(Notice.class));
                }
                handlerSortListNotice(notices);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
    }

    private void handlerSortListNotice(List < Notice > notices){
        Collections.sort(notices, new Comparator<Notice>() {
            @Override
            public int compare(Notice o1, Notice o2) {
                return o2.getTime().compareTo(o1.getTime());
            }
        });
        iHandlerAndLoadNotice.loadNotice(notices);
    }
}
