package com.subi.soccerbooking.model;

public class History {
    private int id;
    private String name, price, hour, date, email, nameuser, phone;

    public History() {
    }

    public History(int id, String name, String price, String hour, String date, String email, String nameuser, String phone) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.hour = hour;
        this.date = date;
        this.email = email;
        this.nameuser = nameuser;
        this.phone = phone;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNameuser() {
        return nameuser;
    }

    public void setNameuser(String nameuser) {
        this.nameuser = nameuser;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "History{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", hour='" + hour + '\'' +
                ", date='" + date + '\'' +
                ", email='" + email + '\'' +
                ", nameuser='" + nameuser + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
