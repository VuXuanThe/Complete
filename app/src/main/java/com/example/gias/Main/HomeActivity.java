package com.example.gias.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.gias.Helper;
import com.example.gias.R;
import com.example.gias.User.UserActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 1000;
    private FirebaseAuth mAuth;
    private DatabaseReference mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if(!isConnect(this)){
            showCustomDialog();
        }
        else {
            mAuth = FirebaseAuth.getInstance();
            mData = FirebaseDatabase.getInstance().getReference();
            splash();
        }

    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle(getResources().getString(R.string.title))
                .setMessage(getResources().getString(R.string.mess))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.button_one), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.button_two), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(HomeActivity.this, getResources().getString(R.string.mess_two), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
        builder.show();
    }

    private void splash(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_logo);
                findViewById(R.id.imgLogo).startAnimation(animation);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mAuth.getCurrentUser() != null)
                            checkUserProfile();
                        else{
                            startActivity(new Intent(getBaseContext(), MainActivity.class));
                            finish();
                        }
                    }
                }, SPLASH_TIME_OUT);
            }
        }, SPLASH_TIME_OUT);
    }

    private void checkUserProfile(){
        mData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child(Helper.TEACHER).hasChild(mAuth.getCurrentUser().getUid()))
                    autoLogin(Helper.TEACHER);
                else if(snapshot.child(Helper.STUDENT).hasChild(mAuth.getCurrentUser().getUid()))
                    autoLogin(Helper.STUDENT);
                else{
                    startActivity(new Intent(getBaseContext(), MainActivity.class));
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void autoLogin(String object){
        Intent intent = new Intent(getBaseContext(), UserActivity.class);
        Toast.makeText(HomeActivity.this, getResources().getString(R.string.Auto_Login), Toast.LENGTH_SHORT).show();
        intent.putExtra("OBJECT", object);
        startActivity(intent);
        finish();
    }

    private boolean isConnect(HomeActivity homeActivity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) homeActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo infoWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo infoMobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if(infoWifi != null && infoWifi.isConnected() ||
            infoMobile != null && infoMobile.isConnected())
            return true;
        return false;
    }


}