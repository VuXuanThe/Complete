package com.example.gias.User.Chats.Interface;

import com.example.gias.Object.Student;
import com.example.gias.Object.Teacher;
import com.example.gias.Object.UserChat;

import java.util.List;

public interface IGetDataChatFragment {
    void searchUser(List<Student> students, List <Teacher> teachers);
    void LoadUserChat(List<UserChat> userChats);
    void getDataTeacher(Teacher teacher);
    void getDataStudent(Student student);
}
