package com.example.gias.User.Manager.Teacher;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.gias.Helper;
import com.example.gias.Notifications.APIService;
import com.example.gias.Notifications.Client;
import com.example.gias.Notifications.Data;
import com.example.gias.Notifications.MyResponse;
import com.example.gias.Notifications.Sender;
import com.example.gias.Notifications.Token;
import com.example.gias.Object.DataConnect;
import com.example.gias.Object.Notice;
import com.example.gias.Object.Student;
import com.example.gias.Object.Teacher;
import com.example.gias.User.UserActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagerForTeacherPresenter {
    private Context context;
    private ILoadDataManagerForTeacher iLoadDataManagerForTeacher;
    private DatabaseReference mData;
    private APIService apiService;

    public ManagerForTeacherPresenter(Context context,
                                      ILoadDataManagerForTeacher iLoadDataManagerForTeacher) {
        this.context = context;
        this.iLoadDataManagerForTeacher = iLoadDataManagerForTeacher;
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        getDataTeacher();
        getDataConnect();
        checkRequestFromStudent();
    }

    private void getDataTeacher(){
        mData = FirebaseDatabase.getInstance().getReference(UserActivity.OBJECT).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Teacher teacher = snapshot.getValue(Teacher.class);
                iLoadDataManagerForTeacher.getDataTeacher(teacher);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
    }

    private void getDataConnect(){
        DatabaseReference mData = FirebaseDatabase.getInstance().getReference("DataConnect");
        final List <DataConnect> dataConnects = new ArrayList<>();
        mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataConnects.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    DataConnect dataConnect = dataSnapshot.getValue(DataConnect.class);
                    Teacher teacher = dataSnapshot.child("teacher").getValue(Teacher.class);
                    if(teacher.getPhoneNumber().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())){
                        Student student = dataSnapshot.child("student").getValue(Student.class);
                        dataConnect.setTeacher(teacher);
                        dataConnect.setStudent(student);
                        dataConnects.add(dataConnect);
                    }

                }
                iLoadDataManagerForTeacher.getDataConnect(dataConnects);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
    }

    private void checkRequestFromStudent(){
        DatabaseReference mData = FirebaseDatabase.getInstance().getReference(Helper.TEACHER)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mData.child("requestFromStudent").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    HashMap<String, String> hashMap = null;
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                         hashMap = (HashMap<String, String>) dataSnapshot.getValue();
                    }
                    iLoadDataManagerForTeacher.getRequestFromStudent(hashMap);
                }
                else
                    iLoadDataManagerForTeacher.getRequestFromStudent(null);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
    }

    public void createDataConnect(final String uidTeacher, final String uidStudent){
        DatabaseReference mDataStudent = FirebaseDatabase.getInstance().getReference(Helper.STUDENT).child(uidStudent);
        mDataStudent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //getStudent
                final Student student = snapshot.getValue(Student.class);

                //getTeacher
                DatabaseReference mDataTeacher = FirebaseDatabase.getInstance().getReference(Helper.TEACHER).child(uidTeacher);
                mDataTeacher.child("requestFromStudent").child(uidStudent).removeValue();
                mDataTeacher.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Teacher teacher = snapshot.getValue(Teacher.class);
                        DatabaseReference mDataConnect = FirebaseDatabase.getInstance().getReference("DataConnect").child(uidTeacher + uidStudent);
                        DataConnect dataConnect = new DataConnect(student, teacher, uidStudent, uidTeacher, "connected", "no", null, null, null, null, "no");
                        mDataConnect.setValue(dataConnect);

                        //Push notification
                        Data data = new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(), Helper.TEACHER_TV + " " + teacher.getTeacherName() + " đã xác nhận kết nối với bạn", "Confirm Request",
                                uidStudent);
                        sendNotificationRequest(data);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}});}
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
    }

    public void checkExistsConnectUser(final String uidTeacher,
                                       final String uidStudent,
                                       final String nameSender){
        DatabaseReference mData = FirebaseDatabase.getInstance().getReference("DataConnect");
        mData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChild(uidTeacher + uidStudent)){
                    createRequest(uidStudent, uidTeacher, nameSender);
                    Data data = new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(), Helper.TEACHER_TV + " " + nameSender + " đã gửi cho bạn một yêu cầu kết nối", "New Request",
                            uidStudent);
                    sendNotificationRequest(data);
                    iLoadDataManagerForTeacher.sendnotificationRequest("Gửi yêu cầu thành công!");
                }
                else if(snapshot.hasChild(uidTeacher + uidStudent)){
                    iLoadDataManagerForTeacher.sendnotificationRequest("Bạn đã liên kết với tài khoản này rồi");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
    }

    private void sendNotificationRequest(final Data data){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(data.getSented());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Token token = dataSnapshot.getValue(Token.class);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {}
                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {}});
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
    }

    private void createRequest(String uidStudent, String uidTeacher, String nameSender){
        DatabaseReference mData = FirebaseDatabase.getInstance().getReference(Helper.STUDENT).child(uidStudent).child("requestFromTeacher");
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uidTeacher", uidTeacher);
        hashMap.put("nameSender", nameSender);
        mData.child(uidTeacher).setValue(hashMap);
    }
}
