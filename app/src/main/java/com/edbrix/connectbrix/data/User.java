
package com.edbrix.connectbrix.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("FirstName")
    @Expose
    private String firstName;
    @SerializedName("LastName")
    @Expose
    private String lastName;
    @SerializedName("Email")
    @Expose
    private String email;
    @SerializedName("UserType")
    @Expose
    private String userType;
    @SerializedName("ImageUrl")
    @Expose
    private String imageUrl;
    @SerializedName("SchoolDisplayName")
    @Expose
    private String schoolDisplayName;
    @SerializedName("SchoolLoginUrl")
    @Expose
    private String schoolLoginUrl;
    private final static long serialVersionUID = -5626855158322304629L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSchoolDisplayName() {
        return schoolDisplayName;
    }

    public void setSchoolDisplayName(String schoolDisplayName) {
        this.schoolDisplayName = schoolDisplayName;
    }

    public String getSchoolLoginUrl() {
        return schoolLoginUrl;
    }

    public void setSchoolLoginUrl(String schoolLoginUrl) {
        this.schoolLoginUrl = schoolLoginUrl;
    }
}
