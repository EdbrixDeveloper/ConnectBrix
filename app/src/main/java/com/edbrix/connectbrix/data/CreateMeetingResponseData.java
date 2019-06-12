package com.edbrix.connectbrix.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CreateMeetingResponseData implements Serializable {

    @SerializedName("Success")
    @Expose
    private Integer success;
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("MeetingId")
    @Expose
    private Integer meetingId;

    @SerializedName("Error")
    @Expose
    private Error error;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Integer meetingId) {
        this.meetingId = meetingId;
    }

}
