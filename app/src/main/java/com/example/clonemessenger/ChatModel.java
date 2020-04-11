package com.example.clonemessenger;

public class ChatModel {
    private String sender;
    private String receiver;
    private String message;
    private String image;

    public ChatModel(String sender, String receiver, String message, String image) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.image = image;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
