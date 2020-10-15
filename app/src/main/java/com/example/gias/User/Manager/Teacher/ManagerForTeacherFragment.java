package com.example.gias.User.Manager.Teacher;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gias.Helper;
import com.example.gias.Notifications.APIService;
import com.example.gias.Notifications.Client;
import com.example.gias.Object.DataConnect;
import com.example.gias.Object.Student;
import com.example.gias.Object.Teacher;
import com.example.gias.R;
import com.example.gias.User.Manager.DataConnect.AdapterDataConnect;
import com.example.gias.User.Manager.DataConnect.IClickItemDataConnect;
import com.example.gias.databinding.FragmentManagerForTeacherBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class ManagerForTeacherFragment extends Fragment implements ILoadDataManagerForTeacher {
    private FragmentManagerForTeacherBinding binding;
    private ManagerForTeacherPresenter presenter;
    private APIService apiService;

    public static ManagerForTeacherFragment newInstance() {
        ManagerForTeacherFragment fragment = new ManagerForTeacherFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manager_for_teacher, container, false);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        presenter = new ManagerForTeacherPresenter(getContext(), this);
        connectNewStudent();
        return binding.getRoot();
    }

    private void connectNewStudent(){
        binding.btnConnectNewStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBottomSheetSearch();
            }
        });

        binding.tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBottomSheetSearch();
            }
        });
    }

    private void setBottomSheetSearch(){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getContext())
                .inflate(R.layout.layout_bottom_sheet_search,
                        (LinearLayout)getActivity().findViewById(R.id.bottomSheetSearch));

        final EditText edtSearch = bottomSheetView.findViewById(R.id.edtSearch);
        ImageButton btnSearch = bottomSheetView.findViewById(R.id.btnSearch);
        final ImageView imgAvatarConnectStudent = bottomSheetView.findViewById(R.id.imgAvatarConnectStudent);
        final ImageView online = bottomSheetView.findViewById(R.id.online);
        final TextView tvNameConnectStudent = bottomSheetView.findViewById(R.id.tvNameConnectStudent);
        final TextView gender = bottomSheetView.findViewById(R.id.tvGenderConnectStudent);
        final TextView object = bottomSheetView.findViewById(R.id.tvObject);
        final CardView view_connect_student = bottomSheetView.findViewById(R.id.view_connect_student);
        final Button btnConnect = bottomSheetView.findViewById(R.id.btnConnect);
        TextView tvTitle = bottomSheetView.findViewById(R.id.tvTitle);
        final TextView tvNotice = bottomSheetView.findViewById(R.id.tvNotice);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtSearch.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Chưa nhập số điện thoại!", Toast.LENGTH_SHORT).show();
                }
                else if(edtSearch.getText().length() != 10 || edtSearch.getText().charAt(0) != '0'){
                    Toast.makeText(getContext(), "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
                }
                else {
                    DatabaseReference mData = FirebaseDatabase.getInstance().getReference(Helper.STUDENT);
                    mData.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean have = false;
                            for (final DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                final Student student = dataSnapshot.getValue(Student.class);
                                if(student.getPhoneNumber().equals(Helper.VIETNAM + edtSearch.getText().toString().substring(1))){
                                    have = true;
                                    view_connect_student.setVisibility(View.VISIBLE);
                                    btnConnect.setVisibility(View.VISIBLE);
                                    tvNotice.setVisibility(View.VISIBLE);
                                    Glide.with(getContext()).load(student.getLinkAvatarStudent()).into(imgAvatarConnectStudent);
                                    tvNameConnectStudent.setText(student.getStudentName());
                                    gender.setText(student.getGender());
                                    object.setText(Helper.STUDENT_TV);
                                    if(student.getStatus().equals("online"))
                                        online.setVisibility(View.VISIBLE);
                                    else
                                        online.setVisibility(View.GONE);

                                    btnConnect.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            presenter.checkExistsConnectUser(FirebaseAuth.getInstance().getUid(),
                                                    dataSnapshot.getKey(),
                                                    binding.tvNameTeacher.getText().toString());
                                            bottomSheetDialog.dismiss();
                                            Helper.hideKeyBoard(getActivity());
                                        }
                                    });

                                    break;
                                }
                            }
                            if(!have)
                                Toast.makeText(getContext(), "Không tồn tại tài khoản học sinh có số điện thoại này!", Toast.LENGTH_LONG).show();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}});
                }
            }
        });

        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void confirmRequest(final String uidStudent, final String nameSender){
        binding.tvRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Xác nhận liên kết");
                builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.createDataConnect(FirebaseAuth.getInstance().getCurrentUser().getUid(), uidStudent);
                        dialog.dismiss();
                    }
                });
                builder.setMessage("Xác nhận kết nối với Học sinh " + nameSender);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public void getDataTeacher(Teacher teacher) {
        binding.tvNameTeacher.setText(teacher.getTeacherName());
        if(getContext() != null){
            Glide.with(getContext()).load(teacher.getLinkAvatarTeacher()).into(binding.imgAvatarTeacher);
        }
    }

    @Override
    public void sendnotificationRequest(String mess) {
        Toast.makeText(getContext(), mess, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getRequestFromStudent(HashMap<String, String> hashMap) {
        if(hashMap != null){
            String nameSender = hashMap.get("nameSender");
            String uidStudent = hashMap.get("uidStudent");
            String notice = "Thông báo: Học sinh " + nameSender + " đã gửi cho bạn một yêu cầu kết nối !";
            binding.tvRequest.setText(Helper.setUnderlineString(notice));
            binding.tvRequest.setVisibility(View.VISIBLE);
            confirmRequest(uidStudent, nameSender);
        }
        else{
            binding.tvRequest.setText("");
            binding.tvRequest.setVisibility(View.GONE);
        }
    }

    @Override
    public void getDataConnect(List<DataConnect> dataConnects) {
        if(dataConnects.size() > 0){
            binding.tvAdd.setVisibility(View.GONE);
        }
        else{
            binding.tvAdd.setVisibility(View.VISIBLE);
        }
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        binding.rvListManagerForTeacher.setLayoutManager(layoutManager);
        AdapterDataConnect adapterDataConnect = new AdapterDataConnect(dataConnects, getContext(), Helper.STUDENT);
        binding.rvListManagerForTeacher.setAdapter(adapterDataConnect);
        adapterDataConnect.setiClickItemDataConnect(new IClickItemDataConnect() {
            @Override
            public void onClick(DataConnect dataConnect) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("DataConnect", dataConnect);
                if(dataConnect.getUpdate().equals("no"))
                    getFragment(UpdateDataConectForTeacherFragment.newInstance(bundle), UpdateDataConectForTeacherFragment.TAG);
                else{
                    getFragment(DataConnectForTeacherFragment.newInstance(bundle), DataConnectForTeacherFragment.TAG);
                }
            }
        });
    }

    public void getFragment(Fragment fragment, String nameFragment) {
        try {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_fragment_right, R.anim.exit_fragment_right, R.anim.pop_enter_fragment, R.anim.pop_exit_fragment)
                    .replace(R.id.fullScreenActivityUser, fragment)
                    .addToBackStack(nameFragment)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}