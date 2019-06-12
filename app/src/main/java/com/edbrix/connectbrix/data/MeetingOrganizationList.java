package com.edbrix.connectbrix.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MeetingOrganizationList {

    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("OrganizationName")
    @Expose
    private String organizationName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

}
