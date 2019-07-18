/**
 *
 */

package com.edbrix.connectbrix.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * Wrapper for managing session data.
 *
 * @author rajk
 */
public class SessionManager {

    private static final String LOGTAG = "SessionManager";

    private static final String APP_SHARED_PREFS_NAME = "ConnectBrix";

    private static final String PREFS_SESSION_USERNAME = "SessionUsername";
    private static final String PREFS_SESSION_PASSWORD = "SessionPassword";
    private static final String PREFS_SESSION_PROFILE_TOKEN = "SessionProfileToken";
    private static final String PREFS_SESSION_FCM_TOKEN = "SessionFCMToken";
    private static final String PREFS_SESSION_DEVICE_TOKEN = "SessionDeviceToken";
    private static final String PREFS_SESSION_DEVICE_TYPE = "SessionDeviceType";
    private static final String PREFS_SESSION_ORG_ID = "SessionOrgId";

    private static final String PREFS_SESSION_PREVIOUS_USER_ID = "SessionPreviousUserId";

    //users details
    private static final String PREFS_SESSION_USER_ID = "SessionUserId";
    private static final String PREFS_SESSION_USER_FIRST_NAME = "SessionUserFirstName";
    private static final String PREFS_SESSION_USER_LAST_NAME = "SessionUserLastName";
    private static final String PREFS_SESSION_USER_EMAIL = "SessionUserEmail";
    private static final String PREFS_SESSION_MobileNo = "MobileNo";
    private static final String PREFS_SESSION_USER_TYPE = "sessionUserType";
    private static final String PREFS_SESSION_PROFILE_IMAGE_Url = "ProfileImageUrl";
    private static final String PREFS_SESSION_SCHOOL_DISPALY_NAME = "sessionSchoolDisplayName";
    private static final String PREFS_SESSION_SCHOOL_LOGIN_URL = "schoolLoginUrl";
    private static final String PREFS_SESSION_ACCESS_TOKEN = "sessionAccessToken";

    private static final String PREFS_SESSION_ORGANISION_API_KEY = "organizationApiKey";
    private static final String PREFS_SESSION_ORGANISION_SECRET_KEY = "organizationSecretKey";

    private static final String PREFS_SESSION_USER_TICKET_ID = "SessionUserTicketId";
    private static final String PREFS_SESSION_USER_TICKET_NAME = "SessionUserTicketName";
    private static final String PREFS_SESSION_USER_LAT = "SessionUserLat";
    private static final String PREFS_SESSION_USER_LNG = "SessionUserLng";
    private static final String PREFS_SESSION_USER_TIME = "SessionUserTime";
    private static final String PREFS_SESSION_USER_TYPE_ID = "00";
    private static final String PREFS_SESSION_USER_TYPE_NAME = "AAAA";

    private static final String PREFS_SESSION_BB_TEST_REPORT_COUNT = "SessionBBTestReportCount";

    private static final String PREF_SESSION_WALKTHROUGH_SKIP_KEY = "wlkSkip";
    private static final String PREF_SESSION_APPUPDATE_SKIP_KEY = "AppUpdateSkip";
    private static final String PREFS_SESSION_PROFILE_IMAGE_BASE64 = "SessionProfileImageBase64";


    private static final String PREFS_SESSION_User_Designation = "Designation";
    private static final String PREFS_SESSION_User_Circle = "Circle";

    private static final String PREFS_SESSION_User_State = "State";
    private static final String PREFS_SESSION_User_Ssa = "SSA";

    private static final String PREFS_SESSION_User_Customer = "Customer";

    //google account
    private static final String PREF_ACCOUNT_NAME = "accountName";

    private static final String PREF_IS_PROFIL_PIC_UPDATE = "isProfilePicUpdate";

    private static final String PREF_IS_PASSWORD_SKIP = "isPasswordSkip";

    private SharedPreferences sharedPrefs;

    public SessionManager(Context context) {
        if (context != null) {
            this.sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        } else {
            Log.w(LOGTAG, "Invalid context!");
        }
    }

    /**
     * Checks if we have valid saved session credentials.
     *
     * @return
     */
  /*  public Boolean hasSessionCredentials() {
        return ((this.getSessionOrgId().length() > 0) && (this.getSessionProfileToken().length() > 0) && (this.getLoggedUserData() != null) && (this.getLoggedUserData().getId().length() > 0));
    }*/

