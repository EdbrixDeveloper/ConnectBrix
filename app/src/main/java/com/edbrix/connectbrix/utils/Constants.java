package com.edbrix.connectbrix.utils;

public class Constants {

    public static String androidDeviceid;
    // TODO Change it to your web domain
    public final static String WEB_DOMAIN = "";
    // TODO Change it to your APP Key
    public final static String APP_KEY__ = "QVBAMTIjMllIRC1TREFTNUQtNUFTRksyMjEhZWRicml4QDE4";
    // TODO Change it to your APP Secret
    public final static String APP_SECRET__ = "MjQ1QDEyIzJZSEQtODVEQTJTM0RFQTg1Mz1JRTVCNEE1IWVkYnJpeEAxOA==";

    public static String HOST_ID = null;
    public static int StateId = -1;
    public static int CountryId = -1;
    public static String StateName = "";

    private final static String BASE_URL = "http://services.edbrix.net/";

    public final static String userLogin = BASE_URL.concat("auth/login");  // authenticate
    public final static String organizationList = BASE_URL.concat("auth/authorganizationlistbyuseremail");
    public final static String resetPassword = BASE_URL.concat("auth/resetpassword");
    public final static String changePassword = BASE_URL.concat("auth/changepassword");

    public final static String getCountryList = BASE_URL.concat("common/getcountrylist");
    public final static String getStateList = BASE_URL.concat("common/getstatelistbycountry");


    public final static String getUserPersonalData = BASE_URL.concat("auth/getuserdetails");
    public final static String updateUserPersonalData = BASE_URL.concat("auth/updateuserdetails");
    public final static String updateUserProfilePicture = BASE_URL.concat("auth/uploaduserprofilepicture");

    public final static String getMeetingList = BASE_URL.concat("connectbrix/getmeetinglist");
    public final static String getMeetingDetails = BASE_URL.concat("connectbrix/getmeetingdetails");
    public final static String deleteMeetingDetails = BASE_URL.concat("connectbrix/deletemeeting");
    public final static String deleteMeetingParticipant = BASE_URL.concat("connectbrix/deletemeetingparticipant");
    public final static String updateMeetingAvilabilityStatus = BASE_URL.concat("connectbrix/updatemeetingavilabilitystatus");

    public final static String getMeetingOrganizationList = BASE_URL.concat("connectbrix/getmeetingorganizationlist");
    public final static String getMeetingParticipantList = BASE_URL.concat("connectbrix/getmeetingparticipantlist");
    public final static String getMeetingStudentList = BASE_URL.concat("connectbrix/getmeetingstudentlist");
    public final static String getMeetingStudentParentList = BASE_URL.concat("connectbrix/getmeetingstudentparentlist");
    public final static String saveMeetingParticipants = BASE_URL.concat("connectbrix/savemeetingparticipants");

    public final static String createMeeting = BASE_URL.concat("connectbrix/createmeeting");
    public final static String getMeetingByDate = BASE_URL.concat("connectbrix/getmeetingsbydate");
    public final static String updateMeeting = BASE_URL.concat("connectbrix/updatemeeting");
    public final static String getAvailableMeetingDates = BASE_URL.concat("connectbrix/getavailablemeetingdates");
    public final static String savedevicetoken = BASE_URL.concat("connectbrix/savedevicetoken");

}
