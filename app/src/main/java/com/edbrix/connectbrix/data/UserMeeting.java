package com.edbrix.connectbrix.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserMeeting {
    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("meetingDate")
    @Expose
    private String meetingDate;
    @SerializedName("meetingTime")
    @Expose
    private String meetingTime;
    @SerializedName("ParticipantCount")
    @Expose
    private String meetingParticipantsCount;
    @SerializedName("Title")
    @Expose
    private String title;
    @SerializedName("Agenda")
    @Expose
    private String agenda;
    @SerializedName("MeetingId")
    @Expose
    private String meetingId;
    @SerializedName("isAvailable")
    @Expose
    private String isAvailable = "";
    @SerializedName("IsHost")
    @Expose
    private Integer isHost;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(String meetingDate) {
        this.meetingDate = meetingDate;
    }

    public String getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(String meetingTime) {
        this.meetingTime = meetingTime;
    }

    public String getMeetingParticipantsCount() {
        return meetingParticipantsCount;
    }

    public void setMeetingParticipantsCount(String meetingParticipantsCount) {
        this.meetingParticipantsCount = meetingParticipantsCount;
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

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(String isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Integer getIsHost() {
        return isHost;
    }

    public void setIsHost(Integer isHost) {
        this.isHost = isHost;
    }

}
