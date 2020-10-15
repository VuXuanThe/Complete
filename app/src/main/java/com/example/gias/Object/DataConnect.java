package com.example.gias.Object;

import java.io.Serializable;

public class DataConnect implements Serializable {
    private Student student;
    private Teacher teacher;
    private String uidStudent;
    private String uidTeacher;
    private String connect;
    private String update;
    private String subject;
    private String tuition;
    private String numOfSessions;
    private String salary;
    private String resetSalary;

    public DataConnect(Student student, Teacher teacher,
                       String uidStudent, String uidTeacher,
                       String connect, String update,
                       String subject, String tuition,
                       String numOfSessions, String salary, String resetSalary) {
        this.student = student;
        this.teacher = teacher;
        this.uidStudent = uidStudent;
        this.uidTeacher = uidTeacher;
        this.connect = connect;
        this.update = update;
        this.subject = subject;
        this.tuition = tuition;
        this.numOfSessions = numOfSessions;
        this.salary = salary;
        this.resetSalary = resetSalary;
    }

    public DataConnect() {
    }

    public String getResetSalary() {
        return resetSalary;
    }

    public void setResetSalary(String resetSalary) {
        this.resetSalary = resetSalary;
    }

    public String getUidStudent() {
        return uidStudent;
    }

    public void setUidStudent(String uidStudent) {
        this.uidStudent = uidStudent;
    }

    public String getUidTeacher() {
        return uidTeacher;
    }

    public void setUidTeacher(String uidTeacher) {
        this.uidTeacher = uidTeacher;
    }

    public Student getStudent() {
        return student;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public String getConnect() {
        return connect;
    }

    public void setConnect(String connect) {
        this.connect = connect;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTuition() {
        return tuition;
    }

    public void setTuition(String tuition) {
        this.tuition = tuition;
    }

    public String getNumOfSessions() {
        return numOfSessions;
    }

    public void setNumOfSessions(String numOfSessions) {
        this.numOfSessions = numOfSessions;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }
}
