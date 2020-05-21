package com.example.clonemessenger.Models;

public class UserSharedPref {
    String name;
    String imagePath;
    String imageCompressPath;
    String id;

    public String getImageCompressPath() {
        return imageCompressPath;
    }

    public UserSharedPref(String name, String imagePath, String imageCompressPath, String id) {
        this.name = name;
        this.imagePath = imagePath;
        this.imageCompressPath = imageCompressPath;
        this.id = id;
    }

    public void setImageCompressPath(String imageCompressPath) {
        this.imageCompressPath = imageCompressPath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserSharedPref(String name, String imagePath, String id) {
        this.name = name;
        this.imagePath = imagePath;
        this.id = id;
    }

    public UserSharedPref() {
    }

    public UserSharedPref(String name, String imagePath) {
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
