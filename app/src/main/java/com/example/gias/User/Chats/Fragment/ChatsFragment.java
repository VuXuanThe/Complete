package com.example.gias.User.Chats.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gias.User.Chats.Adapter.AdapterListChat;
import com.example.gias.User.Chats.Adapter.AdapterStudentSearch;
import com.example.gias.User.Chats.Adapter.AdapterTeacherSearch;
import com.example.gias.User.Chats.Interface.ClickItemChat;
import com.example.gias.User.Chats.Interface.IGetDataChatFragment;
import com.example.gias.User.Chats.Model.ChatPresenter;
import com.example.gias.FindStudent.ClickItemStudent;
import com.example.gias.FindTeacher.ClickItemTeacher;
import com.example.gias.Helper;
import com.example.gias.User.Chats.MesengerActivity;
import com.example.gias.Object.Student;
import com.example.gias.Object.Teacher;
import com.example.gias.Object.UserChat;
import com.example.gias.R;
import com.example.gias.User.UserActivity;
import com.example.gias.databinding.FragmentChatsBinding;

import java.util.List;


public class ChatsFragment extends Fragment implements IGetDataChatFragment {
    public static final String TAG = "ChatsFragment";
    private FragmentChatsBinding binding;
    private ChatPresenter chatPresenternter;
    private AdapterListChat adapterListChat;
    private UserChat userChatSender;

    public static ChatsFragment newInstance() {
        ChatsFragment fragment = new ChatsFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chats, container, false);
        chatPresenternter = new ChatPresenter(getContext(), this);
        chatPresenternter.getData(UserActivity.OBJECT);
        searchUsers();
        return binding.getRoot();
    }

    private void searchUsers(){
        binding.tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Click vào thanh search bar để thêm bạn chat", Toast.LENGTH_SHORT).show();
            }
        });
        binding.edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().isEmpty()){
                    binding.LoadSearch.setVisibility(View.VISIBLE);
                    binding.rvListChat.setVisibility(View.GONE);
                    chatPresenternter.searchName(s.toString());
                }

                else {
                    binding.rvListTeacherSearch.setVisibility(View.GONE);
                    binding.rvListStudentSearch.setVisibility(View.GONE);
                    binding.LoadSearch.setVisibility(View.GONE);
                    binding.rvListChat.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void clickItemSearch(AdapterTeacherSearch adapterTeacherSearch,
                                 AdapterStudentSearch adapterStudentSearch){
        adapterStudentSearch.setClickItemStudent(new ClickItemStudent() {
            @Override
            public void onClick(Student student) {
                binding.edtSearchName.setText("");
                Helper.hideKeyBoard(getActivity());
                final UserChat userChat = new UserChat(student.getPhoneNumber(), student.getStudentName(), student.getLinkAvatarStudent(),
                        null, student.getObject(), null);

                Intent intent = new Intent(getContext(), MesengerActivity.class);
                intent.putExtra("UserChatReceiver", userChat);
                intent.putExtra("UserChatSender", userChatSender);
                startActivity(intent);
            }
        });

        adapterTeacherSearch.setClickItemTeacher(new ClickItemTeacher() {
            @Override
            public void onClick(Teacher teacher) {
                binding.edtSearchName.setText("");
                Helper.hideKeyBoard(getActivity());
                final UserChat userChat = new UserChat(teacher.getPhoneNumber(), teacher.getTeacherName(), teacher.getLinkAvatarTeacher(),
                        null, teacher.getObject(), null);
                Intent intent = new Intent(getContext(), MesengerActivity.class);
                intent.putExtra("UserChatReceiver", userChat);
                intent.putExtra("UserChatSender", userChatSender);
                startActivity(intent);
            }
        });
    }

    public void getDataTeacher(Teacher teacher) {
        userChatSender = new UserChat(teacher.getPhoneNumber(), teacher.getTeacherName(),
                teacher.getLinkAvatarTeacher(), null, teacher.getObject(), null);
        binding.tvUserName.setText(teacher.getTeacherName());
        if(getContext() != null)
            Glide.with(getContext()).load(teacher.getLinkAvatarTeacher()).into(binding.imgAvatarUser);
        else
            binding.imgAvatarUser.setImageResource(R.drawable.logo);

    }

    public void getDataStudent(Student student) {
        userChatSender = new UserChat(student.getPhoneNumber(), student.getStudentName(),
                student.getLinkAvatarStudent(), null, student.getObject(), null);
        binding.tvUserName.setText(student.getStudentName());
        if(getContext() != null)
            Glide.with(getContext()).load(student.getLinkAvatarStudent()).into(binding.imgAvatarUser);
        else
            binding.imgAvatarUser.setImageResource(R.drawable.logo);
    }

    @Override
    public void searchUser(List<Student> students, List<Teacher> teachers) {
        if(students.size() > 0) binding.rvListStudentSearch.setVisibility(View.VISIBLE);
        if(teachers.size() > 0) binding.rvListTeacherSearch.setVisibility(View.VISIBLE);
        binding.rvListStudentSearch.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        AdapterStudentSearch adapterStudentSearch = new AdapterStudentSearch(students, getContext());
        binding.rvListStudentSearch.setAdapter(adapterStudentSearch);

        binding.rvListTeacherSearch.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        AdapterTeacherSearch adapterTeacherSearch = new AdapterTeacherSearch(teachers, getContext());
        binding.rvListTeacherSearch.setAdapter(adapterTeacherSearch);
        clickItemSearch(adapterTeacherSearch, adapterStudentSearch);
    }

    @Override
    public void LoadUserChat(List<UserChat> userChats) {
        if(userChats.size() > 0)
            binding.tvAdd.setVisibility(View.GONE);
        binding.rvListChat.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        adapterListChat = new AdapterListChat(userChats, getContext());
        binding.rvListChat.setAdapter(adapterListChat);
        adapterListChat.setClickItemUserChat(new ClickItemChat() {
            @Override
            public void onClick(final UserChat userChat) {
                Helper.hideKeyBoard(getActivity());
                Intent intent = new Intent(getContext(), MesengerActivity.class);
                intent.putExtra("UserChatReceiver", userChat);
                intent.putExtra("UserChatSender", userChatSender);
                startActivity(intent);
            }
        });
    }
}