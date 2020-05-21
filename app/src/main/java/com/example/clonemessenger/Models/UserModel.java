package com.example.clonemessenger.Models;

import android.net.Uri;

public class UserModel {
    String name;
    String imagePath;
    String imageCompressPath;

    public UserModel() {
    }

    public String getImageCompressPath() {
        return imageCompressPath;
    }

    public UserModel(String name, String imagePath, String imageCompressPath) {
        this.name = name;
        this.imagePath = imagePath;
        this.imageCompressPath = imageCompressPath;
    }

    public void setImageCompressPath(String imageCompressPath) {
        this.imageCompressPath = imageCompressPath;
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
