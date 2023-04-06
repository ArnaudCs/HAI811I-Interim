package com.example.interim.models;

public class Conversation {
    private String id;
    private String contact;
    private boolean unread;
    private String lastMsg;

    public Conversation(String id, String contact, boolean unread, String lastMsg) {
        this.id = id;
        this.contact = contact;
        this.unread = unread;
        this.lastMsg = lastMsg;
    }

    public Conversation(String contact, boolean unread, String lastMsg) {
        this.contact = contact;
        this.unread = unread;
        this.lastMsg = lastMsg;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }
}
