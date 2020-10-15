package com.example.gias.User.news;

import com.example.gias.Object.Post;
import com.example.gias.Object.Student;
import com.example.gias.Object.Teacher;

import java.util.List;

public interface ILoadDataNews {
    void getDataStudent(Student student);
    void getDataTeacher(Teacher teacher);
    void getListNews(List <Post> posts);
}
