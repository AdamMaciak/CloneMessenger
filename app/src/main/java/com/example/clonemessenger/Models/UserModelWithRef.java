package com.example.clonemessenger.Models;

import com.google.firebase.firestore.DocumentReference;

//Tylko dla ekranu dodawnia uzytkownika

public class UserModelWithRef {
    String pathToDocument;
    UserModel userModel;

    public UserModelWithRef() {
    }

    public UserModelWithRef(String pathToDocument,
                            UserModel userModel) {
        this.pathToDocument = pathToDocument;
        this.userModel = userModel;
    }

    public String getPathToDocument() {
        return pathToDocument;
    }

    public void setPathToDocument(String pathToDocument) {
        this.pathToDocument = pathToDocument;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }
}
