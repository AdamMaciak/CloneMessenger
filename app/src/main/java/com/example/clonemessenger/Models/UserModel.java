package com.example.clonemessenger.Models;

import android.net.Uri;

import java.util.Date;

public class UserModel {
    String name;
    String imagePath;
    String imageCompressPath;
    boolean fullVersion;
    boolean isOnline;
    Date lastOnline;

    public Date getLastOnline() {
        return lastOnline;
    }

    public UserModel(String name, String imagePath, String imageCompressPath, boolean fullVersion,
                     boolean isOnline, Date lastOnline) {
        this.name = name;
        this.imagePath = imagePath;
        this.imageCompressPath = imageCompressPath;
        this.fullVersion = fullVersion;
        this.isOnline = isOnline;
        this.lastOnline = lastOnline;
    }

    public void setLastOnline(Date lastOnline) {
        this.lastOnline = lastOnline;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public UserModel(String name, String imagePath, String imageCompressPath, boolean fullVersion,
                     boolean isOnline) {
        this.name = name;
        this.imagePath = imagePath;
        this.imageCompressPath = imageCompressPath;
        this.fullVersion = fullVersion;
        this.isOnline = isOnline;
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