    /**
     * Gets the session's saved username.
     *
     * @return
     */
    public String getSessionUsername() {
        return this.sharedPrefs.getString(PREFS_SESSION_USERNAME, "");
    }

    /**
     * Gets the session's saved password.
     *
     * @return
     */
    public String getSessionPassword() {
        return this.sharedPrefs.getString(PREFS_SESSION_PASSWORD, "");
    }

    /**
     * Gets the session's saved profileToken.
     *
     * @return
     */
    public String getSessionProfileToken() {
        return this.sharedPrefs.getString(PREFS_SESSION_PROFILE_TOKEN, "");
    }

    /**
     * Gets the session's saved deviceToken.
     *
     * @return
     */
    public String getSessionDeviceToken() {
        return this.sharedPrefs.getString(PREFS_SESSION_DEVICE_TOKEN, "");
    }

    /**
     * Gets the session's saved device FCM Token.
     *
     * @return
     */
    public String getSessionFCMToken() {
        return this.sharedPrefs.getString(PREFS_SESSION_FCM_TOKEN, "");
    }

    /**
     * Gets the session's saved userID.
     *
     * @return
     */
    public String getSessionUserId() {
        return this.sharedPrefs.getString(PREFS_SESSION_USER_ID, "");
    }

    public String getSessionPreviousUserId() {
        return this.sharedPrefs.getString(PREFS_SESSION_PREVIOUS_USER_ID, "");
    }

    /**
     * Gets the session's saved user First Name.
     *
     * @return
     */
    public String getSessionUserFirstName() {
        return this.sharedPrefs.getString(PREFS_SESSION_USER_FIRST_NAME, "");
    }

    /**
     * Gets the session's saved user Last Name.
     *
     * @return
     */
    public String getSessionUserFirstLast() {
        return this.sharedPrefs.getString(PREFS_SESSION_USER_LAST_NAME, "");
    }

    public String getSessionUserTypeId() {
        return this.sharedPrefs.getString(PREFS_SESSION_USER_TYPE_ID, "");
    }

    public String getSessionUserTypeName() {
        return this.sharedPrefs.getString(PREFS_SESSION_USER_TYPE_NAME, "");
    }

    public String getSessionUserEmail() {
        return this.sharedPrefs.getString(PREFS_SESSION_USER_EMAIL, "");
    }


    public String getSessionMobileNo() {
        return this.sharedPrefs.getString(PREFS_SESSION_MobileNo, "");
    }


    public String getSessionUserTicketId() {
        return this.sharedPrefs.getString(PREFS_SESSION_USER_TICKET_ID, "");
    }

    public String getSessionUserTicketName() {
        return this.sharedPrefs.getString(PREFS_SESSION_USER_TICKET_NAME, "");
    }


    /**
     * Gets the session's saved device orientation.
     *
     * @return
     */
    public String getSessionDeviceType() {
        return this.sharedPrefs.getString(PREFS_SESSION_DEVICE_TYPE, "mob");
    }


    /**
     * Gets the session's user profile image base64.
     *
     * @return
     */
    public String getPrefsSessionProfileImageBase64() {
        return this.sharedPrefs.getString(PREFS_SESSION_PROFILE_IMAGE_BASE64, "");
    }


    public String getSessionProfileImageUrl() {
        return this.sharedPrefs.getString(PREFS_SESSION_PROFILE_IMAGE_Url, "");
    }

    public String getSessionDesignation() {
        return this.sharedPrefs.getString(PREFS_SESSION_User_Designation, "");
    }

    public String getSessionCircle() {
        return this.sharedPrefs.getString(PREFS_SESSION_User_Circle, "");
    }

    public String getUser_State() {
        return this.sharedPrefs.getString(PREFS_SESSION_User_State, "");
    }

    public String getUser_Ssa() {
        return this.sharedPrefs.getString(PREFS_SESSION_User_Ssa, "");
    }

    public String getSessionCustomer() {
        return this.sharedPrefs.getString(PREFS_SESSION_User_Customer, "");
    }

    public String getSessionSiteLat() {
        return this.sharedPrefs.getString(PREFS_SESSION_USER_LAT, "");
    }

    public String getSessionSiteLng() {
        return this.sharedPrefs.getString(PREFS_SESSION_USER_LNG, "");
    }

