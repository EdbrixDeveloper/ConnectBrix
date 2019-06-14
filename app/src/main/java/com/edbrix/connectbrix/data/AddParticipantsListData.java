package com.edbrix.connectbrix.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AddParticipantsListData implements java.io.Serializable {
    @SerializedName("APIKEY")
    @Expose
    private String aPIKEY = "";
    @SerializedName("SECRETKEY")
    @Expose
    private String sECRETKEY = "";
    @SerializedName("MeetingId")
    @Expose
    private String meetingId = "";
    @SerializedName("ParticipantList")
    @Expose
    private ArrayList<AddParticipantList> participantList = null;

    public String getAPIKEY() {
        return aPIKEY;
    }

    public void setAPIKEY(String aPIKEY) {
        this.aPIKEY = aPIKEY;
    }

    public String getSECRETKEY() {
        return sECRETKEY;
    }

    public void setSECRETKEY(String sECRETKEY) {
        this.sECRETKEY = sECRETKEY;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public ArrayList<AddParticipantList> getParticipantList() {
        return participantList;
    }

    public void setParticipantList(ArrayList<AddParticipantList> participantList) {
        this.participantList = participantList;
    }
}
