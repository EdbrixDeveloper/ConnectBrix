package com.edbrix.connectbrix.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Meeting {
    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("Title")
    @Expose
    private String title;
    @SerializedName("Agenda")
    @Expose
    private String agenda;
    @SerializedName("StartDateTime")
    @Expose
    private String startDateTime;
    @SerializedName("ParticipantList")
    @Expose
    private ArrayList<ParticipantList> participantList = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAgenda() {
        return agenda;
    }

    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public ArrayList<ParticipantList> getParticipantList() {
        return participantList;
    }

    public void setParticipantList(ArrayList<ParticipantList> participantList) {
        this.participantList = participantList;
    }

}
