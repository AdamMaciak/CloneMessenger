package com.example.clonemessenger.Models;

import android.net.Uri;

public class UserModel {
    String name;
    String imagePath;

    public UserModel() {
    }

    public UserModel(String name, String imagePath) {
        this.name = name;
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
