package com.example.samefor.model;

import com.google.firebase.Timestamp;

public class ChatMessageModel {
    private String messgae;
    private String sender;
    private Timestamp timestampl;

    public ChatMessageModel() {
    }

    public ChatMessageModel(String messgae, String sender, Timestamp timestampl) {
        this.messgae = messgae;
        this.sender = sender;
        this.timestampl = timestampl;
    }

    public String getMessgae() {
        return messgae;
    }

    public void setMessgae(String messgae) {
        this.messgae = messgae;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Timestamp getTimestampl() {
        return timestampl;
    }

    public void setTimestampl(Timestamp timestampl) {
        this.timestampl = timestampl;
    }
}
