package com.edbrix.connectbrix.activities;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
/*import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;*/
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.applandeo.materialcalendarview.listeners.OnNavigationButtonClickListener;
import com.edbrix.connectbrix.Application;
import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.adapters.SchoolListWithCalendarAdapter;
import com.edbrix.connectbrix.baseclass.BaseActivity;
import com.edbrix.connectbrix.commons.AlertDialogManager;
import com.edbrix.connectbrix.data.GetMeetingsByMonthYearResposeData;
import com.edbrix.connectbrix.data.GetThreeMonthsMeetingListData;
import com.edbrix.connectbrix.data.GetThreeMonthsMeetingParentData;
import com.edbrix.connectbrix.data.MeetingListData;
import com.edbrix.connectbrix.data.MyEventDay;
import com.edbrix.connectbrix.data.UserData;
import com.edbrix.connectbrix.data.UserMeetingByDateParentData;
import com.edbrix.connectbrix.data.UserMeetingListResponseData;
import com.edbrix.connectbrix.utils.Constants;
import com.edbrix.connectbrix.utils.SessionManager;
import com.edbrix.connectbrix.volley.GsonRequest;
import com.edbrix.connectbrix.volley.SettingsMy;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import pub.devrel.easypermissions.EasyPermissions;

public class CalenderViewMeetingListActivity extends BaseActivity {

    private static final String TAG = CalenderViewMeetingListActivity.class.getName();
    private LinearLayout mLinearLayoutCal;
    private AlertDialogManager alertDialogManager;
    private CalendarView mCalendarView;
    private ListView mMeetingListWithCalender;
    private TextView mTxtSelectedDate;
    private TextView txtDataFound;
    private ImageView meetingListImg;
    private FloatingActionButton mFloatingActionButtonFabWithListview;
    SchoolListWithCalendarAdapter schoolListWithCalendarAdapter;
    SessionManager sessionManager;
    ArrayList<String> daysForEvent = new ArrayList<>();
    private List<EventDay> mEventDays = new ArrayList<>();
    SimpleDateFormat finalSimpleDateFormat;
    private GetMeetingsByMonthYearResposeData meetingListData;
    private UserMeetingByDateParentData userMeetingByDateParentData;
    String dateForGetEvents;
    ArrayList<UserMeetingListResponseData> userMeetingListResponseData;
    List<GetThreeMonthsMeetingListData> getThreeMonthsMeetingListData;

    public static final int REFRESH_DATA = 1;
    String RefreshFlag = "";

