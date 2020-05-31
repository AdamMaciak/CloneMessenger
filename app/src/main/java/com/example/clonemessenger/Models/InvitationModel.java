package com.example.clonemessenger.Models;

import com.google.firebase.firestore.DocumentReference;

public class InvitationModel {
    String idUser;
    boolean idAccepted;
    String idContact;

    public InvitationModel(String idUser, boolean idAccepted, String idContact) {
        this.idUser = idUser;
        this.idAccepted = idAccepted;
        this.idContact = idContact;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public boolean isIdAccepted() {
        return idAccepted;
    }

    public void setIdAccepted(boolean idAccepted) {
        this.idAccepted = idAccepted;
    }

    public String getIdContact() {
        return idContact;
    }

    public void setIdContact(String idContact) {
        this.idContact = idContact;
    }
}
