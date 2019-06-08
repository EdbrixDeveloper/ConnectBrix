package com.edbrix.connectbrix.utils;

public interface AuthConstants {

    /**
     *    ========== Disclaimer ==========
     *
     *    Please be aware that all hard-coded variables and constants
     *    shown in the documentation and in the demo, such as Zoom Token,
     *    Zoom Access, Token, etc., are ONLY FOR DEMO AND TESTING PURPOSES.
     *    We STRONGLY DISCOURAGE the way of HARDCODING any Zoom Credentials
     *    (username, password, API Keys & secrets, SDK keys & secrets, etc.)
     *    or any Personal Identifiable Information (PII) inside your application.
     *    WE DON’T MAKE ANY COMMITMENTS ABOUT ANY LOSS CAUSED BY HARD-CODING CREDENTIALS
     *    OR SENSITIVE INFORMATION INSIDE YOUR APP WHEN DEVELOPING WITH OUR SDK.
     *
     */
    // TODO Change it to your web domain
    public final static String WEB_DOMAIN = "zoom.us";

    // Change it to your APP Key
    public final static String APP_KEY = "qjDDhSsOzp5Ln0WSP0Z0LoKo86XFR4S2UIUn";

    // Change it to your APP Secret
    public final static String APP_SECRET = "ePR5WENlisNzQVRJ8vrVeG0UGUsPza2iQ3xL";

    // ==============
    /* If you would like to ask your user to login with their own credentials, you don't need to fill up the following. */

    // Change it to your user ID
    public final static String USER_ID = "Your user ID from REST API";

    // Change it to your zoom token
    public final static String ZOOM_TOKEN = "Your zoom token from REST API";

    // Change it to your zoom access token(ZAK)
    public final static String ZOOM_ACCESS_TOKEN = "Your zoom access token(ZAK) from REST API";
}
