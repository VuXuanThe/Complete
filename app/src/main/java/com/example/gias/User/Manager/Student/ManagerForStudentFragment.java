package com.example.gias.User.Manager.Student;

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
import com.example.gias.Object.DataConnect;
import com.example.gias.Object.Student;
import com.example.gias.Object.Teacher;
import com.example.gias.R;
import com.example.gias.User.Manager.DataConnect.AdapterDataConnect;
import com.example.gias.User.Manager.DataConnect.IClickItemDataConnect;
import com.example.gias.User.Manager.Teacher.UpdateDataConectForTeacherFragment;
import com.example.gias.databinding.FragmentManagerForStudentBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;


public class ManagerForStudentFragment extends Fragment implements ILoadDataManagerForStudent {
    private FragmentManagerForStudentBinding binding;
    private ManagerForStudentPresenter presenter;

    public static ManagerForStudentFragment newInstance() {
        ManagerForStudentFragment fragment = new ManagerForStudentFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manager_for_student, container, false);
        presenter = new ManagerForStudentPresenter(getContext(), this);
        connectNewTeacher();
        return binding.getRoot();
    }

    private void connectNewTeacher(){
        binding.btnConnectNewTeacher.setOnClickListener(new View.OnClickListener() {
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
                    DatabaseReference mData = FirebaseDatabase.getInstance().getReference(Helper.TEACHER);
                    mData.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean have = false;
                            for (final DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                final Teacher teacher = dataSnapshot.getValue(Teacher.class);
                                if(teacher.getPhoneNumber().equals(Helper.VIETNAM + edtSearch.getText().toString().substring(1))){
                                    have = true;
                                    view_connect_student.setVisibility(View.VISIBLE);
                                    btnConnect.setVisibility(View.VISIBLE);
                                    tvNotice.setVisibility(View.VISIBLE);
                                    Glide.with(getContext()).load(teacher.getLinkAvatarTeacher()).into(imgAvatarConnectStudent);
                                    tvNameConnectStudent.setText(teacher.getTeacherName());
                                    gender.setText(teacher.getGender());
                                    object.setText(Helper.TEACHER_TV);
                                    if(teacher.getStatus().equals("online"))
                                        online.setVisibility(View.VISIBLE);
                                    else
                                        online.setVisibility(View.GONE);

                                    btnConnect.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            presenter.checkExistsConnectUser(dataSnapshot.getKey(),
                                                    FirebaseAuth.getInstance().getUid(),
                                                    binding.tvNameStudent.getText().toString());
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

    private void confirmRequest(final String uidTeacher, final String nameSender){
        binding.tvRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Xác nhận liên kết");
                builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.createDataConnect(uidTeacher, FirebaseAuth.getInstance().getCurrentUser().getUid());
                        dialog.dismiss();
                    }
                });
                builder.setMessage("Xác nhận kết nối với Gia sư " + nameSender);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public void getDataStudent(Student student) {
        binding.tvNameStudent.setText(student.getStudentName());
        if(getContext() != null){
            Glide.with(getContext()).load(student.getLinkAvatarStudent()).into(binding.imgAvatarStudent);
        }
    }

    @Override
    public void sendnotificationRequest(String mess) {
        Toast.makeText(getContext(), mess, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getRequestFromTeacher(HashMap<String, String> hashMap) {
        if(hashMap != null){
            String nameSender = hashMap.get("nameSender");
            String uidTeacher = hashMap.get("uidTeacher");
            String notice = "Thông báo:  Gia sư " + nameSender + " đã gửi cho bạn một yêu cầu kết nối!";
            binding.tvRequest.setText(Helper.setUnderlineString(notice));
            binding.tvRequest.setVisibility(View.VISIBLE);
            confirmRequest(uidTeacher, nameSender);
        }
        else{
            binding.tvRequest.setText("");
            binding.tvRequest.setVisibility(View.GONE);
        }
    }

    @Override
    public void getDataConnect(List<DataConnect> dataConnects) {
        if(dataConnects.size() == 0){
            binding.tvAdd.setVisibility(View.VISIBLE);
        }
        else{
            binding.tvAdd.setVisibility(View.GONE);
        }
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        binding.rvListManagerForStudent.setLayoutManager(layoutManager);
        AdapterDataConnect adapterDataConnect = new AdapterDataConnect(dataConnects, getContext(), Helper.TEACHER);
        binding.rvListManagerForStudent.setAdapter(adapterDataConnect);
        adapterDataConnect.setiClickItemDataConnect(new IClickItemDataConnect() {
            @Override
            public void onClick(DataConnect dataConnect) {
                Bundle dataBundle = new Bundle();
                dataBundle.putSerializable("DataConnect", dataConnect);
                if(dataConnect.getUpdate().equals("no"))
                    getFragment(UpdateDataConectForStudentFragment.newInstance(dataBundle), UpdateDataConectForStudentFragment.TAG);
                else{
                    getFragment(DataConnectForStudentFragment.newInstance(dataBundle), DataConnectForStudentFragment.TAG);
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