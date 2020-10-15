package com.example.gias.User.Manager.Student;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.gias.User.Manager.AdapterItemNotice;
import com.example.gias.User.Manager.Teacher.DataConnectForTeacherPresenter;
import com.example.gias.User.Manager.Teacher.IHandlerAndLoadNotice;
import com.example.gias.databinding.FragmentDataConnectForStudentBinding;
import com.example.gias.databinding.FragmentDataConnectForTeacherBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataConnectForStudentFragment extends Fragment implements IHandlerAndLoadNotice {
    public final static String TAG = "DataConnectForStudentFragment";
    private FragmentDataConnectForStudentBinding binding;
    private DataConnect dataConnect;
    private DataConnectForTeacherPresenter presenter;
    private APIService apiService;

    public static DataConnectForStudentFragment newInstance(Bundle bundle) {
        DataConnectForStudentFragment fragment = new DataConnectForStudentFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_data_connect_for_student, container, false);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        Bundle data = getArguments();
        if(data != null){
            dataConnect = (DataConnect) data.getSerializable("DataConnect");
            presenter = new DataConnectForTeacherPresenter(getContext(), this, dataConnect);
            back();
            setView();
            conFirmUpdate();
            addOrDeviceNumSession();
            changeTuition();
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

    private void setView(){
        binding.edtTuition.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                binding.btnConfirmTuition.setVisibility(View.VISIBLE);
            }
        });

        binding.textAlltuition.setText(Helper.setUnderlineString("Học phí: "));
        binding.textComfirm.setText(Helper.setUnderlineString("Xác nhận đóng học phí"));

        binding.tvAllTuition.setText(dataConnect.getSalary());


        if(dataConnect.getUpdate().equals("Wait Student")){
            binding.tvNoticeConfirmUpdate.setText(Helper.setUnderlineString("Thông báo: Xác nhận cập nhật thông tin từ " + dataConnect.getTeacher().getTeacherName()));
            binding.tvNoticeConfirmUpdate.setVisibility(View.VISIBLE);
        }
        if(getContext() != null){
            Glide.with(getContext()).load(dataConnect.getStudent().getLinkAvatarStudent()).into(binding.imgFront);
            Glide.with(getContext()).load(dataConnect.getTeacher().getLinkAvatarTeacher()).into(binding.imgBehind);
        }
        binding.tvNameStudent.setText(dataConnect.getStudent().getStudentName());
        binding.tvPhoneStudent.setText("0" + dataConnect.getStudent().getPhoneNumber().substring(3));
        binding.tvNameTeacher.setText(dataConnect.getTeacher().getTeacherName());
        binding.tvPhoneTeacher.setText("0" + dataConnect.getTeacher().getPhoneNumber().substring(3));
        binding.tvSubject.setText(dataConnect.getSubject());
        binding.edtTuition.setText(dataConnect.getTuition());
        binding.tvNumSessions.setText(dataConnect.getNumOfSessions());
    }

    private void conFirmUpdate(){
        binding.tvNoticeConfirmUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Xác nhận cập nhật");
                builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataConnect.setUpdate("Updated");
                        updateDataConnect();

                        //create notice
                        Notice notice = new Notice("Xác nhận cập nhật", String.valueOf(System.currentTimeMillis()), dataConnect.getStudent().getStudentName() + " đã xác nhận cập nhật!");
                        createNotice(notice);

                        //create notification
                        Data data = new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(), Helper.STUDENT_TV + " " + dataConnect.getStudent().getStudentName() + " đã xác nhận môn học bạn cạp nhật", "Confirm Request",
                                dataConnect.getUidTeacher());
                        sendNotificationRequest(data);

                        binding.tvNoticeConfirmUpdate.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                });
                builder.setMessage("Xác nhận cập nhật từ Gia sư " + dataConnect.getTeacher().getTeacherName() + ", Nếu không đúng thông tin, hay liên hệ với gia sư qua phần tin nhắn!");
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    private void addOrDeviceNumSession(){
        binding.btnAddOneNumsessions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!dataConnect.getUpdate().equals("Updated")){
                    Toast.makeText(getContext(), "Môn học chưa được xác nhận cập nhật", Toast.LENGTH_SHORT).show();
                }
                else {
                    binding.tvNumSessions.setText(String.valueOf(Integer.parseInt(dataConnect.getNumOfSessions()) + 1));
                    dataConnect.setNumOfSessions(binding.tvNumSessions.getText().toString());
                    updateDataConnect();

                    //create notice
                    Notice notice = new Notice("Tăng buổi học", String.valueOf(System.currentTimeMillis()), dataConnect.getStudent().getStudentName() + " đã tăng lên một buổi học!");
                    createNotice(notice);

                    //push notification
                    Data data = new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(), Helper.STUDENT_TV + " " + dataConnect.getStudent().getStudentName() + " đã tăng lên một buổi học", "Tăng buổi học",
                            dataConnect.getUidTeacher());
                    sendNotificationRequest(data);
                }
            }
        });

        binding.btnDeviceNumSessions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!dataConnect.getUpdate().equals("Updated")){
                    Toast.makeText(getContext(), "Môn học chưa được xác nhận cập nhật", Toast.LENGTH_SHORT).show();
                }
                else {
                    binding.tvNumSessions.setText(String.valueOf(Integer.parseInt(dataConnect.getNumOfSessions()) - 1));
                    dataConnect.setNumOfSessions(binding.tvNumSessions.getText().toString());
                    updateDataConnect();

                    Notice notice = new Notice("Giảm buổi học", String.valueOf(System.currentTimeMillis()), dataConnect.getStudent().getStudentName() + " đã giảm đi một buổi học!");
                    createNotice(notice);

                    Data data = new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(), Helper.STUDENT_TV + " " + dataConnect.getStudent().getStudentName() + " đã giảm đi một buổi học", "Giảm buổi học",
                            dataConnect.getUidTeacher());
                    sendNotificationRequest(data);
                }
            }
        });
    }

    private void changeTuition(){
        binding.btnConfirmTuition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTuition = binding.edtTuition.getText().toString();
                if(!dataConnect.getUpdate().equals("Updated")){
                    Toast.makeText(getContext(), "Môn học chưa được xác nhận cập nhật", Toast.LENGTH_SHORT).show();
                }
                if(newTuition.equals(dataConnect.getTuition())){
                    Toast.makeText(getContext(), "Bạn chưa thực hiện thay đổi học phí!", Toast.LENGTH_SHORT).show();
                }
                else{
                    dataConnect.setTuition(newTuition);
                    updateDataConnect();

                    Notice notice = new Notice("Học Phí", String.valueOf(System.currentTimeMillis()), dataConnect.getStudent().getStudentName() + " đã thay đổi học phí thành " + newTuition);
                    createNotice(notice);

                    Data data = new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(), Helper.STUDENT_TV + " " + dataConnect.getStudent().getStudentName() + " đã thay đổi học phí", "Học phí",
                            dataConnect.getUidTeacher());
                    sendNotificationRequest(data);
                    Helper.hideKeyBoard(getActivity());
                }
            }
        });
    }

    private void updateDataConnect(){
        DatabaseReference mData = FirebaseDatabase.getInstance().getReference("DataConnect")
                .child(dataConnect.getUidTeacher() + dataConnect.getUidStudent());
        mData.setValue(dataConnect);
    }

    private void createNotice(Notice notice){
        DatabaseReference mNotice = FirebaseDatabase.getInstance().getReference("Notices")
                .child(dataConnect.getUidTeacher() + dataConnect.getUidStudent());
        mNotice.push().setValue(notice);
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

    @Override
    public void loadNotice(List<Notice> notices) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        binding.rvHistory.setLayoutManager(layoutManager);
        AdapterItemNotice adapterItemNotice = new AdapterItemNotice(getContext(), notices);
        binding.rvHistory.setAdapter(adapterItemNotice);
    }
}