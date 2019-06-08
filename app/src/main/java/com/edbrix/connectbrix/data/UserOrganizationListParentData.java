package com.edbrix.connectbrix.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class UserOrganizationListParentData implements Serializable {

    @SerializedName("Success")
    @Expose
    private Integer success;
    @SerializedName("Code")
    @Expose
    private String code;
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("UserOrganizationList")
    @Expose
    private List<UserOrganizationListData> userOrganizationList = null;
    private final static long serialVersionUID = -124633347000079428L;

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

    public List<UserOrganizationListData> getUserOrganizationList() {
        return userOrganizationList;
    }

    public void setUserOrganizationList(List<UserOrganizationListData> userOrganizationList) {
        this.userOrganizationList = userOrganizationList;
    }
}