    public String getSessionBBTestReportArrayCount() {
        return this.sharedPrefs.getString(PREFS_SESSION_BB_TEST_REPORT_COUNT, "");
    }

    public String getSessionBBTestReportTime() {
        return this.sharedPrefs.getString(PREFS_SESSION_USER_TIME, "");
    }

    public String getSessionUserType() {
        return this.sharedPrefs.getString(PREFS_SESSION_USER_TYPE, "");
    }

    public String getPrefsSessionSchoolDispalyName() {
        return this.sharedPrefs.getString(PREFS_SESSION_SCHOOL_DISPALY_NAME, "");
    }

    public String getPrefsSessionSchoolLoginUrl() {
        return this.sharedPrefs.getString(PREFS_SESSION_SCHOOL_DISPALY_NAME, "");
    }

    public String getPrefsSessionAccessToken() {
        return this.sharedPrefs.getString(PREFS_SESSION_ACCESS_TOKEN, "");
    }

    public String getPrefsOrganizationApiKey() {
        return this.sharedPrefs.getString(PREFS_SESSION_ORGANISION_API_KEY, "");
    }

    public String getPrefsOrganizationSecretKey() {
        return this.sharedPrefs.getString(PREFS_SESSION_ORGANISION_SECRET_KEY, "");
    }

    public String getGoogleAccount() {
        return this.sharedPrefs.getString(PREF_ACCOUNT_NAME, "");
    }

    public String getIsProfilePicUpdated() {
        return this.sharedPrefs.getString(PREF_IS_PROFIL_PIC_UPDATE, "");
    }

    public String getPrefIsPasswordSkip() {
        return this.sharedPrefs.getString(PREF_IS_PASSWORD_SKIP, "");
    }

    /*  public ArrayList<UserData> getLoggedUsersList() {
        try {
            return (ArrayList<UserData>) ObjectSerializer.deserialize(this.sharedPrefs.getString(PREFS_SESSION_LOGGEDUSERSLIST, ObjectSerializer.serialize(new ArrayList<UserData>())));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public HashMap<String, String> getLoggedUsersCredentialsHashMap() {
        try {
            return (HashMap<String, String>) ObjectSerializer.deserialize(this.sharedPrefs.getString(PREFS_SESSION_LOGGEDUSERSCREDENTIALSLIST, ObjectSerializer.serialize(new HashMap<String, String>())));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }*/

    /**
     * Updates the saved session credentials.
     *
     * @param username the username to save.
     */


    public void updateSessionUsername(String username) {

        Editor editor = this.sharedPrefs.edit();
        if ((username != null) && (username.length() > 0)) {
            editor.putString(PREFS_SESSION_USERNAME, username);
        } else {
            editor.remove(PREFS_SESSION_USERNAME);
        }
        editor.commit();
    }

    /**
     * Updates the saved session credentials.
     *
     * @param password the password to save.
     */
    public void updateSessionPassword(String password) {

        Editor editor = this.sharedPrefs.edit();
        if ((password != null) && (password.length() > 0)) {
            editor.putString(PREFS_SESSION_PASSWORD, password);
        } else {
            editor.remove(PREFS_SESSION_PASSWORD);
        }
        editor.commit();
    }

    /**
     * Updates the saved session credentials.
     *
     * @param accessToken the AccessToken to save.
     */
    public void updateSessionProfileToken(String accessToken) {

        Editor editor = this.sharedPrefs.edit();
        if ((accessToken != null) && (accessToken.length() > 0)) {
            editor.putString(PREFS_SESSION_PROFILE_TOKEN, accessToken);
        } else {
            editor.remove(PREFS_SESSION_PROFILE_TOKEN);
        }
        editor.commit();
    }

    /**
     * Updates the saved session credentials.
     *
     * @param deviceToken the DeviceToken to save.
     */
    public void updateSessionDeviceToken(String deviceToken) {

        Editor editor = this.sharedPrefs.edit();
        if ((deviceToken != null) && (deviceToken.length() > 0)) {
            editor.putString(PREFS_SESSION_DEVICE_TOKEN, deviceToken);
        } else {
            editor.remove(PREFS_SESSION_DEVICE_TOKEN);
        }
        editor.commit();
    }

