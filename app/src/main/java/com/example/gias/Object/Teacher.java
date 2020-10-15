package com.example.gias.Object;

import android.graphics.Bitmap;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

public class Teacher implements Serializable {
    private String  linkAvatarTeacher;
    private String  phoneNumber;
    private String  teacherName;
    private double  longitude;
    private double  latitude;
    private String  email;
    private String  gender;
    private String  age;
    private String  object;
    private String  experient;
    private String  subject;
    private String  vehicle;
    private String  freeTime;
    private String  tuition;
    private String  processWork;
    private transient Bitmap  avatarTeacher;
    private String  status;

    public Teacher(String linkAvatarTeacher,
                   String phoneNumber, String teacherName,
                   double longitude, double latitude,
                   String email, String gender,
                   String object, String experient,
                   String vehicle, String age, String subject,
                   String freeTime, String tuition, String processWork, Bitmap avatarTeacher, String status) {
        this.linkAvatarTeacher = linkAvatarTeacher;
        this.phoneNumber = phoneNumber;
        this.teacherName = teacherName;
        this.longitude = longitude;
        this.latitude = latitude;
        this.email = email;
        this.gender = gender;
        this.object = object;
        this.experient = experient;
        this.vehicle = vehicle;
        this.age = age;
        this.subject = subject;
        this.freeTime = freeTime;
        this.tuition = tuition;
        this.processWork = processWork;
        this.avatarTeacher = avatarTeacher;
        this.status = status;
    }

    public Teacher() {}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLinkAvatarTeacher() {
        return linkAvatarTeacher;
    }

    public void setLinkAvatarTeacher(String linkAvatarTeacher) {
        this.linkAvatarTeacher = linkAvatarTeacher;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getExperient() {
        return experient;
    }

    public void setExperient(String experient) {
        this.experient = experient;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFreeTime() {
        return freeTime;
    }

    public void setFreeTime(String freeTime) {
        this.freeTime = freeTime;
    }

    public String getTuition() {
        return tuition;
    }

    public void setTuition(String tuition) {
        this.tuition = tuition;
    }

    public String getProcessWork() {
        return processWork;
    }

    public void setProcessWork(String processWork) {
        this.processWork = processWork;
    }

    public Bitmap getAvatarTeacher() {
        return avatarTeacher;
    }

    public void setAvatarTeacher(Bitmap avatarTeacher) {
        this.avatarTeacher = avatarTeacher;
    }
}
