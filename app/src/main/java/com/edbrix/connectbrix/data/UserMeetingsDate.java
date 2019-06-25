package com.edbrix.connectbrix.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UserMeetingsDate {
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("meetingCount")
    @Expose
    private Integer meetingCount;
    @SerializedName("userMeetings")
    @Expose
    private ArrayList<UserMeeting> userMeetings = null;

    public UserMeetingsDate(String date, Integer meetingCount, ArrayList<UserMeeting> userMeeting) {
        super();
        this.date = date;
        this.meetingCount = meetingCount;
        this.userMeetings = userMeeting;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getMeetingCount() {
        return meetingCount;
    }

    public void setMeetingCount(Integer meetingCount) {
        this.meetingCount = meetingCount;
    }

    public ArrayList<UserMeeting> getUserMeetings() {
        return userMeetings;
    }

    public void setUserMeetings(ArrayList<UserMeeting> userMeetings) {
        this.userMeetings = userMeetings;
    }
}