    /**
     * Updates the saved session credentials.
     *
     * @param deviceFCMToken the DeviceFCMToken to save.
     */
    public void updateSessionFCMToken(String deviceFCMToken) {

        Editor editor = this.sharedPrefs.edit();
        if ((deviceFCMToken != null) && (deviceFCMToken.length() > 0)) {
            editor.putString(PREFS_SESSION_FCM_TOKEN, deviceFCMToken);
        } else {
            editor.remove(PREFS_SESSION_FCM_TOKEN);
        }
        editor.commit();
    }

    public void updateSessionDeviceType(String deviceType) {

        Editor editor = this.sharedPrefs.edit();
        if ((deviceType != null) && (deviceType.length() > 0)) {
            editor.putString(PREFS_SESSION_DEVICE_TYPE, deviceType);
        } else {
            editor.remove(PREFS_SESSION_DEVICE_TYPE);
        }
        editor.commit();
    }


    /**
     * Updates the saved session credentials.
     *
     * @param orgId the Organization Id to save.
     */
    public void updateSessionOrgID(String orgId) {

        Editor editor = this.sharedPrefs.edit();
        if ((orgId != null) && (orgId.length() > 0)) {
            editor.putString(PREFS_SESSION_ORG_ID, orgId);
        } else {
            editor.remove(PREFS_SESSION_ORG_ID);
        }
        editor.commit();
    }

    /**
     * Updates the saved session credentials.
     *
     * @param Id the user Id to save.
     */
    public void updateSessionUserID(String Id) {

        Editor editor = this.sharedPrefs.edit();
        if ((Id != null) && (Id.length() > 0)) {
            editor.putString(PREFS_SESSION_USER_ID, Id);
        } else {
            editor.remove(PREFS_SESSION_USER_ID);
        }
        editor.commit();
    }

    public void updateSessionPreviousUserID(String Id) {

        Editor editor = this.sharedPrefs.edit();
        if ((Id != null) && (Id.length() > 0)) {
            editor.putString(PREFS_SESSION_PREVIOUS_USER_ID, Id);
        } else {
            editor.remove(PREFS_SESSION_PREVIOUS_USER_ID);
        }
        editor.commit();
    }

    /**
     * Updates the saved session credentials.
     *
     * @param fname the user First name to save.
     */
    public void updateSessionUserFirstName(String fname) {

        Editor editor = this.sharedPrefs.edit();
        if ((fname != null) && (fname.length() > 0)) {
            editor.putString(PREFS_SESSION_USER_FIRST_NAME, fname);
        } else {
            editor.remove(PREFS_SESSION_USER_FIRST_NAME);
        }
        editor.commit();
    }

    /**
     * Updates the saved session credentials.
     *
     * @param lname the user Last name to save.
     */
    public void updateSessionUserLastName(String lname) {

        Editor editor = this.sharedPrefs.edit();
        if ((lname != null) && (lname.length() > 0)) {
            editor.putString(PREFS_SESSION_USER_LAST_NAME, lname);
        } else {
            editor.remove(PREFS_SESSION_USER_LAST_NAME);
        }
        editor.commit();
    }

    public void updateSessionUserTypeId(String UserTypeId) {

        Editor editor = this.sharedPrefs.edit();
        if ((UserTypeId != null) && (UserTypeId.length() > 0)) {
            editor.putString(PREFS_SESSION_USER_TYPE_ID, UserTypeId);
        } else {
            editor.remove(PREFS_SESSION_USER_TYPE_ID);
        }
        editor.commit();
    }

    public void updateSessionUserTypeName(String UserTypeName) {

        Editor editor = this.sharedPrefs.edit();
        if ((UserTypeName != null) && (UserTypeName.length() > 0)) {
            editor.putString(PREFS_SESSION_USER_TYPE_NAME, UserTypeName);
        } else {
            editor.remove(PREFS_SESSION_USER_TYPE_NAME);
        }
        editor.commit();
    }

    public void updateSessionUserEmail(String Email) {

        Editor editor = this.sharedPrefs.edit();
        if ((Email != null) && (Email.length() > 0)) {
            editor.putString(PREFS_SESSION_USER_EMAIL, Email);
        } else {
            editor.remove(PREFS_SESSION_USER_EMAIL);
        }
        editor.commit();
    }

    public void updateSessionMobileNo(String MobileNo) {

        Editor editor = this.sharedPrefs.edit();
        if ((MobileNo != null) && (MobileNo.length() > 0)) {
            editor.putString(PREFS_SESSION_MobileNo, MobileNo);
        } else {
            editor.remove(PREFS_SESSION_MobileNo);
        }
        editor.commit();
    }


