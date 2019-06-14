package com.edbrix.connectbrix.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class UserMeetingByDateParentData implements Serializable {

    @SerializedName("Success")
    @Expose
    private Integer success;
    @SerializedName("Code")
    @Expose
    private String code;
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Meetings")
    @Expose
    private List<UserMeetingListResponseData> meetings = null;
    private final static long serialVersionUID = -4681838781431222730L;

    @SerializedName("Error")
    @Expose
    private Error error;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

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

    public List<UserMeetingListResponseData> getMeetings() {
        return meetings;
    }

    public void setMeetings(List<UserMeetingListResponseData> meetings) {
        this.meetings = meetings;
    }
}
