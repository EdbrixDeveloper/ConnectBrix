package com.edbrix.connectbrix.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MeetingListData {
    @SerializedName("Success")
    @Expose
    private Integer success;
    @SerializedName("Code")
    @Expose
    private String code;
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("MeetingRequestCount")
    @Expose
    private Integer meetingRequestCount;
    @SerializedName("MeetingRequestCountAll")
    @Expose
    private Integer meetingRequestCountAll;
    @SerializedName("UserMeetingsDates")
    @Expose
    private List<UserMeetingsDate> userMeetingsDates = null;

    @SerializedName("Error")
    @Expose
    private Error error;

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getMeetingRequestCount() {
        return meetingRequestCount;
    }

    public void setMeetingRequestCount(Integer meetingRequestCount) {
        this.meetingRequestCount = meetingRequestCount;
    }

    public List<UserMeetingsDate> getUserMeetingsDates() {
        return userMeetingsDates;
    }

    public void setUserMeetingsDates(List<UserMeetingsDate> userMeetingsDates) {
        this.userMeetingsDates = userMeetingsDates;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public Integer getMeetingRequestCountAll() {
        return meetingRequestCountAll;
    }

    public void setMeetingRequestCountAll(Integer meetingRequestCountAll) {
        this.meetingRequestCountAll = meetingRequestCountAll;
    }
}
