package com.edbrix.connectbrix.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MeetingOrganizationListData {
    @SerializedName("Success")
    @Expose
    private Integer success;
    @SerializedName("Code")
    @Expose
    private String code;
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("MeetingOrganizationList")
    @Expose
    private List<MeetingOrganizationList> meetingOrganizationList = null;

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

    public List<MeetingOrganizationList> getMeetingOrganizationList() {
        return meetingOrganizationList;
    }

    public void setMeetingOrganizationList(List<MeetingOrganizationList> meetingOrganizationList) {
        this.meetingOrganizationList = meetingOrganizationList;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
