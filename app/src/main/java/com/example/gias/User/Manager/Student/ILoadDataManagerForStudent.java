package com.example.gias.User.Manager.Student;

import com.example.gias.Object.DataConnect;
import com.example.gias.Object.Student;

import java.util.HashMap;
import java.util.List;

public interface ILoadDataManagerForStudent {
    void getDataStudent (Student student);
    void sendnotificationRequest(String mess);
    void getRequestFromTeacher(HashMap<String, String> hashMap);
    void getDataConnect(List<DataConnect> dataConnects);
}
