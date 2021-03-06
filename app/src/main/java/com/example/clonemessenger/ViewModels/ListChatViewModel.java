package com.example.clonemessenger.ViewModels;

import androidx.annotation.NonNull;

import java.util.Date;

public class ListChatViewModel {

    private String title;
    private String lastMessage;
    private String imageChatPath;
    private Date lastMessageDate;
    private String idChat;
    private long countUnreadMessages;

    public long getCountUnreadMessages() {
        return countUnreadMessages;
    }

    public void setCountUnreadMessages(long countUnreadMessages) {
        this.countUnreadMessages = countUnreadMessages;
    }

    public ListChatViewModel() {
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public ListChatViewModel(String title, String lastMessage, String imageChatPath,
                             Date lastMessageDate, String idChat, long countUnreadMessages) {
        this.title = title;
        this.lastMessage = lastMessage;
        this.imageChatPath = imageChatPath;
        this.lastMessageDate = lastMessageDate;
        this.idChat = idChat;
        this.countUnreadMessages = countUnreadMessages;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getImageChatPath() {
        return imageChatPath;
    }

    public void setImageChatPath(String imageChatPath) {
        this.imageChatPath = imageChatPath;
    }

    public Date getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(Date lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public String getIdChat() {
        return idChat;
    }

    public void setIdChat(String idChat) {
        this.idChat = idChat;
    }
}

