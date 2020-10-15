package com.example.gias.FindStudent;

import com.example.gias.Object.Student;

import java.util.List;

public interface ILoadStudent {
    void loadSuccess(List<Student> students);
    void loadFail(String mess);
}
