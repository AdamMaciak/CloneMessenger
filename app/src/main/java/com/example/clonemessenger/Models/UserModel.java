package com.example.clonemessenger.Models;

import android.net.Uri;

public class UserModel {
    String name;
    String imagePath;
    String imageCompressPath;
    boolean fullVersion;
    String desc;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public UserModel(String name, String imagePath, String imageCompressPath, boolean fullVersion,
                     String desc) {
        this.name = name;
        this.imagePath = imagePath;
        this.imageCompressPath = imageCompressPath;
        this.fullVersion = fullVersion;
        this.desc = desc;
    }

    public UserModel() {
    }

    public boolean isFullVersion() {
        return fullVersion;
    }

    public UserModel(String name, String imagePath, String imageCompressPath, boolean fullVersion) {
        this.name = name;
        this.imagePath = imagePath;
        this.imageCompressPath = imageCompressPath;
        this.fullVersion = fullVersion;
    }

    public void setFullVersion(boolean fullVersion) {
        this.fullVersion = fullVersion;
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
