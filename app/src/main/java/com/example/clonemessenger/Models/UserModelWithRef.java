package com.example.clonemessenger.Models;

//Tylko dla ekranu dodawnia uzytkownika

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserModelWithRef)) return false;
        UserModelWithRef that = (UserModelWithRef) o;
        return getPathToDocument().equals(that.getPathToDocument()) &&
                getUserModel().equals(that.getUserModel());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPathToDocument(), getUserModel());
    }
}
