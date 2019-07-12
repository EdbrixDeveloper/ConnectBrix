package com.edbrix.connectbrix.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class TimeZoneParentData implements Serializable {

    @SerializedName("Success")
    @Expose
    private Integer success;
    @SerializedName("Code")
    @Expose
    private String code;
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("TimezoneList")
    @Expose

    private List<TimeZoneListData> timezoneList = null;

    @SerializedName("Error")
    @Expose
    private Error error;

    private final static long serialVersionUID = -8150575024492891869L;

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

    public List<TimeZoneListData> getTimezoneList() {
        return timezoneList;
    }

    public void setTimezoneList(List<TimeZoneListData> timezoneList) {
        this.timezoneList = timezoneList;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
