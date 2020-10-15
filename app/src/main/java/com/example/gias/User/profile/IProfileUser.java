package com.example.gias.User.profile;

import com.example.gias.Object.Post;
import com.example.gias.Object.Student;
import com.example.gias.Object.Teacher;
import com.example.gias.Object.UserChat;

import java.util.List;

public interface IProfileUser {
    void getDataTeacher(Teacher teacher);
    void getDataStudent(Student student);
    void getPostsUser(List<Post> posts);
    void getUserChatSender(UserChat userChatSender);
}
