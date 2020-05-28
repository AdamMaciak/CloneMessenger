package com.example.clonemessenger.ViewModels;

public class ListChatViewModel {

    private String title;
    private String lastMessage;
    private String imageChatPath;
    private String lastMessageDate;
    private String idChat;
    private String refToChat;

    public
    ListChatViewModel() {
    }

    public ListChatViewModel(String title, String lastMessage, String imageChatPath,
                             String lastMessageDate, String idChat) {
        this.title = title;
        this.lastMessage = lastMessage;
        this.imageChatPath = imageChatPath;
        this.lastMessageDate = lastMessageDate;
        this.idChat = idChat;
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

    public String getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(String lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public String getIdChat() {
        return idChat;
    }

    public void setIdChat(String idChat) {
        this.idChat = idChat;
    }
}

