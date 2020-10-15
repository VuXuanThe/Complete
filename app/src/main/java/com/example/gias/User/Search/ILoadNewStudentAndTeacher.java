package com.example.gias.User.Search;

import com.example.gias.Object.Student;
import com.example.gias.Object.Teacher;

import java.util.List;

public interface ILoadNewStudentAndTeacher {
    void loadNewStudents(List <Student> newStudents);
    void loadNewTeachers(List <Teacher> newTeachers);
}
