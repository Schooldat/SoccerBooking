package com.subi.soccerbooking.model;

import java.io.Serializable;

public class Field implements Serializable {
    private int id;
    private String name, price, image,  email;

    public Field() {
    }

    public Field(int id, String name, String price, String image,String email) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Field{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", image='" + image + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
