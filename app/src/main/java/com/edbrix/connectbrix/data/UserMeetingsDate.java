package com.edbrix.connectbrix.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
    private List<UserMeeting> userMeetings = null;

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

    public List<UserMeeting> getUserMeetings() {
        return userMeetings;
    }

    public void setUserMeetings(List<UserMeeting> userMeetings) {
        this.userMeetings = userMeetings;
    }
}
