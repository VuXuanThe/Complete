package com.example.gias.FindTeacher;

import com.example.gias.Object.Teacher;

import java.util.List;

public interface ILoadTeacher {
    void loadSuccess(List<Teacher> teachers);
    void loadFail(String mess);
}
