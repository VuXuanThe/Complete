package com.example.gias.Object;

import android.graphics.Bitmap;
import android.os.Parcelable;

import java.io.Serializable;

public class Student implements Serializable{
    private String  linkAvatarStudent;
    private String  phoneNumber;
    private String  studentName;
    private double  longitude;
    private double  latitude;
    private String  gender;
    private String  study;
    private String  age;
    private String  object;
    private String  subject;
    private String  freeTime;
    private String  tuition;
    private transient Bitmap  avatarStudent;
    private String  status;

    public Student() {}

    public Student(String linkAvatarStudent, String phoneNumber, String studentName,
                   double longitude, double latitude, String gender,
                   String study, String age, String object,
                   String subject, String freeTime, String tuition, Bitmap avatarStudent, String status) {
        this.linkAvatarStudent = linkAvatarStudent;
        this.phoneNumber = phoneNumber;
        this.studentName = studentName;
        this.longitude = longitude;
        this.latitude = latitude;
        this.gender = gender;
        this.study = study;
        this.age = age;
        this.object = object;
        this.subject = subject;
        this.freeTime = freeTime;
        this.tuition = tuition;
        this.avatarStudent = avatarStudent;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLinkAvatarStudent() {
        return linkAvatarStudent;
    }

    public void setLinkAvatarStudent(String linkAvatarStudent) {
        this.linkAvatarStudent = linkAvatarStudent;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStudy() {
        return study;
    }

    public void setStudy(String study) {
        this.study = study;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
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

    public Bitmap getAvatarStudent() {
        return avatarStudent;
    }

    public void setAvatarStudent(Bitmap avatarStudent) {
        this.avatarStudent = avatarStudent;
    }
}
