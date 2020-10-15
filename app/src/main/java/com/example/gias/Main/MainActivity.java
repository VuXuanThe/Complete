package com.example.gias.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import com.example.gias.FindStudent.FragmentFindStudent;
import com.example.gias.FindTeacher.FragmentFindTeacher;
import com.example.gias.Main.Login.FragmentLogin;
import com.example.gias.R;
import com.example.gias.databinding.ActivityMainBinding;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        getFragmentManager().popBackStack();
        init();
    }

    private void init(){
        Login();
        FindTeacher();
        FindStudent();
    }

    private void Login(){
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkRequiredPermissions()) checkRequiredPermissions();
                else getFragment(FragmentLogin.newInstance(), FragmentLogin.TAG);

            }
        });
    }

    private void FindTeacher(){
        binding.viewFindTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkRequiredPermissions()) checkRequiredPermissions();
                else{
                    Bundle args = new Bundle();
                    args.putString("PositionAddFragmentUser", TAG);
                    getFragment(FragmentFindTeacher.newInstance(args), FragmentFindTeacher.TAG);
                }

            }
        });
    }

    private void FindStudent(){
        binding.viewFindStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkRequiredPermissions()) checkRequiredPermissions();
                else{
                    Bundle args = new Bundle();
                    args.putString("PositionAddFragmentUser", TAG);
                    getFragment(FragmentFindStudent.newInstance(args), FragmentFindStudent.TAG);
                }

            }
        });
    }

    private void getFragment(Fragment fragment, String nameFragment) {
        try {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_fragment_right, R.anim.exit_fragment_right, R.anim.pop_enter_fragment, R.anim.pop_exit_fragment)
                    .replace(R.id.container, fragment)
                    .addToBackStack(nameFragment)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
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