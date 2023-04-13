package com.subi.soccerbooking.model;

import java.io.Serializable;

public class Booking implements Serializable {
    private int id;
    private String name, phone, datetime, idField, note, email;

    public Booking() {
    }

    public Booking(int id, String name, String phone, String datetime, String idField, String note, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.datetime = datetime;
        this.idField = idField;
        this.note = note;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getIdField() {
        return idField;
    }

    public void setIdField(String idField) {
        this.idField = idField;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", datetime='" + datetime + '\'' +
                ", idField='" + idField + '\'' +
                ", note='" + note + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
