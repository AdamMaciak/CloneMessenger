package com.example.clonemessenger.Models;

public class ListChatModel {
    private String title;
    private String imageChat;
    private boolean isDeleted;
    private boolean haveImage;

    public ListChatModel() {
    }

    public ListChatModel(String title, String imageChat, boolean isDeleted, boolean haveImage) {
        this.title = title;
        this.imageChat = imageChat;
        this.isDeleted = isDeleted;
        this.haveImage = haveImage;
    }

    public String getImageChat() {
        return imageChat;
    }

    public void setImageChat(String imageChat) {
        this.imageChat = imageChat;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isHaveImage() {
        return haveImage;
    }

    public void setHaveImage(boolean haveImage) {
        this.haveImage = haveImage;
    }
}
