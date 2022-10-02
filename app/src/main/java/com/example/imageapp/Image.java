package com.example.imageapp;

public class Image {
    private int _id;
    private String address;

    public Image(int _id, String address) {
        this._id = _id;
        this.address = address;
    }

    public int getId() {
        return _id;
    }
    public void setId(int _id) {
        this._id = _id;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
}
