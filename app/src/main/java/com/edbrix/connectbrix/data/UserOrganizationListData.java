package com.edbrix.connectbrix.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserOrganizationListData implements Serializable {

    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("OrganizationName")
    @Expose
    private String organizationName;
    @SerializedName("ApiKey")
    @Expose
    private String apiKey;
    @SerializedName("Secretekey")
    @Expose
    private String secretekey;
    @SerializedName("SchoolLogoUrl")
    @Expose
    private String schoolLogoUrl;
    @SerializedName("OrganizationDomain")
    @Expose
    private String organizationDomain;
    @SerializedName("OrganizationURL")
    @Expose
    private String organizationURL;
    private final static long serialVersionUID = -1864480739430301757L;

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

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSecretekey() {
        return secretekey;
    }

    public void setSecretekey(String secretekey) {
        this.secretekey = secretekey;
    }

    public String getSchoolLogoUrl() {
        return schoolLogoUrl;
    }

    public void setSchoolLogoUrl(String schoolLogoUrl) {
        this.schoolLogoUrl = schoolLogoUrl;
    }

    public String getOrganizationDomain() {
        return organizationDomain;
    }

    public void setOrganizationDomain(String organizationDomain) {
        this.organizationDomain = organizationDomain;
    }

    public String getOrganizationURL() {
        return organizationURL;
    }

    public void setOrganizationURL(String organizationURL) {
        this.organizationURL = organizationURL;
    }
}
