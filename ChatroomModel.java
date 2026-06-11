package com.example.samefor.model;

import com.google.firebase.Timestamp;
import java.util.List;

public class ChatroomModel {
    private String chatroomId;
    private List<String> userIds;
    private List<String> usernames;
    private Timestamp lastmessageTimestamp;
    private String lastMessageSenderId;
    private String lastMessage;

    public ChatroomModel() {}

    public ChatroomModel(String chatroomId, List<String> userIds, List<String> usernames, Timestamp lastmessageTimestamp, String lastMessageSenderId, String lastMessage) {
        this.chatroomId = chatroomId;
        this.userIds = userIds;
        this.usernames = usernames;
        this.lastmessageTimestamp = lastmessageTimestamp;
        this.lastMessageSenderId = lastMessageSenderId;
        this.lastMessage = lastMessage;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public List<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }

    public Timestamp getLastmessageTimestamp() {
        return lastmessageTimestamp;
    }

    public void setLastmessageTimestamp(Timestamp lastmessageTimestamp) {
        this.lastmessageTimestamp = lastmessageTimestamp;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
