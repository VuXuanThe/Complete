package com.example.gias.User;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.gias.User.Chats.Fragment.ChatsFragment;
import com.example.gias.Notifications.Token;
import com.example.gias.Helper;
import com.example.gias.R;
import com.example.gias.User.Manager.Student.ManagerForStudentFragment;
import com.example.gias.User.Manager.Teacher.ManagerForTeacherFragment;
import com.example.gias.User.Search.FragmentSearch;
import com.example.gias.User.news.NewsFragment;
import com.example.gias.User.profile.FragmentUser;
import com.example.gias.databinding.ActivityUserBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import pub.devrel.easypermissions.EasyPermissions;

public class UserActivity extends AppCompatActivity {
    public static final String TAG = "UserAcivity";
    private ActivityUserBinding binding;
    public static String OBJECT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user);
        if (!checkRequiredPermissions()) checkRequiredPermissions();
        Intent intent       = getIntent();
        OBJECT              = intent.getStringExtra("OBJECT");

        if(OBJECT.equals(Helper.TEACHER))
            getFragment(ManagerForTeacherFragment.newInstance());
        else
            getFragment(ManagerForStudentFragment.newInstance());

        binding.btnEvaluate.setEnabled(false);
        if(intent.hasExtra("INITFRAGMENTCHAT")){
            binding.btnInformationUser.setEnabled(true);
            binding.btnMes.setEnabled(false);
            binding.btnGroup.setEnabled(true);
            binding.btnSearch.setEnabled(true);
            binding.btnEvaluate.setEnabled(true);
            binding.btnInformationUser.setImageDrawable(getResources().getDrawable(R.drawable.ic_defaut_person));
            binding.btnMes.setImageDrawable(getResources().getDrawable(R.drawable.ic_messenger));
            binding.btnSearch.setImageDrawable(getResources().getDrawable(R.drawable.ic_defaut_search));
            binding.btnGroup.setImageDrawable(getResources().getDrawable(R.drawable.ic_defaut_group));
            getFragmentRight(ChatsFragment.newInstance());
        }

        binding.btnInformationUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnInformationUser.setEnabled(false);
                binding.btnMes.setEnabled(true);
                binding.btnGroup.setEnabled(true);
                binding.btnSearch.setEnabled(true);
                binding.btnEvaluate.setEnabled(true);
                binding.btnInformationUser.setImageDrawable(getResources().getDrawable(R.drawable.ic_person));
                binding.btnMes.setImageDrawable(getResources().getDrawable(R.drawable.ic_defaut_messenger));
                binding.btnSearch.setImageDrawable(getResources().getDrawable(R.drawable.ic_defaut_search));
                binding.btnGroup.setImageDrawable(getResources().getDrawable(R.drawable.ic_defaut_group));
                getFragmentRight(FragmentUser.newInstance(null));
            }
        });

        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnInformationUser.setEnabled(true);
                binding.btnMes.setEnabled(true);
                binding.btnGroup.setEnabled(true);
                binding.btnSearch.setEnabled(false);
                binding.btnEvaluate.setEnabled(true);
                binding.btnInformationUser.setImageDrawable(getResources().getDrawable(R.drawable.ic_defaut_person));
                binding.btnMes.setImageDrawable(getResources().getDrawable(R.drawable.ic_defaut_messenger));
                binding.btnSearch.setImageDrawable(getResources().getDrawable(R.drawable.ic_search_bottom_app_bar));
                binding.btnGroup.setImageDrawable(getResources().getDrawable(R.drawable.ic_defaut_group));
                Bundle data = new Bundle();
                data.putString("PositionAddFragmentUser", TAG);
                getFragmentLeft(FragmentSearch.newInstance(data));
            }
        });


        binding.btnMes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnInformationUser.setEnabled(true);
                binding.btnMes.setEnabled(false);
                binding.btnGroup.setEnabled(true);
                binding.btnSearch.setEnabled(true);
                binding.btnEvaluate.setEnabled(true);
                binding.btnInformationUser.setImageDrawable(getResources().getDrawable(R.drawable.ic_defaut_person));
                binding.btnMes.setImageDrawable(getResources().getDrawable(R.drawable.ic_messenger));
                binding.btnSearch.setImageDrawable(getResources().getDrawable(R.drawable.ic_defaut_search));
                binding.btnGroup.setImageDrawable(getResources().getDrawable(R.drawable.ic_defaut_group));
                getFragmentRight(ChatsFragment.newInstance());
            }
        });

        binding.btnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnInformationUser.setEnabled(true);
                binding.btnMes.setEnabled(true);
                binding.btnGroup.setEnabled(false);
                binding.btnSearch.setEnabled(true);
                binding.btnEvaluate.setEnabled(true);
                binding.btnInformationUser.setImageDrawable(getResources().getDrawable(R.drawable.ic_defaut_person));
                binding.btnMes.setImageDrawable(getResources().getDrawable(R.drawable.ic_defaut_messenger));
                binding.btnSearch.setImageDrawable(getResources().getDrawable(R.drawable.ic_defaut_search));
                binding.btnGroup.setImageDrawable(getResources().getDrawable(R.drawable.ic_group));
                getFragmentLeft(NewsFragment.newInstance());
            }
        });

        binding.btnEvaluate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnInformationUser.setEnabled(true);
                binding.btnMes.setEnabled(true);
                binding.btnGroup.setEnabled(true);
                binding.btnSearch.setEnabled(true);
                binding.btnEvaluate.setEnabled(false);
                binding.btnInformationUser.setImageDrawable(getResources().getDrawable(R.drawable.ic_defaut_person));
                binding.btnMes.setImageDrawable(getResources().getDrawable(R.drawable.ic_defaut_messenger));
                binding.btnSearch.setImageDrawable(getResources().getDrawable(R.drawable.ic_defaut_search));
                binding.btnGroup.setImageDrawable(getResources().getDrawable(R.drawable.ic_defaut_group));
                if(OBJECT.equals(Helper.TEACHER))
                    getFragment(ManagerForTeacherFragment.newInstance());
                else
                    getFragment(ManagerForStudentFragment.newInstance());
            }
        });

    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token1);
    }

    public void getFragmentLeft(Fragment fragment) {
        try {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_fragment_left, R.anim.exit_fragment_left)
                    .replace(R.id.containerUser, fragment)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getFragment(Fragment fragment) {
        try {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.containerUser, fragment)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getFragmentRight(Fragment fragment) {
        try {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_fragment_right, R.anim.exit_fragment_right)
                    .replace(R.id.containerUser, fragment)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    private boolean checkRequiredPermissions() {
        String[] perms = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.message_request_permission_read_phone_state),
                    20000, perms);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}