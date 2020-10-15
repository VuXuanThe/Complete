package com.example.gias.User.Manager.Teacher;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gias.Helper;
import com.example.gias.Notifications.APIService;
import com.example.gias.Notifications.Client;
import com.example.gias.Notifications.Data;
import com.example.gias.Notifications.MyResponse;
import com.example.gias.Notifications.Sender;
import com.example.gias.Notifications.Token;
import com.example.gias.Object.DataConnect;
import com.example.gias.Object.Notice;
import com.example.gias.R;
import com.example.gias.databinding.FragmentUpdateDataConectForTeacherBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateDataConectForTeacherFragment extends Fragment {
    public static String TAG = "UpdateDataConectForTeacherFragment";
    private FragmentUpdateDataConectForTeacherBinding binding;
    private DataConnect dataConnect;
    private APIService apiService;

    public static UpdateDataConectForTeacherFragment newInstance(Bundle data) {
        UpdateDataConectForTeacherFragment fragment = new UpdateDataConectForTeacherFragment();
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_update_data_conect_for_teacher, container, false);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        Bundle bundle = getArguments();
        if(bundle != null){
            dataConnect = (DataConnect) bundle.getSerializable("DataConnect");
            back();
            setView();
            checkValue();
        }
        return binding.getRoot();
    }

    private void back(){
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
    }

    private void checkValue() {
        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.hideKeyBoard(getActivity());
                String subject = binding.edtSubject.getText().toString();
                String tuition = binding.edtTuition.getText().toString();
                String  numSession = binding.edtNumSession.getText().toString();
                String salary = String.valueOf(Integer.parseInt(tuition) * Integer.parseInt(numSession));

                if(subject == null || tuition == null || numSession == null){
                    Toast.makeText(getContext(), "Bạn chưa nhập đủ các giá trị!", Toast.LENGTH_SHORT).show();
                }
                else{
                    //set again dataconnect
                    dataConnect.setUpdate("Wait Student");
                    dataConnect.setSubject(subject);
                    dataConnect.setTuition(tuition);
                    dataConnect.setNumOfSessions(numSession);
                    dataConnect.setSalary(salary);

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Xác nhận cập nhật");
                    builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DatabaseReference mData = FirebaseDatabase.getInstance().getReference("DataConnect");
                            mData.child(dataConnect.getUidTeacher() + dataConnect.getUidStudent()).setValue(dataConnect);

                            Data data = new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(),Helper.TEACHER_TV + " " + dataConnect.getTeacher().getTeacherName() + " đã cập nhật thông tin lớp học" , "Confirm Request",
                                    dataConnect.getUidStudent());
                            sendNotificationRequest(data);

                            //create notice
                            Notice notice = new Notice("Cập Nhật", String.valueOf(System.currentTimeMillis()), dataConnect.getTeacher().getTeacherName() + " đã cập nhật dữ liệu môn học");
                            DatabaseReference mNotice = FirebaseDatabase.getInstance().getReference("Notices").child(dataConnect.getUidTeacher() + dataConnect.getUidStudent());
                            mNotice.push().setValue(notice);

                            dialog.dismiss();
                            getFragmentManager().popBackStack();
                        }
                    });
                    builder.setMessage("Hệ thống sẽ gửi thông báo xác nhận đến " + Helper.STUDENT_TV + " " + dataConnect.getStudent().getStudentName());
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });
    }

    private void setView(){
       if(getContext() != null){
           Glide.with(getContext()).load(dataConnect.getStudent().getLinkAvatarStudent()).into(binding.imgBehind);
           Glide.with(getContext()).load(dataConnect.getTeacher().getLinkAvatarTeacher()).into(binding.imgFront);
       }
       binding.tvNameStudent.setText(dataConnect.getStudent().getStudentName());
       binding.tvPhoneStudent.setText("0" + dataConnect.getStudent().getPhoneNumber().substring(3));
       binding.tvNameTeacher.setText(dataConnect.getTeacher().getTeacherName());
       binding.tvPhoneTeacher.setText("0" + dataConnect.getTeacher().getPhoneNumber().substring(3));
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
}