    /**
     * Updates the saved session credentials.
     *
     * @param base64String the base64String to save.
     */
    public void updateSessionProfileImageBase64(String base64String) {

        Editor editor = this.sharedPrefs.edit();
        if ((base64String != null) && (base64String.length() > 0)) {
            editor.putString(PREFS_SESSION_PROFILE_IMAGE_BASE64, base64String);
        } else {
            editor.remove(PREFS_SESSION_PROFILE_IMAGE_BASE64);
        }
        editor.commit();
    }

    public void updateSessionProfileImageUrl(String ProfileImageUrl) {

        Editor editor = this.sharedPrefs.edit();
        if ((ProfileImageUrl != null) && (ProfileImageUrl.length() > 0)) {
            editor.putString(PREFS_SESSION_PROFILE_IMAGE_Url, ProfileImageUrl);
        } else {
            editor.remove(PREFS_SESSION_PROFILE_IMAGE_Url);
        }
        editor.commit();
    }

    public void updateSessionCircle(String Circle) {

        Editor editor = this.sharedPrefs.edit();
        if ((Circle != null) && (Circle.length() > 0)) {
            editor.putString(PREFS_SESSION_User_Circle, Circle);
        } else {
            editor.remove(PREFS_SESSION_User_Circle);
        }
        editor.commit();
    }

    public void updateSessionState(String State) {

        Editor editor = this.sharedPrefs.edit();
        if ((State != null) && (State.length() > 0)) {
            editor.putString(PREFS_SESSION_User_State, State);
        } else {
            editor.remove(PREFS_SESSION_User_State);
        }
        editor.commit();
    }

    public void updateSessionSsa(String SSA) {

        Editor editor = this.sharedPrefs.edit();
        if ((SSA != null) && (SSA.length() > 0)) {
            editor.putString(PREFS_SESSION_User_Ssa, SSA);
        } else {
            editor.remove(PREFS_SESSION_User_Ssa);
        }
        editor.commit();
    }

    public void updateSessionCustomer(String Customer) {

        Editor editor = this.sharedPrefs.edit();
        if ((Customer != null) && (Customer.length() > 0)) {
            editor.putString(PREFS_SESSION_User_Customer, Customer);
        } else {
            editor.remove(PREFS_SESSION_User_Customer);
        }
        editor.commit();
    }

    public void updateSessionDesignation(String Designation) {

        Editor editor = this.sharedPrefs.edit();
        if ((Designation != null) && (Designation.length() > 0)) {
            editor.putString(PREFS_SESSION_User_Designation, Designation);
        } else {
            editor.remove(PREFS_SESSION_User_Designation);
        }
        editor.commit();
    }

    public void updateSessionUserTicketId(String id) {

        Editor editor = this.sharedPrefs.edit();
        if ((id != null) && (id.length() > 0)) {
            editor.putString(PREFS_SESSION_USER_TICKET_ID, id);
        } else {
            editor.remove(PREFS_SESSION_USER_TICKET_ID);
        }
        editor.commit();
    }

    public void updateSessionUserTicketName(String name) {

        Editor editor = this.sharedPrefs.edit();
        if ((name != null) && (name.length() > 0)) {
            editor.putString(PREFS_SESSION_USER_TICKET_NAME, name);
        } else {
            editor.remove(PREFS_SESSION_USER_TICKET_NAME);
        }
        editor.commit();
    }

    public void updateSessionUserLat(String lat) {

        Editor editor = this.sharedPrefs.edit();
        if ((lat != null) && (lat.length() > 0)) {
            editor.putString(PREFS_SESSION_USER_LAT, lat);
        } else {
            editor.remove(PREFS_SESSION_USER_LAT);
        }
        editor.commit();
    }

    public void updateSessionUserLng(String lng) {

        Editor editor = this.sharedPrefs.edit();
        if ((lng != null) && (lng.length() > 0)) {
            editor.putString(PREFS_SESSION_USER_LNG, lng);
        } else {
            editor.remove(PREFS_SESSION_USER_LNG);
        }
        editor.commit();
    }

    public void updateSessionBBTestReportCount(String count) {

        Editor editor = this.sharedPrefs.edit();
        if ((count != null) && (count.length() > 0)) {
            editor.putString(PREFS_SESSION_BB_TEST_REPORT_COUNT, count);
        } else {
            editor.remove(PREFS_SESSION_BB_TEST_REPORT_COUNT);
        }
        editor.commit();
    }

