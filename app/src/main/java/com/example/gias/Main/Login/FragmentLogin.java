package com.example.gias.Main.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.gias.Helper;
import com.example.gias.R;
import com.example.gias.User.UserActivity;
import com.example.gias.databinding.FragmentLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class FragmentLogin extends Fragment {
    public static String TAG = "FragmentLogin";
    private FragmentLoginBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference mData;
    private String verificationId;
    private Boolean vertification = false;
    private int resentOTP = 0;

    public static FragmentLogin newInstance() {
        Bundle args = new Bundle();
        FragmentLogin fragment = new FragmentLogin();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();
        getOTP();
        resentOTP();
        setupOTPInputs();
        return binding.getRoot();
    }

    private void resentOTP(){
        binding.btnResentOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(resentOTP > 3)
                    Toast.makeText(getContext(), getResources().getString(R.string.More_Code_OTP), Toast.LENGTH_SHORT).show();
                else{
                    String phoneNumber = binding.edtPhoneNumber.getText().toString();
                    phoneNumber = Helper.VIETNAM + phoneNumber.substring(1);
                    requestOTP(phoneNumber);
                    Toast.makeText(getContext(), getResources().getString(R.string.Notice_More_Code_OTP), Toast.LENGTH_SHORT).show();
                    resentOTP++;
                }
            }
        });

    }

    private void setupOTPInputs(){
        binding.edtNumcode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty())
                    binding.edtNumcode2.requestFocus();
            }
            @Override
            public void afterTextChanged(Editable s) {}});
        binding.edtNumcode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty())
                    binding.edtNumcode3.requestFocus();
            }
            @Override
            public void afterTextChanged(Editable s) {}});
        binding.edtNumcode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty())
                    binding.edtNumcode4.requestFocus();
            }
            @Override
            public void afterTextChanged(Editable s) {}});
        binding.edtNumcode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty())
                    binding.edtNumcode5.requestFocus();
            }
            @Override
            public void afterTextChanged(Editable s) {}});
        binding.edtNumcode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty())
                    binding.edtNumcode6.requestFocus();
            }
            @Override
            public void afterTextChanged(Editable s) {}});

    }

    @Override
    public void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null)
            getFragment(FragmentAddDetailsUser.newInstance(), FragmentAddDetailsUser.TAG);
    }

    private void checkUserProfile(){
        binding.Load.setVisibility(View.GONE);
        mData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child(Helper.TEACHER).hasChild(mAuth.getCurrentUser().getUid()))
                    login(Helper.TEACHER);
                else if(snapshot.child(Helper.STUDENT).hasChild(mAuth.getCurrentUser().getUid()))
                    login(Helper.STUDENT);
                else{
                    Toast.makeText(getContext(), getResources().getString(R.string.Update_Infomation), Toast.LENGTH_SHORT).show();
                    getFragment(FragmentAddDetailsUser.newInstance(), FragmentAddDetailsUser.TAG);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void login(String object){
        Intent intent = new Intent(getContext(), UserActivity.class);
        intent.putExtra("OBJECT", object);
        startActivity(intent);
        getActivity().finish();
    }

    public void getFragment(Fragment fragment, String nameFragment) {
        try {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_fragment_right, R.anim.exit_fragment_right, R.anim.pop_enter_fragment, R.anim.pop_exit_fragment)
                    .replace(R.id.container, fragment)
                    .addToBackStack(nameFragment)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void verifyAuth(PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    checkUserProfile();
                }else{
                    binding.edtPhoneNumber.setEnabled(true);
                    if(task.getException().getMessage().equals("The sms verification code used to create the phone auth credential is invalid. Please resend the verification code sms and be sure use the verification code provided by the user."))
                        binding.tvNotice.setText(getResources().getString(R.string.Wrong_Code_OTP));
                    else
                        binding.tvNotice.setText(getResources().getString(R.string.Error));
                    binding.Load.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getOTP(){
        binding.btnOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.hideKeyBoard(getActivity());
                if(binding.edtPhoneNumber.getText().toString().isEmpty())
                    binding.edtPhoneNumber.setError(getResources().getString(R.string.Error_Dont_Enter_Phone_Number));
                else if(binding.edtPhoneNumber.getText().toString().length() != 10 || binding.edtPhoneNumber.getText().toString().charAt(0) != '0')
                    binding.edtPhoneNumber.setError(getResources().getString(R.string.Error_Phone_Number));
                else if(vertification == false){
                    String phoneNumber = binding.edtPhoneNumber.getText().toString();
                    phoneNumber = Helper.VIETNAM + phoneNumber.substring(1);
                    binding.edtPhoneNumber.setEnabled(false);
                    requestOTP(phoneNumber);
                }
                else{
                    String numcodeOTP1 = binding.edtNumcode1.getText().toString().trim();
                    String numcodeOTP2 = binding.edtNumcode2.getText().toString().trim();
                    String numcodeOTP3 = binding.edtNumcode3.getText().toString().trim();
                    String numcodeOTP4 = binding.edtNumcode4.getText().toString().trim();
                    String numcodeOTP5 = binding.edtNumcode5.getText().toString().trim();
                    String numcodeOTP6 = binding.edtNumcode6.getText().toString().trim();
                    if(numcodeOTP1.isEmpty() || numcodeOTP2.isEmpty() || numcodeOTP3.isEmpty() ||
                    numcodeOTP4.isEmpty() || numcodeOTP5.isEmpty() || numcodeOTP6.isEmpty()){
                        Toast.makeText(getContext(), getResources().getString(R.string.Error_Enter_OTP_Code), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        binding.Load.setVisibility(View.VISIBLE);
                        String userOTP = numcodeOTP1 + numcodeOTP2 + numcodeOTP3 +
                                numcodeOTP4 + numcodeOTP5 + numcodeOTP6;
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, userOTP);
                        verifyAuth(credential);
                    }

                }
            }
        });
    }

    private void requestOTP(String phoneNumber){
        binding.Load.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,
                60,
                TimeUnit.SECONDS,
                getActivity(),
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        binding.codeOTP.setVisibility(View.VISIBLE);
                        binding.codeOTP.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.enter_fragment_right));
                        binding.Load.setVisibility(View.GONE);
                        binding.btnOTP.setText(getResources().getString(R.string.Confirm));
                        verificationId = s;
                        vertification = true;
                    }

                    @Override
                    public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                        super.onCodeAutoRetrievalTimeOut(s);
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Toast.makeText(getContext(), getResources().getString(R.string.Auto_Confirm), Toast.LENGTH_SHORT).show();
                        verifyAuth(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
