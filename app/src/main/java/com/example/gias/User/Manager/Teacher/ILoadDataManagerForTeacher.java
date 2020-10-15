package com.example.gias.User.Manager.Teacher;

import com.example.gias.Object.DataConnect;
import com.example.gias.Object.Teacher;

import java.util.HashMap;
import java.util.List;

public interface ILoadDataManagerForTeacher {
    void getDataTeacher(Teacher teacher);
    void sendnotificationRequest(String mess);
    void getRequestFromStudent(HashMap<String, String> hashMap);
    void getDataConnect(List<DataConnect> dataConnects);
}