    public void updateSessionBBTestReportTime(String time) {

        Editor editor = this.sharedPrefs.edit();
        if ((time != null) && (time.length() > 0)) {
            editor.putString(PREFS_SESSION_USER_TIME, time);
        } else {
            editor.remove(PREFS_SESSION_USER_TIME);
        }
        editor.commit();
    }

    public void updateUserType(String userType) {
        Editor editor = this.sharedPrefs.edit();
        if ((userType != null) && (userType.length() > 0)) {
            editor.putString(PREFS_SESSION_USER_TYPE, userType);
        } else {
            editor.remove(PREFS_SESSION_USER_TYPE);
        }
        editor.commit();
    }

    public void updateSchoolDisplayName(String schoolDisplayName) {
        Editor editor = this.sharedPrefs.edit();
        if ((schoolDisplayName != null) && (schoolDisplayName.length() > 0)) {
            editor.putString(PREFS_SESSION_SCHOOL_DISPALY_NAME, schoolDisplayName);
        } else {
            editor.remove(PREFS_SESSION_SCHOOL_DISPALY_NAME);
        }
        editor.commit();
    }

    public void updateSchoolLoginUrl(String schoolLoginUrl) {
        Editor editor = this.sharedPrefs.edit();
        if ((schoolLoginUrl != null) && (schoolLoginUrl.length() > 0)) {
            editor.putString(PREFS_SESSION_SCHOOL_LOGIN_URL, schoolLoginUrl);
        } else {
            editor.remove(PREFS_SESSION_SCHOOL_LOGIN_URL);
        }
        editor.commit();
    }

    public void updateAccessToken(String accessToken) {
        Editor editor = this.sharedPrefs.edit();
        if ((accessToken != null) && (accessToken.length() > 0)) {
            editor.putString(PREFS_SESSION_ACCESS_TOKEN, accessToken);
        } else {
            editor.remove(PREFS_SESSION_ACCESS_TOKEN);
        }
        editor.commit();
    }

    public void updateOrganizationApiKey(String organizationApiKey) {
        Editor editor = this.sharedPrefs.edit();
        if ((organizationApiKey != null) && (organizationApiKey.length() > 0)) {
            editor.putString(PREFS_SESSION_ORGANISION_API_KEY, organizationApiKey);
        } else {
            editor.remove(PREFS_SESSION_ORGANISION_API_KEY);
        }
        editor.commit();
    }

    public void updateOrganizationSecretKey(String organizationSecretKey) {
        Editor editor = this.sharedPrefs.edit();
        if ((organizationSecretKey != null) && (organizationSecretKey.length() > 0)) {
            editor.putString(PREFS_SESSION_ORGANISION_SECRET_KEY, organizationSecretKey);
        } else {
            editor.remove(PREFS_SESSION_ORGANISION_SECRET_KEY);
        }
        editor.commit();
    }

    public void updateGoogleAccount(String googleAccount) {
        Editor editor = this.sharedPrefs.edit();
        if ((googleAccount != null) && (googleAccount.length() > 0)) {
            editor.putString(PREF_ACCOUNT_NAME, googleAccount);
        } else {
            editor.remove(PREF_ACCOUNT_NAME);
        }
        editor.commit();
    }

    public void updateIsProfilePicUpdated(String isProfilePicUpdateStatus) {
        Editor editor = this.sharedPrefs.edit();
        if ((isProfilePicUpdateStatus != null) && (isProfilePicUpdateStatus.length() > 0)) {
            editor.putString(PREF_IS_PROFIL_PIC_UPDATE, isProfilePicUpdateStatus);
        } else {
            editor.remove(PREF_IS_PROFIL_PIC_UPDATE);
        }
        editor.commit();
    }


    public void updateIsPasswordSkip(String isPasswordSkip) {
        Editor editor = this.sharedPrefs.edit();
        if ((isPasswordSkip != null) && (isPasswordSkip.length() > 0)) {
            editor.putString(PREF_IS_PASSWORD_SKIP, isPasswordSkip);
        } else {
            editor.remove(PREF_IS_PASSWORD_SKIP);
        }
        editor.commit();
    }

