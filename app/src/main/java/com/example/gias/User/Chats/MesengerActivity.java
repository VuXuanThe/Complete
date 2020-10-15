package com.example.gias.User.Chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gias.User.Chats.Adapter.MessageAdapter;
import com.example.gias.Notifications.APIService;
import com.example.gias.Notifications.Client;
import com.example.gias.Notifications.Data;
import com.example.gias.Notifications.MyResponse;
import com.example.gias.Notifications.Sender;
import com.example.gias.Notifications.Token;
import com.example.gias.Helper;
import com.example.gias.Object.Chat;
import com.example.gias.Object.Student;
import com.example.gias.Object.Teacher;
import com.example.gias.Object.UserChat;
import com.example.gias.R;
import com.example.gias.User.UserActivity;
import com.example.gias.databinding.ActivityMesengerBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MesengerActivity extends AppCompatActivity {
    public static final String TAG = "MesengerActivity";
    private ActivityMesengerBinding binding;
    private UserChat userChatReceiver;
    private UserChat userChatSender;
    private List<Chat> mChats;
    private DatabaseReference mData;
    private APIService apiService;
    private boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mesenger);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        Intent data = getIntent();
        if(data != null){
            userChatReceiver = (UserChat) data.getSerializableExtra("UserChatReceiver");
            userChatSender = (UserChat) data.getSerializableExtra("UserChatSender");
        }
        setView();
    }
    private void setView(){
        Glide.with(this).load(userChatReceiver.getLinkAvatar()).into(binding.imgAvatarUserReceiver);
        binding.tvUserReceiverName.setText(userChatReceiver.getUserName());

        mData = FirebaseDatabase.getInstance().getReference(userChatReceiver.getObject());
        mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(userChatReceiver.getObject().equals(Helper.STUDENT)){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Student student = dataSnapshot.getValue(Student.class);
                        if(student.getPhoneNumber().equals(userChatReceiver.getPhoneNumber())){
                            if(student.getStatus().equals("online"))
                                binding.online.setVisibility(View.VISIBLE);
                            else
                                binding.online.setVisibility(View.GONE);
                        }

                    }
                }
                else {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Teacher teacher = dataSnapshot.getValue(Teacher.class);
                        if(teacher.getPhoneNumber().equals(userChatReceiver.getPhoneNumber())){
                            if(teacher.getStatus().equals("online"))
                                binding.online.setVisibility(View.VISIBLE);
                            else
                                binding.online.setVisibility(View.GONE);
                        }

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
        binding.rvListMessage.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        binding.rvListMessage.setLayoutManager(linearLayoutManager);

        readMessage();

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.textSend.getText().toString();
                if(!msg.isEmpty()){
                    notify = true;
                    sendMessage(msg);
                }
                else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.Enter_Message), Toast.LENGTH_SHORT).show();
                }
                binding.textSend.setText("");
            }
        });
    }

    private void readMessage(){
        mChats = new ArrayList<>();
        mData = FirebaseDatabase.getInstance().getReference("Chats");
        mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChats.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()) && chat.getSender().equals((userChatReceiver.getPhoneNumber())) ||
                            chat.getReceiver().equals(userChatReceiver.getPhoneNumber()) && chat.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) mChats.add(chat);
                }
                MessageAdapter messageAdapter = new MessageAdapter(getBaseContext(), mChats, userChatReceiver.getLinkAvatar());
                binding.rvListMessage.setAdapter(messageAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
    }

    private void sendMessage(final String message){
        mData = FirebaseDatabase.getInstance().getReference();
        Chat chat = new Chat(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), userChatReceiver.getPhoneNumber(), message);
        mData.child("Chats").push().setValue(chat);

        //update new item chat in list of sender
        mData = FirebaseDatabase.getInstance().getReference();
        mData.child("UserChat").child(userChatSender.getPhoneNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<UserChat> userChats = new ArrayList<>();
                int index = 0;
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    UserChat userChat = dataSnapshot.getValue(UserChat.class);
                    userChats.add(userChat);
                    if(userChat.getPhoneNumber().equals(userChatReceiver.getPhoneNumber()))
                        index = Integer.parseInt(userChat.getIndex());
                }
                for (UserChat userChat: userChats) {
                    if(index == 0){
                        userChat.setIndex(String.valueOf((Integer.parseInt(userChat.getIndex()) + 1)));
                        DatabaseReference updateIndex = FirebaseDatabase.getInstance().getReference();
                        updateIndex.child("UserChat").child(userChatSender.getPhoneNumber())
                                .child(userChat.getPhoneNumber()).setValue(userChat);
                    }
                    else if(Integer.parseInt(userChat.getIndex()) < index){
                        userChat.setIndex(String.valueOf((Integer.parseInt(userChat.getIndex()) + 1)));
                        DatabaseReference updateIndex = FirebaseDatabase.getInstance().getReference();
                        updateIndex.child("UserChat").child(userChatSender.getPhoneNumber())
                                .child(userChat.getPhoneNumber()).setValue(userChat);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});


        //update new item chat in list of receiver
        mData = FirebaseDatabase.getInstance().getReference();
        mData.child("UserChat").child(userChatReceiver.getPhoneNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<UserChat> userChats = new ArrayList<>();
                int index = 0;
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    UserChat userChat = dataSnapshot.getValue(UserChat.class);
                    userChats.add(userChat);
                    if(userChat.getPhoneNumber().equals(userChatSender.getPhoneNumber()))
                        index = Integer.parseInt(userChat.getIndex());
                }
                for (UserChat userChat: userChats) {
                    if(index == 0){
                        userChat.setIndex(String.valueOf((Integer.parseInt(userChat.getIndex()) + 1)));
                        DatabaseReference updateIndex = FirebaseDatabase.getInstance().getReference();
                        updateIndex.child("UserChat").child(userChatReceiver.getPhoneNumber())
                                .child(userChat.getPhoneNumber()).setValue(userChat);
                    }
                    else if(Integer.parseInt(userChat.getIndex()) < index){
                        userChat.setIndex(String.valueOf((Integer.parseInt(userChat.getIndex()) + 1)));
                        DatabaseReference updateIndex = FirebaseDatabase.getInstance().getReference();
                        updateIndex.child("UserChat").child(userChatReceiver.getPhoneNumber())
                                .child(userChat.getPhoneNumber()).setValue(userChat);
                    }
                }
                userChatReceiver.setMes(message);
                userChatSender.setMes(message);
                userChatReceiver.setIndex("1");
                userChatSender.setIndex("1");

                mData = FirebaseDatabase.getInstance().getReference();
                mData.child("UserChat").child(userChatSender.getPhoneNumber()).child(userChatReceiver.getPhoneNumber()).setValue(userChatReceiver);
                mData.child("UserChat").child(userChatReceiver.getPhoneNumber()).child(userChatSender.getPhoneNumber()).setValue(userChatSender);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});

        //getUid of receiver of send notification when online
        if(userChatReceiver.getObject().equals(Helper.STUDENT)){
            mData = FirebaseDatabase.getInstance().getReference(userChatReceiver.getObject());
            mData.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Student student = dataSnapshot.getValue(Student.class);
                        if(student.getPhoneNumber().equals(userChatReceiver.getPhoneNumber())){
                            String idReceiver = dataSnapshot.getKey();
                            if(notify && student.getStatus().equals("offline")){
                                sendNotification(idReceiver, userChatSender.getUserName(), message);
                            }
                            notify = false;
                            break;
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}});
        }
        else {
            mData = FirebaseDatabase.getInstance().getReference(userChatReceiver.getObject());
            mData.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Teacher teacher = dataSnapshot.getValue(Teacher.class);
                        if(teacher.getPhoneNumber().equals(userChatReceiver.getPhoneNumber())){
                            String idReceiver = dataSnapshot.getKey();
                            if(notify && teacher.getStatus().equals("offline")){
                                sendNotification(idReceiver, userChatSender.getUserName(), message);
                            }
                            notify = false;
                            break;
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}});
        }
    }

    private void sendNotification(final String receiver, final String senderName, final String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Token token = dataSnapshot.getValue(Token.class);
                    Data data = new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(), senderName + ": " + message, "New Message",
                            receiver);

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

    private void status(String status){
        mData = FirebaseDatabase.getInstance().getReference(UserActivity.OBJECT)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        HashMap hashMap = new HashMap();
        hashMap.put("status", status);
        mData.updateChildren(hashMap);
    }

    @Override
    protected void onPause() {
        super.onPause();
            status("offline");
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }
}