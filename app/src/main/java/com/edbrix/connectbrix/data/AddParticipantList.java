package com.edbrix.connectbrix.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddParticipantList implements java.io.Serializable {
    @SerializedName("Type")
    @Expose
    private String type = "";
    @SerializedName("UserId")
    @Expose
    private String userId = "";
    @SerializedName("Email")
    @Expose
    private String email = "";
    @SerializedName("GroupId")
    @Expose
    private String groupId = "";

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