    /**
     * Updates the saved session credentials.
     *
     * @param username the username to save.
     * @param password the password to save.
     */
    public void updateSessionCredentials(String username, String password, String profileToken) {

        Editor editor = this.sharedPrefs.edit();
        if ((username != null) && (username.length() > 0)) {
            editor.putString(PREFS_SESSION_USERNAME, username);
        } else {
            editor.remove(PREFS_SESSION_USERNAME);
        }

        if ((password != null) && (password.length() > 0)) {
            editor.putString(PREFS_SESSION_PASSWORD, password);
        } else {
            editor.remove(PREFS_SESSION_PASSWORD);
        }

        if ((profileToken != null) && (profileToken.length() > 0)) {
            editor.putString(PREFS_SESSION_PROFILE_TOKEN, profileToken);
        } else {
            editor.remove(PREFS_SESSION_PROFILE_TOKEN);
        }

        editor.commit();
    }

   /* public void updateLoggedUserData(UserData loggedUserData) {
        Editor editor = this.sharedPrefs.edit();
        try {
            if ((loggedUserData != null)) {
                editor.putString(PREFS_SESSION_LOGGED_USERDATA, ObjectSerializer.serialize(loggedUserData));
            } else {
                editor.remove(PREFS_SESSION_LOGGED_USERDATA);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();
    }


    public void updateLoggedUserList(ArrayList<UserData> loggedUserList) {
        Editor editor = this.sharedPrefs.edit();
        try {
            if ((loggedUserList != null) && (loggedUserList.size() > 0)) {
                editor.putString(PREFS_SESSION_LOGGEDUSERSLIST, ObjectSerializer.serialize(loggedUserList));
            } else {
                editor.remove(PREFS_SESSION_LOGGEDUSERSLIST);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();
    }

    public void updateLoggedUsersCredentials(HashMap<String, String> usercredentialHashMap) {
        Editor editor = this.sharedPrefs.edit();
        try {
            if ((usercredentialHashMap != null) && (usercredentialHashMap.size() > 0)) {
                editor.putString(PREFS_SESSION_LOGGEDUSERSCREDENTIALSLIST, ObjectSerializer.serialize(usercredentialHashMap));
            } else {
                editor.remove(PREFS_SESSION_LOGGEDUSERSCREDENTIALSLIST);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();
    }*/

    public void addUpdateSkipWalkthroughPref(boolean flag) {
        Editor editor = this.sharedPrefs.edit();
        editor.putBoolean(PREF_SESSION_WALKTHROUGH_SKIP_KEY, flag);
        editor.commit();
    }

    public boolean getWalkthroughSkipValue() {
        return this.sharedPrefs.getBoolean(PREF_SESSION_WALKTHROUGH_SKIP_KEY, false);
    }

    public void updateAppUpdatesPref(boolean flag) {
        Editor editor = this.sharedPrefs.edit();
        editor.putBoolean(PREF_SESSION_APPUPDATE_SKIP_KEY, flag);
        editor.commit();
    }

    public boolean getAppUpdatesPrefValue() {
        return this.sharedPrefs.getBoolean(PREF_SESSION_APPUPDATE_SKIP_KEY, false);
    }

    /**
     * Clears stored session credentials if any.
     */
    public void clearSessionCredentials() {

        this.updateSessionPassword(null);
        this.updateSessionOrgID(null);
        this.updateSessionProfileToken(null);
        this.updateSessionDeviceToken(null);

        this.updateSessionUsername(null);
        this.updateSessionPassword(null);
        this.updateSessionProfileToken(null);
        this.updateSessionDeviceToken(null);
        this.updateSessionDeviceType(null);
        this.updateSessionOrgID(null);
        this.updateSessionUserID(null);
        this.updateSessionUserFirstName(null);
        this.updateSessionUserLastName(null);
        this.updateSessionProfileImageBase64(null);
        this.updateSessionCredentials(null, null, null);
        this.updateSessionUserTicketId(null);
        this.updateSessionUserTicketName(null);
        this.updateSessionState(null);
        this.updateSessionSsa(null);
        this.updateSessionCustomer(null);
        this.updateAccessToken(null);
        this.updateSessionUserEmail(null);
        this.updateUserType(null);
        this.updateSchoolDisplayName(null);
        this.updateSessionProfileImageUrl(null);
        this.updateSchoolLoginUrl(null);
        this.updateOrganizationApiKey(null);
        this.updateOrganizationSecretKey(null);
        this.updateGoogleAccount(null);

    }
}