    //google calendar
    GoogleAccountCredential mCredential;
    private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY, CalendarScopes.CALENDAR};
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    final public int CHECK_PERMISSIONS = 123;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    String userTimeZon = "";
    int size = 0;
    int counter = 0;
    MenuItem menuItem;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender_view_meeting_list);
        getSupportActionBar().setTitle("Meetings");

        sessionManager = new SessionManager(this);
        alertDialogManager = new AlertDialogManager(CalenderViewMeetingListActivity.this);
        assignViews();
        registerEventReceiver();

        getThreeMonthsMeeting();

        meetingListData = new GetMeetingsByMonthYearResposeData();
        userMeetingListResponseData = new ArrayList<>();
        userMeetingByDateParentData = new UserMeetingByDateParentData();
        getThreeMonthsMeetingListData = new ArrayList<GetThreeMonthsMeetingListData>();
        finalSimpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy");

        clickListner();

        if (savedInstanceState != null) {
            meetingListData.setMeetingDates((ArrayList<String>) savedInstanceState.getSerializable("meetingDates"));
            if (meetingListData.getMeetingDates() != null && meetingListData.getMeetingDates().size() > 0) {

                for (int i = 0; i < meetingListData.getMeetingDates().size(); i++) {
                    addEventToCalendar(meetingListData.getMeetingDates().get(i));
                }
            }
            mTxtSelectedDate.setText(savedInstanceState.getString("selectedDate"));

            userMeetingByDateParentData.setMeetings((List<UserMeetingListResponseData>) savedInstanceState.getSerializable("meetingList"));
            if (userMeetingByDateParentData.getMeetings() != null && userMeetingByDateParentData.getMeetings().size() > 0) {
                txtDataFound.setVisibility(View.GONE);
                meetingListImg.setVisibility(View.GONE);
                mMeetingListWithCalender.setVisibility(View.VISIBLE);
                userMeetingListResponseData = new ArrayList<>();

                userMeetingListResponseData.addAll(userMeetingByDateParentData.getMeetings());
                schoolListWithCalendarAdapter = new SchoolListWithCalendarAdapter(CalenderViewMeetingListActivity.this, userMeetingListResponseData);
                mMeetingListWithCalender.setAdapter(schoolListWithCalendarAdapter);
            } else {
                mMeetingListWithCalender.setVisibility(View.GONE);
                txtDataFound.setVisibility(View.VISIBLE);
                meetingListImg.setVisibility(View.VISIBLE);
            }
        } else {

            try {

                Date date = new Date();
                mTxtSelectedDate.setText(finalSimpleDateFormat.format(date));
                SimpleDateFormat tempSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date tempDate = null;
                Date getMonthYear = finalSimpleDateFormat.parse(mTxtSelectedDate.getText().toString());

                String getMonthYear_str_date = tempSimpleDateFormat.format(getMonthYear);
                String[] temp_date_str = getMonthYear_str_date.split("-");
                prepareMeetingByMonthYear(temp_date_str[0], temp_date_str[1]);


                tempDate = finalSimpleDateFormat.parse(mTxtSelectedDate.getText().toString());
                dateForGetEvents = tempSimpleDateFormat.format(tempDate);
                prepareMeetingListByDate(dateForGetEvents);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

    private void clickListner() {

        mMeetingListWithCalender.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CalenderViewMeetingListActivity.this, MeetingDetailsActivity.class);
                intent.putExtra("meetingDbId", userMeetingListResponseData.get(position).getId());
                intent.putExtra("IsHost", userMeetingListResponseData.get(position).getIsHost());
                intent.putExtra("isAvailable", userMeetingListResponseData.get(position).getIsAvailable());
                intent.putExtra("IsHost", userMeetingListResponseData.get(position).getIsHost());
                intent.putExtra("IsCalenderActivity", "YC");
                intent.putExtra("RefreshFlag", "N");
                //startActivity(intent);
                startActivityForResult(intent, REFRESH_DATA);
            }
        });

        mFloatingActionButtonFabWithListview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalenderViewMeetingListActivity.this, CreateMeetingActivity.class);
                intent.putExtra("comesFor", "new");
                intent.putExtra("isAvailable", "0");
                intent.putExtra("IsCalenderActivity", "Y");
                startActivity(intent);
            }
        });

        mCalendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                try {
                    MyEventDay myEventDay = (MyEventDay) eventDay;
                    Calendar cal = myEventDay.getCalendar();
                    Date date = cal.getTime();
                    mTxtSelectedDate.setText(finalSimpleDateFormat.format(date));
                    SimpleDateFormat tempSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date tempDate = finalSimpleDateFormat.parse(mTxtSelectedDate.getText().toString());
                    dateForGetEvents = tempSimpleDateFormat.format(tempDate);
                    prepareMeetingListByDate(dateForGetEvents);

                } catch (Exception e) {
                    e.printStackTrace();
                    Calendar cal1 = mCalendarView.getSelectedDate();
                    Date date = cal1.getTime();
                    mTxtSelectedDate.setText(finalSimpleDateFormat.format(date));
                    txtDataFound.setVisibility(View.VISIBLE);
                    meetingListImg.setVisibility(View.VISIBLE);
                    mMeetingListWithCalender.setVisibility(View.GONE);
                  /*  menuItem = menu.findItem(R.id.menuGoogle);
                    menuItem.setEnabled(false);*/
                }
            }
        });


        mCalendarView.setOnForwardButtonClickListener(new OnNavigationButtonClickListener() {
            @Override
            public void onClick() {
                Calendar calendar = mCalendarView.getCurrentPageDate();
                Date date = calendar.getTime();
                SimpleDateFormat tempSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String forwardPageDate = tempSimpleDateFormat.format(date);

                String[] splitString = forwardPageDate.split("-");
                prepareMeetingByMonthYear(splitString[0], splitString[1]);
            }
        });

        mCalendarView.setOnPreviousButtonClickListener(new OnNavigationButtonClickListener() {
            @Override
            public void onClick() {
                Calendar calendar = mCalendarView.getCurrentPageDate();
                Date date = calendar.getTime();
                SimpleDateFormat tempSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String forwardPageDate = tempSimpleDateFormat.format(date);

                String[] splitString = forwardPageDate.split("-");
                prepareMeetingByMonthYear(splitString[0], splitString[1]);
            }
        });
    }

    private void addEventToCalendar(String date) {

        int count = 0;
        try {
            SimpleDateFormat convertDateTime = new SimpleDateFormat("MM/dd/yyyy");
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date dateTime = convertDateTime.parse(date);
            String tempDate = sdf.format(dateTime);

            Date dateForEvent = sdf.parse(tempDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateForEvent);
            MyEventDay myEventDay1 = new MyEventDay(cal, R.drawable.circle_24, "Note " + count++);
            mCalendarView.setDate(myEventDay1.getCalendar());
            mEventDays.add(myEventDay1);
            mCalendarView.setEvents(mEventDays);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void assignViews() {
        mLinearLayoutCal = (LinearLayout) findViewById(R.id.linearLayoutCal);
        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        mMeetingListWithCalender = (ListView) findViewById(R.id.meetingListWithCalender);
        mFloatingActionButtonFabWithListview = (FloatingActionButton) findViewById(R.id.float_btn);
        mTxtSelectedDate = (TextView) findViewById(R.id.txtSelectedDate);
        txtDataFound = (TextView) findViewById(R.id.txtDataFound);
        meetingListImg = (ImageView) findViewById(R.id.meetingListImg);
    }

    private void setSchoolListWithCalendarAdapter(ArrayList<String> date) {
        /*schoolListWithCalendarAdapter = new SchoolListWithCalendarAdapter(CalenderViewMeetingListActivity.this, date);
        mMeetingListWithCalender.setAdapter(schoolListWithCalendarAdapter);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
           /* case android.R.id.menuGoogle:
                startActivity(new Intent(this, UserProfileActivity.class));
                return true;*/
            case R.id.menuCalender:
                startActivity(new Intent(this, SchoolListActivity.class));
                //onBackPressed();
                return true;
            case R.id.menuGoogle:

                alertDialogManager.Dialog("Confirmation", "Continue with Sync to Google Calendar?", "ok", "cancel", new AlertDialogManager.onTwoButtonClickListner() {
                    @Override
                    public void onPositiveClick() {
                        if (checkPermission() == true) {
                            mCredential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(SCOPES))
                                    .setBackOff(new ExponentialBackOff());
                            getResultsFromApi();
                        }
                    }

                    @Override
                    public void onNegativeClick() {
                    }
                }).show();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.calender_view_menu, menu);
        menuItem = menu.findItem(R.id.menuGoogle);
       /* if(userMeetingListResponseData.size()>0 || !userMeetingListResponseData.isEmpty()){
            menuItem.setVisible(true);
        }else{
            menuItem.setVisible(false);
        }*/
        return true;

    }


    private void prepareMeetingListByDate(String dateForGetEvents) {

        try {

            showBusyProgress();
            JSONObject jo = new JSONObject();
            jo.put("AccessToken", sessionManager.getPrefsSessionAccessToken());
            jo.put("UserId", sessionManager.getSessionUserId());
            jo.put("MeetingDate", dateForGetEvents);

            GsonRequest<UserMeetingByDateParentData> getMeetingByDateRequest = new GsonRequest<>(Request.Method.POST, Constants.getMeetingByDate, jo.toString(), UserMeetingByDateParentData.class,
                    new Response.Listener<UserMeetingByDateParentData>() {
                        @Override
                        public void onResponse(@NonNull UserMeetingByDateParentData response) {
                            hideBusyProgress();
                            if (response.getError() != null) {
                                showToast(response.getError().getErrorMessage());
                            } else {
                                if (response.getSuccess() == 1) {

                                    userMeetingByDateParentData = response;
                                    if (userMeetingByDateParentData.getMeetings() != null && userMeetingByDateParentData.getMeetings().size() > 0) {
                                        txtDataFound.setVisibility(View.GONE);
                                        meetingListImg.setVisibility(View.GONE);
                                        /* menuItem.setVisible(true);*/
                                        mMeetingListWithCalender.setVisibility(View.VISIBLE);
                                        userMeetingListResponseData = new ArrayList<>();

                                        userMeetingListResponseData.addAll(userMeetingByDateParentData.getMeetings());
                                        schoolListWithCalendarAdapter = new SchoolListWithCalendarAdapter(CalenderViewMeetingListActivity.this, userMeetingListResponseData);
                                        mMeetingListWithCalender.setAdapter(schoolListWithCalendarAdapter);
                                    } else {
                                        mMeetingListWithCalender.setVisibility(View.GONE);
                                        txtDataFound.setVisibility(View.VISIBLE);
                                        meetingListImg.setVisibility(View.VISIBLE);
                                        /* menuItem.setVisible(false);*/
                                    }
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideBusyProgress();
                    Log.e(TAG, error.getMessage());
                }
            });
            getMeetingByDateRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            getMeetingByDateRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(getMeetingByDateRequest, "getMeetingByDateRequest");

        } catch (Exception e) {
            hideBusyProgress();
            showToast("Something went wrong. Please try again later.");
        }
    }


    private void prepareMeetingByMonthYear(String year, String month) {


        try {
            JSONObject jo = new JSONObject();
            /*jo.put("APIKEY", sessionManager.getPrefsOrganizationApiKey());
            jo.put("SECRETKEY", sessionManager.getPrefsOrganizationSecretKey());*/
            jo.put("AccessToken", sessionManager.getPrefsSessionAccessToken());
            jo.put("UserId", sessionManager.getSessionUserId());
            jo.put("Year", year);
            jo.put("Month", month);

            GsonRequest<GetMeetingsByMonthYearResposeData> getAssignAvailabilityLearnersListRequest = new GsonRequest<>(Request.Method.POST, Constants.getAvailableMeetingDates, jo.toString(), GetMeetingsByMonthYearResposeData.class,
                    new Response.Listener<GetMeetingsByMonthYearResposeData>() {
                        @Override
                        public void onResponse(@NonNull GetMeetingsByMonthYearResposeData response) {
                            hideBusyProgress();
                            if (response.getError() != null) {
                                showToast(response.getError().getErrorMessage());
                            } else {
                                if (response.getSuccess() == 1) {

                                    meetingListData = response;
                                    if (meetingListData.getMeetingDates() != null && meetingListData.getMeetingDates().size() > 0) {

                                        for (int i = 0; i < meetingListData.getMeetingDates().size(); i++) {
                                            /*daysForEvent.add(meetingListData.getUserMeetingsDates().get(i).getDate());*/
                                            addEventToCalendar(meetingListData.getMeetingDates().get(i));
                                        }

                                    }
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideBusyProgress();
                    Log.e(TAG, error.getMessage());
                }
            });
            getAssignAvailabilityLearnersListRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            getAssignAvailabilityLearnersListRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(getAssignAvailabilityLearnersListRequest, "MeetingListData");

        } catch (Exception e) {
            hideBusyProgress();
            showToast("Something went wrong. Please try again later.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        invalidateOptionsMenu();

        if (requestCode == 1) {
            try {
                finalSimpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy");
                Date date = new Date();
                mTxtSelectedDate.setText(finalSimpleDateFormat.format(date));
                SimpleDateFormat tempSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date getMonthYear = finalSimpleDateFormat.parse(mTxtSelectedDate.getText().toString());
                Date tempDate = null;

                String getMonthYear_str_date = tempSimpleDateFormat.format(getMonthYear);
                String[] temp_date_str = getMonthYear_str_date.split("-");
                prepareMeetingByMonthYear(temp_date_str[0], temp_date_str[1]);

                tempDate = finalSimpleDateFormat.parse(mTxtSelectedDate.getText().toString());
                dateForGetEvents = tempSimpleDateFormat.format(tempDate);
                prepareMeetingListByDate(dateForGetEvents);

            } catch (Exception ex) {

            }
            //prepareListData();
            //showToast("Calling in Activity Result");
        }
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    showToast("This app requires Google Play Services. Please install " +
                            "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        sessionManager.updateGoogleAccount(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }

    }

    final String eventName = "com.edbrix.connectbrix.CALEN";

    private void registerEventReceiver() {
        IntentFilter eventFilter = new IntentFilter();
        eventFilter.addAction(eventName);
        registerReceiver(eventReceiver1, eventFilter);
    }

    private BroadcastReceiver eventReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            RefreshFlag = intent.getStringExtra("RefreshFlag");
            if (RefreshFlag.equals("Y")) {
                try {
                    finalSimpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy");
                    Date date = new Date();
                    mTxtSelectedDate.setText(finalSimpleDateFormat.format(date));
                    SimpleDateFormat tempSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date getMonthYear = finalSimpleDateFormat.parse(mTxtSelectedDate.getText().toString());
                    Date tempDate = null;

                    String getMonthYear_str_date = tempSimpleDateFormat.format(getMonthYear);
                    String[] temp_date_str = getMonthYear_str_date.split("-");
                    prepareMeetingByMonthYear(temp_date_str[0], temp_date_str[1]);

                    tempDate = finalSimpleDateFormat.parse(mTxtSelectedDate.getText().toString());
                    dateForGetEvents = tempSimpleDateFormat.format(tempDate);
                    prepareMeetingListByDate(dateForGetEvents);

                } catch (Exception ex) {
                    Log.e("Exception", ex.getMessage());
                }
            }
            //This code will be executed when the broadcast in activity B is launched
        }
    };


    @Override
    public void onBackPressed() {

        if (RefreshFlag.equals("Y")) {

            Intent resultIntent = new Intent();
            // TODO Add extras or a data URI to this intent as appropriate.
            resultIntent.putExtra("RefreshFlag", "Y");
            setResult(Activity.RESULT_OK, resultIntent);
            finish();

        } else {
            finish();
        }
    }


    /*@Override
    protected void onStop() {
        unregisterReceiver(eventReceiver1);
        super.onStop();
    }

    @Override
    protected void onResume() {
        registerEventReceiver();
        super.onResume();
    }*/

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("meetingDates", meetingListData.getMeetingDates());
        outState.putSerializable("meetingList", userMeetingListResponseData);
        outState.putString("selectedDate", mTxtSelectedDate.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        meetingListData.setMeetingDates((ArrayList<String>) savedInstanceState.getSerializable("meetingDates"));
        if (meetingListData.getMeetingDates() != null && meetingListData.getMeetingDates().size() > 0) {

            for (int i = 0; i < meetingListData.getMeetingDates().size(); i++) {
                addEventToCalendar(meetingListData.getMeetingDates().get(i));
            }
        }
        mTxtSelectedDate.setText(savedInstanceState.getString("selectedDate"));

        userMeetingByDateParentData.setMeetings((List<UserMeetingListResponseData>) savedInstanceState.getSerializable("meetingList"));
        if (userMeetingByDateParentData.getMeetings() != null && userMeetingByDateParentData.getMeetings().size() > 0) {
            txtDataFound.setVisibility(View.GONE);
            meetingListImg.setVisibility(View.GONE);
            mMeetingListWithCalender.setVisibility(View.VISIBLE);
            userMeetingListResponseData = new ArrayList<>();

            userMeetingListResponseData.addAll(userMeetingByDateParentData.getMeetings());
            schoolListWithCalendarAdapter = new SchoolListWithCalendarAdapter(CalenderViewMeetingListActivity.this, userMeetingListResponseData);
            mMeetingListWithCalender.setAdapter(schoolListWithCalendarAdapter);
        } else {
            mMeetingListWithCalender.setVisibility(View.GONE);
            txtDataFound.setVisibility(View.VISIBLE);
            meetingListImg.setVisibility(View.VISIBLE);
        }
    }


    //------------------------------------- Sync Meetings With Google Calendar-----------------------------------//

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(CalenderViewMeetingListActivity.this, Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(CalenderViewMeetingListActivity.this,
                    new String[]{Manifest.permission.GET_ACCOUNTS},
                    CHECK_PERMISSIONS);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CHECK_PERMISSIONS: {
                //boolean isPerpermissionForAllGranted = false;
                if (grantResults.length > 0) {
                    boolean GET_ACCOUNTS = grantResults[0] == PackageManager.PERMISSION_GRANTED;


                    if (GET_ACCOUNTS) {
                        //Toast.makeText(LoginActivity.this,"all permission granted",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(CalenderViewMeetingListActivity.this, "Google account permission not granted", Toast.LENGTH_LONG).show();
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                        return;
                    }
                }
                break;
            }
        }
    }

    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            /* mOutputText.setText("No network connection available.");*/
            showToast("No network connection available.");
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                CalenderViewMeetingListActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private void chooseAccount() {
        // to do clear mCredential and shared perferance when logout
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null);
            /*if (accountName != null)
            {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
            }*/
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                //startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);

                accountName = sessionManager.getGoogleAccount();
                if (!accountName.isEmpty()) {
                    mCredential.setSelectedAccountName(accountName);
                    getResultsFromApi();
                } else {
                    // Start a dialog from which the user can choose an account
                    startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
                }
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private com.google.api.services.calendar.Calendar mService = null;


    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {

        private Exception mLastError = null;
        private boolean FLAG = false;

        public MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Calendar API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                getDataFromApi();
            } catch (Exception e) {
                e.printStackTrace();
                mLastError = e;
                cancel(true);
                return null;
            }
            return null;
        }

        private void getDataFromApi() throws IOException {
            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());
            List<String> eventStrings = new ArrayList<String>();
            Events events = mService.events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();


        }

        @Override
        protected void onCancelled() {
            //mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            SchoolListActivity.REQUEST_AUTHORIZATION);
                } else {
                    // mOutputText.setText("The following error occurred:\n"+ mLastError.getMessage());
                }
            } else {
                //mOutputText.setText("Request cancelled.");
            }
        }

        @Override
        protected void onPreExecute() {//009
            showBusyProgress();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            hideBusyProgress();

            size = getThreeMonthsMeetingListData.size() - 1;
            if (getThreeMonthsMeetingListData.size() > 0) {

                //for (int i = 0; i < getThreeMonthsMeetingListData.size(); i++) {

                String userDate = getThreeMonthsMeetingListData.get(0).getStartDateTime();
                SimpleDateFormat convertDateTime = new SimpleDateFormat("dd/MMM/yyyy hh:mm a");
                DateTime start = null;
                DateTime end = null;
                try {
                    Date dateOfMeeting = convertDateTime.parse(userDate);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    String tempDate = simpleDateFormat.format(dateOfMeeting);
                    Date dateTime = simpleDateFormat.parse(tempDate);
                    start = new DateTime(dateTime);
                    end = new DateTime(dateTime);
                    Log.d("Date", dateOfMeeting.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                createEventAsync(getThreeMonthsMeetingListData.get(0).getTitle(), "", getThreeMonthsMeetingListData.get(0).getAgenda(), start, end, null);
                //}
                //showToast("Sync Success");

            } else {
                showToast("No meetings available.");
            }
        }

    }

    public void createEventAsync(final String summary, final String location, final String des, final DateTime startDate, final DateTime endDate, final EventAttendee[]
            eventAttendees) {

        new AsyncTask<Void, Void, String>() {
            private com.google.api.services.calendar.Calendar mService = null;
            private Exception mLastError = null;
            private boolean FLAG = false;


            @Override
            protected String doInBackground(Void... voids) {
                try {
                    insertEvent(summary, location, des, startDate, endDate, new EventAttendee[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (counter == size) {
                    counter = 0;
                    hideBusyProgress();
                    showToast("Meetings sync to Google Calendar successfully.");
                } else {
                    //showBusyProgress();
                    //getThreeMonthsMeetingListData.get(counter);
                    counter++;
                    String userDate = getThreeMonthsMeetingListData.get(counter).getStartDateTime();
                    SimpleDateFormat convertDateTime = new SimpleDateFormat("dd/MMM/yyyy hh:mm a");
                    DateTime start = null;
                    DateTime end = null;
                    try {
                        Date dateOfMeeting = convertDateTime.parse(userDate);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                        String tempDate = simpleDateFormat.format(dateOfMeeting);
                        Date dateTime = simpleDateFormat.parse(tempDate);
                        start = new DateTime(dateTime);
                        end = new DateTime(dateTime);
                        Log.d("Date", dateOfMeeting.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    createEventAsync(getThreeMonthsMeetingListData.get(counter).getTitle(), "", getThreeMonthsMeetingListData.get(counter).getAgenda(), start, end, null);

                }

                //getResultsFromApi();
            }
        }.execute();
    }

    void insertEvent(String summary, String location, String des, DateTime startDate, DateTime endDate, EventAttendee[] eventAttendees) throws IOException {

        Log.d(SchoolListActivity.class.getName(), TimeZone.getDefault().getID());
        Event event = new Event()
                .setSummary(summary)
                .setLocation(location)
                .setDescription(des);

        EventDateTime start = new EventDateTime()
                .setDateTime(startDate)
                .setTimeZone(TimeZone.getDefault().getID());
        event.setStart(start);

        EventDateTime end = new EventDateTime()
                .setDateTime(endDate)
                .setTimeZone(TimeZone.getDefault().getID());
        event.setEnd(end);

        String[] recurrence = new String[]{"RRULE:FREQ=DAILY;COUNT=1"};
        event.setRecurrence(Arrays.asList(recurrence));


        event.setAttendees(Arrays.asList(eventAttendees));

        EventReminder[] reminderOverrides = new EventReminder[]{
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        String calendarId = "primary";
        //event.send
        if (mService != null) {
            mService.events().insert(calendarId, event).setSendNotifications(true).execute();
            /*try{
                mService.events().insert(calendarId, event).setSendNotifications(true).execute();
            }catch (UserRecoverableAuthIOException e) {
                startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            }*/
        }

    }

    private void getThreeMonthsMeeting() {
        try {
            /*showBusyProgress();*/
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("UserId", sessionManager.getSessionUserId());
            jsonObject.put("AccessToken", sessionManager.getPrefsSessionAccessToken());

            GsonRequest<GetThreeMonthsMeetingParentData> getThreeMonthsMeetingParentDataGsonRequest = new GsonRequest<>(Request.Method.POST, Constants.getthreemonthsmeeting, jsonObject.toString(), GetThreeMonthsMeetingParentData.class,
                    new Response.Listener<GetThreeMonthsMeetingParentData>() {
                        @Override
                        public void onResponse(@NonNull GetThreeMonthsMeetingParentData response) {
                            /* hideBusyProgress();*/
                            if (response.getError() != null) {
                                String error = response.getError().getErrorMessage();
                                showToast(error);
                            } else {

                                if (response.getSuccess() == 1) {
                                    getThreeMonthsMeetingListData = new ArrayList<GetThreeMonthsMeetingListData>();
                                    getThreeMonthsMeetingListData.addAll(response.getMeetings());
                                }
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideBusyProgress();
                    showToast(SettingsMy.getErrorMessage(error));
                }
            });
            getThreeMonthsMeetingParentDataGsonRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            getThreeMonthsMeetingParentDataGsonRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(getThreeMonthsMeetingParentDataGsonRequest, "getThreeMonthsMeetingParentDataGsonRequest");

        } catch (Exception e) {
            /*hideBusyProgress();*/
            Log.e("Exception", e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getThreeMonthsMeeting();
    }
}
