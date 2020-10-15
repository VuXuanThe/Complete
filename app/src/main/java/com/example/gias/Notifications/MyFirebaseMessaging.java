package com.example.gias.Notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.gias.Helper;
import com.example.gias.User.UserActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    private DatabaseReference mData;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String sented = remoteMessage.getData().get("sented");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser != null && sented.equals(firebaseUser.getUid())){
            checkSendNotification(remoteMessage);
        }
    }

    private void checkSendNotification(RemoteMessage remoteMessage) {
        final String user = remoteMessage.getData().get("user");
        final String title = remoteMessage.getData().get("title");
        final String body = remoteMessage.getData().get("body");

        mData = FirebaseDatabase.getInstance().getReference();
        mData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(Helper.TEACHER).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    sendNotification(user, title, body, Helper.TEACHER);
                else sendNotification(user, title, body, Helper.STUDENT);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
    }

    private void sendNotification(String user, String title, String body, String object){
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra("OBJECT", object);
        if(title.equals("New Message"))
            intent.putExtra("INITFRAGMENTCHAT", "OK");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        OreoAndAboveNotification notification = new OreoAndAboveNotification(this);
        Notification.Builder builder = notification.getOnNotifications(title, body, pendingIntent, defaultSound);

        int i = 0;
        if(j > 0){
            i = j;
        }
        notification.getManager().notify(i, builder.build());
    }
 }
