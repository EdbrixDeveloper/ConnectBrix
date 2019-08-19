package com.edbrix.connectbrix.activities;

import android.Manifest;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.edbrix.connectbrix.Application;
import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.adapters.SchoolExpListAdapter;
import com.edbrix.connectbrix.baseclass.BaseActivity;
import com.edbrix.connectbrix.commons.AlertDialogManager;
import com.edbrix.connectbrix.data.GetThreeMonthsMeetingListData;
import com.edbrix.connectbrix.data.GetThreeMonthsMeetingParentData;
import com.edbrix.connectbrix.data.MeetingListData;
import com.edbrix.connectbrix.data.UserMeeting;
import com.edbrix.connectbrix.data.UserMeetingsDate;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import pub.devrel.easypermissions.EasyPermissions;


public class SchoolListActivity extends BaseActivity {

    private SchoolExpListAdapter pmAcExpListAdapter;
    public ExpandableListView schoolList_listView_schoolList;
    private AlertDialogManager alertDialogManager;
    private TextView txtDataFound;
    private ImageView meetingListImg;
    private TextView requestMeetingListCount;
    private ImageView imgCalender;
    private ImageView imgUserProfile;
    private ImageView imgSearch;
    private ImageView cancelSearch;
    private ImageView googlePlusMenu;
    private LinearLayout linearLayoutCircular;
    private LinearLayout actionBarLayout2;
    private FloatingActionButton requestedMeetingButton;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private TextInputLayout mInputLayoutSearch;
    private EditText mInputSearch;
    private boolean searchFlag = false;

    public static final int RESULT_UPDATE_PROFILE = 200;

    public static final int REFRESH_DATA = 1;
    String userTimeZon = "";
    int size = 0;
    int counter = 0;


    FloatingActionButton floating_action_button_fab_with_listview;
    boolean doubleBackToExitPressedOnce = false;
    SessionManager sessionManager;
    private MeetingListData meetingListData;


    /*private MyContinousAsyncTask myContinouslyRunningAsyncTask;*/

    // Listview Pagingnation Purpose
    ArrayList<UserMeetingsDate> userMeetingsDateList;
    private int requestCount = 0;
    private boolean loading = true;
    private int refreshCnt = 0;
    private SchoolExpListAdapter.OnChildItemClickActionListener onChildItemClickActionListener;
    String y_str = "";
    private int currentVisibleItemInListView = 0;
    Intent intent;
    boolean isHitOnActivityResult = false;

    private int MeetingRequestCount = 0;
    private int MeetingRequestCountAll = 0;

    //google calendar
    GoogleAccountCredential mCredential;
    private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY, CalendarScopes.CALENDAR};
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    final public int CHECK_PERMISSIONS = 123;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    ProgressDialog mProgress;

    List<GetThreeMonthsMeetingListData> getThreeMonthsMeetingListData;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(SchoolListActivity.this);
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Please wait while syncing with Google Calendar..");
        y_str = "N";
        intent = getIntent();

        if (!validateUser()) {
            finish();
            startActivity(new Intent(SchoolListActivity.this, LoginActivity.class));
        } else {
            setContentView(R.layout.activity_school_list);

            mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
            mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
            imgCalender = (ImageView) findViewById(R.id.calender);
            imgUserProfile = (ImageView) findViewById(R.id.imgUserProfile);
            imgSearch = (ImageView) findViewById(R.id.search);
            cancelSearch = (ImageView) findViewById(R.id.cancelSearch);
            googlePlusMenu = (ImageView) findViewById(R.id.googlePlusMenu);
            linearLayoutCircular = (LinearLayout) findViewById(R.id.linearLayoutCircular);
            actionBarLayout2 = (LinearLayout) findViewById(R.id.actionBarLayout2);
            requestedMeetingButton = (FloatingActionButton) findViewById(R.id.requestedMeetingButton);

            mInputLayoutSearch = (TextInputLayout) findViewById(R.id.input_layout_search);
            mInputSearch = (EditText) findViewById(R.id.input_search);

            MeetingRequestCount = 0;
            MeetingRequestCountAll = 0;

            floating_action_button_fab_with_listview = (FloatingActionButton) findViewById(R.id.floating_action_button_fab_with_listview);
            schoolList_listView_schoolList = (ExpandableListView) findViewById(R.id.schoolList_listView_schoolList);
            requestMeetingListCount = (TextView) findViewById(R.id.requestMeetingListCount);
            txtDataFound = (TextView) findViewById(R.id.txtDataFound);
            meetingListImg = (ImageView) findViewById(R.id.meetingListImg);
            txtDataFound.setVisibility(View.GONE);
            meetingListImg.setVisibility(View.GONE);
            meetingListData = new MeetingListData();
            alertDialogManager = new AlertDialogManager(SchoolListActivity.this);


            floating_action_button_fab_with_listview.hide();
            if (sessionManager.getSessionUserType().equals("T") || sessionManager.getSessionUserType().equals("A")) {
                floating_action_button_fab_with_listview.show();
            }
            registerEventReceiver();
            //prepareListData();
            getThreeMonthsMeetingListData = new ArrayList<GetThreeMonthsMeetingListData>();
            getThreeMonthsMeeting();
            userMeetingsDateList = new ArrayList<UserMeetingsDate>();
            if (savedInstanceState != null) {
                if (!savedInstanceState.getString("mInputSearch").isEmpty()) {
                    //mInputSearch.setText(savedInstanceState.getString("mInputSearch"));
                    searchFlag = true;
                    mInputLayoutSearch.setVisibility(View.VISIBLE);
                    mInputSearch.setText("");
                }
                userMeetingsDateList = (ArrayList<UserMeetingsDate>) savedInstanceState.getSerializable("userMeetingsDateList");
                requestCount = savedInstanceState.getInt("requestCount");
                loading = savedInstanceState.getBoolean("loading");
                currentVisibleItemInListView = savedInstanceState.getInt("currentVisibleItemInListView");
                y_str = savedInstanceState.getString("y_str");

                MeetingRequestCount = savedInstanceState.getInt("MeetingRequestCount", MeetingRequestCount);//<UserMeetingsDate>
                MeetingRequestCountAll = savedInstanceState.getInt("MeetingRequestCountAll", MeetingRequestCountAll);
                if (MeetingRequestCount > 0) {
                    requestMeetingListCount.setVisibility(View.VISIBLE);
                    requestMeetingListCount.setText(String.valueOf(MeetingRequestCount));
                } else {
                    requestMeetingListCount.setVisibility(View.GONE);
                }

                if (MeetingRequestCountAll > 0) {
                    requestedMeetingButton.setVisibility(View.VISIBLE);
                } else {
                    requestedMeetingButton.setVisibility(View.GONE);
                }

                if (meetingListData.getUserMeetingsDates() != null && meetingListData.getUserMeetingsDates().size() > 0) {
                    txtDataFound.setVisibility(View.GONE);
                    meetingListImg.setVisibility(View.GONE);
                    schoolList_listView_schoolList.setVisibility(View.VISIBLE);
                    pmAcExpListAdapter = new SchoolExpListAdapter(SchoolListActivity.this, userMeetingsDateList, onChildItemClickActionListener);//meetingListData
                    schoolList_listView_schoolList.setAdapter(pmAcExpListAdapter);
                    schoolList_listView_schoolList.setSelectionFromTop(currentVisibleItemInListView, 0);
                    for (int i = 0; i < userMeetingsDateList.size(); i++) {//meetingListData.getUserMeetingsDates().size()
                        schoolList_listView_schoolList.expandGroup(i);
                    }
                } else {
                    schoolList_listView_schoolList.setVisibility(View.GONE);
                    txtDataFound.setVisibility(View.VISIBLE);
                    meetingListImg.setVisibility(View.VISIBLE);
                }

            } else {
                if (requestCount < 1) {
                    if (loading) {
                        prepareListData(String.valueOf(requestCount), 0);
                    }
                }
            }

            schoolList_listView_schoolList.setOnScrollListener(
                    new AbsListView.OnScrollListener() {
                        private int currentVisibleItemCount;
                        private int currentScrollState;
                        private int currentFirstVisibleItem;
                        private int totalItem;
                        //private LinearLayout lBelow;


                        @Override
                        public void onScrollStateChanged(AbsListView view, int scrollState) {
                            // TODO Auto-generated method stub
                            this.currentScrollState = scrollState;
                            //this.isScrollCompleted();
                            if (loading) {
                                requestCount = requestCount + 1;
                                prepareListData(String.valueOf(requestCount), currentScrollState);//String.valueOf(page)
                            }
                        }

                        @Override
                        public void onScroll(AbsListView view, int firstVisibleItem,
                                             int visibleItemCount, int totalItemCount) {
                            // TODO Auto-generated method stub
                            this.currentFirstVisibleItem = firstVisibleItem;
                            this.currentVisibleItemCount = visibleItemCount;
                            this.totalItem = totalItemCount;

                        /*if (loading) {
                            requestCount = requestCount + 1;
                            prepareListData(String.valueOf(requestCount), firstVisibleItem);//String.valueOf(page)
                        }*/

                        }

                        private void isScrollCompleted() {
                            if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                                    && this.currentScrollState == SCROLL_STATE_IDLE) {
                                /** To do code here*/


                            }
                        }
                    }
            );


            ////
            imgCalender.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //startActivity(new Intent(SchoolListActivity.this, CalenderViewMeetingListActivity.class));
                    Intent intent = new Intent(SchoolListActivity.this, CalenderViewMeetingListActivity.class);
                    startActivityForResult(intent, REFRESH_DATA);
                }
            });

            /////Search for Listview
            imgSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   /* if (searchFlag == true) {
                        searchFlag = false;*/

                  /*  } else {
                        searchFlag = true;*/
                    mInputLayoutSearch.setVisibility(View.VISIBLE);
                    mInputSearch.setText("");
                    mInputSearch.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mInputSearch, InputMethodManager.SHOW_IMPLICIT);

                    /*}*/


                }
            });
            cancelSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mInputLayoutSearch.setVisibility(View.GONE);
                    if (mInputSearch.getText().toString().isEmpty() || mInputSearch.getText().toString() == null) {

                    } else {
                        if (refreshCnt < 1) {
                            refreshCnt = 1;
                            userMeetingsDateList.clear();// = new ArrayList<UserMeetingsDate>();
                            requestCount = 0;
                            loading = true;
                            prepareListData("0", 0);
                            //mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }

                    mInputSearch.setText("");
                    mInputSearch.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mInputSearch, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            });


            mInputSearch.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub
                    if (mInputSearch.isFocused()) {
                        String text = mInputSearch.getText().toString()
                                .toLowerCase(Locale.getDefault());
                        pmAcExpListAdapter.filterData(text);
                        expandAll();
                    }
                }
            });

            //////////////// User Profile //////////////////////

            setImageToUserProfileIcon();

            linearLayoutCircular.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SchoolListActivity.this, UserProfileActivity.class);
                    startActivityForResult(intent, RESULT_UPDATE_PROFILE);
                }
            });

            //////////////////////////////////////

            onChildItemClickActionListener = new SchoolExpListAdapter.OnChildItemClickActionListener() {
                @Override
                public void onChildItemClicked(UserMeeting usermeeting, int position) {

                    /*Intent intent = new Intent(SchoolListActivity.this, MeetingRequestListActivity.class);
                    startActivity(intent);*/
                    if (usermeeting != null) {

                        final String meetingDbId = usermeeting.getId() == null ? "" : usermeeting.getId().toString();
                        final String meetingId = usermeeting.getMeetingId() == null ? "" : usermeeting.getMeetingId().toString();
                        final String isHost = usermeeting.getIsHost() == null ? "" : usermeeting.getIsHost().toString();
                        final String isAvailable = usermeeting.getIsAvailable() == null ? "" : usermeeting.getIsAvailable().toString();
                        final String hostName = usermeeting.getHostName() == null ? "" : usermeeting.getHostName().toString();

                        goToEditingMeetingDetails(meetingDbId, meetingId, isHost, isAvailable,hostName);
                    }
                }

            };

            schoolList_listView_schoolList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    schoolList_listView_schoolList.expandGroup(groupPosition);
                    return true;
                }
            });

            ///////////
            floating_action_button_fab_with_listview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SchoolListActivity.this, CreateMeetingActivity.class);
                    intent.putExtra("comesFor", "new");
                    intent.putExtra("isAvailable", "0");
                    intent.putExtra("IsCalenderActivity", "N");
                    startActivity(intent);
                }
            });

            requestedMeetingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SchoolListActivity.this, MeetingRequestListActivity.class);
                    //startActivity(intent);
                    startActivityForResult(intent, 9);
                }
            });

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (refreshCnt < 1) {
                        refreshCnt = 1;
                        userMeetingsDateList.clear();// = new ArrayList<UserMeetingsDate>();
                        requestCount = 0;
                        loading = true;
                        prepareListData("0", 0);
                        //mSwipeRefreshLayout.setRefreshing(false);
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });

            googlePlusMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    alertDialogManager.Dialog("Confirmation", "Continue with Sync to Google Calendar?", "ok", "cancel", new AlertDialogManager.onTwoButtonClickListner() {
                        @Override
                        public void onPositiveClick() {

                            if (checkPermission() == true) {
                                mCredential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(SCOPES))
                                        .setBackOff(new ExponentialBackOff());
                                getResultsFromApi();



                                /*if (userMeetingsDateList.size() > 0) {

                                    for (int i = 0; i < userMeetingsDateList.size(); i++) {

                                        if (userMeetingsDateList.get(i).getUserMeetings().size() > 0) {
                                            for (int j = 0; j < userMeetingsDateList.get(i).getUserMeetings().size(); j++) {
                                                String userDate = userMeetingsDateList.get(i).getUserMeetings().get(j).getMeetingDate();
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
                                                createEventAsync(userMeetingsDateList.get(i).getUserMeetings().get(j).getTitle(), "", userMeetingsDateList.get(i).getUserMeetings().get(j).getAgenda(), start, end, null);

                                            }
                                        }
                                    }


                                }*///009
                                //createEventAsync(eventTitle.getText().toString(), eventLocation.getText().toString(), buffer.toString(), start, end, eventAttendeeEmail );
                            }
                        }

                        @Override
                        public void onNegativeClick() {
                        }
                    }).show();

                }
            });

        }


    }


    //method to expand all groups
    private void expandAll() {
        int count = pmAcExpListAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            schoolList_listView_schoolList.expandGroup(i);
        }
    }

    private void setImageToUserProfileIcon() {
        if (sessionManager.getSessionProfileImageUrl().isEmpty()) {
            Glide.with(this).load(R.drawable.baseline_account_circle_black_48)
                    .into(imgUserProfile);
            imgUserProfile.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary));
        } else {

            /*int randomNumber = generateRandomIntIntRange(0001, 9999);*/
            String imageUrl = sessionManager.getSessionProfileImageUrl();
            /*if (sessionManager.getIsProfilePicUpdated() == "1") {
                sessionManager.updateIsProfilePicUpdated("0");
                imageUrl =sessionManager.getSessionProfileImageUrl() + "?id=" + randomNumber;
            }else {
                imageUrl = sessionManager.getSessionProfileImageUrl();
            }*/

            Log.d("imageUrl", imageUrl);
            Glide.with(this).load(imageUrl)
                    .into(imgUserProfile);
        }
    }


    private void goToEditingMeetingDetails(String meetingDbId, String MeetingId, String IsHost, String isAvailable, String hostName) {
        Intent intent = new Intent(SchoolListActivity.this, MeetingDetailsActivity.class);
        intent.putExtra("meetingDbId", meetingDbId);
        intent.putExtra("MeetingId", MeetingId);
        intent.putExtra("IsHost", IsHost);
        intent.putExtra("hostName",hostName);
        intent.putExtra("isAvailable", isAvailable);
        intent.putExtra("RefreshFlag", "N");
        intent.putExtra("IsCalenderActivity", "N");
        //startActivity(intent);
        startActivityForResult(intent, REFRESH_DATA);
        mInputSearch.setText("");
        mInputLayoutSearch.setVisibility(View.GONE);
        searchFlag = false;
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        showToast("Click back again to exit.");

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    //
    private void prepareListData(final String page, final int currentFirstVisibleItem) {
        try {
            //showBusyProgress();
            JSONObject jo = new JSONObject();

            jo.put("UserId", sessionManager.getSessionUserId());
            jo.put("AccessToken", sessionManager.getPrefsSessionAccessToken());
            jo.put("Page", page);


            Log.i(SchoolListActivity.class.getName(), Constants.getMeetingList + "\n\n" + jo.toString());
            currentVisibleItemInListView = currentFirstVisibleItem;
            GsonRequest<MeetingListData> getAssignAvailabilityLearnersListRequest = new GsonRequest<>(Request.Method.POST, Constants.getMeetingList, jo.toString(), MeetingListData.class,
                    new Response.Listener<MeetingListData>() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public void onResponse(@NonNull MeetingListData response) {
                            //hideBusyProgress();
                            if (response.getError() != null) {
                                showToast(response.getError().getErrorMessage());
                            } else {
                                if (response.getSuccess() == 1) {

                                    meetingListData = response;
                                    MeetingRequestCount = Integer.parseInt(meetingListData.getMeetingRequestCount().toString());
                                    MeetingRequestCountAll = Integer.parseInt(meetingListData.getMeetingRequestCountAll().toString());
                                    if (MeetingRequestCount > 0) {
                                        requestMeetingListCount.setVisibility(View.VISIBLE);
                                        requestMeetingListCount.setText(String.valueOf(MeetingRequestCount));
                                    } else {
                                        requestMeetingListCount.setVisibility(View.GONE);
                                    }

                                    if (MeetingRequestCountAll > 0) {
                                        requestedMeetingButton.setVisibility(View.VISIBLE);
                                    } else {
                                        requestedMeetingButton.setVisibility(View.GONE);
                                    }

                                    if (meetingListData.getUserMeetingsDates() != null && meetingListData.getUserMeetingsDates().size() > 0) {
                                        txtDataFound.setVisibility(View.GONE);
                                        meetingListImg.setVisibility(View.GONE);
                                        actionBarLayout2.setVisibility(View.VISIBLE);
                                        /*googlePlusMenu.setVisibility(View.VISIBLE);*/
                                        schoolList_listView_schoolList.setVisibility(View.VISIBLE);

                                        /*if (meetingListData.getUserMeetingsDates().size() < 10) {
                                            loading = false;
                                        }*/
                                        /*int cnt = 0;
                                        for (int i = 0; i < meetingListData.getUserMeetingsDates().size(); i++) {//meetingListData.getUserMeetingsDates().size()
                                            if (meetingListData.getUserMeetingsDates().get(i).getUserMeetings() != null) {
                                                cnt += userMeetingsDateList.get(i).getUserMeetings().size();
                                            }
                                        }

                                        if (cnt < 10) {
                                            loading = false;
                                        }*/

                                        for (UserMeetingsDate userMeetingsDate :
                                                meetingListData.getUserMeetingsDates()) {
                                            userMeetingsDateList.add(userMeetingsDate);
                                        }

                                        int cnt = 0;
                                        for (int j = 0; j < userMeetingsDateList.size(); j++) {//meetingListData.getUserMeetingsDates().size()
                                            if (userMeetingsDateList.get(j).getUserMeetings() != null) {
                                                cnt += userMeetingsDateList.get(j).getUserMeetings().size();
                                            }
                                        }

                                        if (cnt < 20) {
                                            loading = false;
                                        }

                                        pmAcExpListAdapter = new SchoolExpListAdapter(SchoolListActivity.this, userMeetingsDateList, onChildItemClickActionListener);//meetingListData
                                        schoolList_listView_schoolList.setAdapter(pmAcExpListAdapter);
                                        schoolList_listView_schoolList.setSelectionFromTop(currentFirstVisibleItem, 0);
                                        for (int i = 0; i < userMeetingsDateList.size(); i++) {//meetingListData.getUserMeetingsDates().size()
                                            schoolList_listView_schoolList.expandGroup(i);
                                        }
                                        pmAcExpListAdapter.notifyDataSetChanged();
                                        refreshCnt = 0;
                                    } else {
                                        loading = false;
                                        if (userMeetingsDateList.size() < 1) {
                                            schoolList_listView_schoolList.setVisibility(View.GONE);
                                            txtDataFound.setVisibility(View.VISIBLE);
                                            meetingListImg.setVisibility(View.VISIBLE);
                                            actionBarLayout2.setVisibility(View.GONE);
                                            /* googlePlusMenu.setVisibility(View.GONE);*/
                                        }
                                    }
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideBusyProgress();

                }
            });
            getAssignAvailabilityLearnersListRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            getAssignAvailabilityLearnersListRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(getAssignAvailabilityLearnersListRequest, "MeetingListData");

        } catch (JSONException e) {
            hideBusyProgress();
            showToast("Something went wrong. Please try again later.");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        invalidateOptionsMenu();
        if (requestCode == RESULT_UPDATE_PROFILE && resultCode == RESULT_OK) {
            setImageToUserProfileIcon();
        }

        if (requestCode == 1 && data != null) {
            //prepareListData();
            requestCount = 0;
            loading = true;
            userMeetingsDateList.clear();// = new ArrayList<UserMeetingsDate>();
            //schoolList_listView_schoolList.setVisibility(View.GONE);
            //pmAcExpListAdapter.notifyDataSetChanged();
            prepareListData("0", 0);
            isHitOnActivityResult = true;

        }

        if (requestCode == 9 && data != null) {
            requestCount = 0;
            loading = true;
            userMeetingsDateList.clear();
            prepareListData("0", 0);
            isHitOnActivityResult = true;
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
                    //showToast("in onActivityResult REQUEST_ACCOUNT_PICKER");
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        /*SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.commit();*/

                        mCredential.setSelectedAccountName(accountName);
                        sessionManager.updateGoogleAccount(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    //showToast("in onActivityResult REQUEST_AUTHORIZATION");
                    getResultsFromApi();
                }
                break;
        }
    }

    final String eventName = "com.edbrix.connectbrix.EVENT";

    private void registerEventReceiver() {
        IntentFilter eventFilter = new IntentFilter();
        eventFilter.addAction(eventName);
        registerReceiver(eventReceiver, eventFilter);
    }

    private BroadcastReceiver eventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String RefreshFlag = intent.getStringExtra("RefreshFlag");
            if (RefreshFlag.equals("Y")) {
                requestCount = 0;
                loading = true;
                userMeetingsDateList.clear(); //= new ArrayList<UserMeetingsDate>();
                //schoolList_listView_schoolList.setVisibility(View.GONE);
                //pmAcExpListAdapter.notifyDataSetChanged();
                prepareListData("0", 0);
                isHitOnActivityResult = true;

            }
            //This code will be executed when the broadcast in activity B is launched
        }
    };


    private boolean validateUser() {
        //sessionManager = new SessionManager(LoginActivity.this);
        if (!sessionManager.getSessionUsername().equals("") && !sessionManager.getSessionUserId().equals("") && !sessionManager.getPrefsOrganizationApiKey().equals("") && !sessionManager.getPrefsOrganizationSecretKey().equals("")) {
            return true;////user available
        } else {
            return false;////user not available
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!y_str.equals("N")) {

            if (isHitOnActivityResult != true) {
                y_str = intent.getStringExtra("result") == null ? "" : intent.getStringExtra("result");
                if (y_str.equals("y")) {
                    requestCount = 0;
                    loading = true;
                    userMeetingsDateList.clear();
                    prepareListData("0", 0);
                    intent.removeExtra("result");
                }
            }
            /*showToast("hiii");*/
            getThreeMonthsMeeting();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("userMeetingsDateList", userMeetingsDateList);//<UserMeetingsDate>
        outState.putInt("requestCount", requestCount);//<UserMeetingsDate>
        outState.putBoolean("loading", loading);//<UserMeetingsDate>
        outState.putInt("currentVisibleItemInListView", currentVisibleItemInListView);//<UserMeetingsDate>
        outState.putString("y_str", y_str);//<UserMeetingsDate>
        outState.putString("mInputSearch", mInputSearch.getText().toString());

        outState.putInt("MeetingRequestCount", MeetingRequestCount);//<UserMeetingsDate>
        outState.putInt("MeetingRequestCountAll", MeetingRequestCountAll);

    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        userMeetingsDateList = (ArrayList<UserMeetingsDate>) savedInstanceState.getSerializable("userMeetingsDateList");
        requestCount = savedInstanceState.getInt("requestCount");
        loading = savedInstanceState.getBoolean("loading");
        currentVisibleItemInListView = savedInstanceState.getInt("currentVisibleItemInListView");
        y_str = savedInstanceState.getString("y_str");

        MeetingRequestCount = savedInstanceState.getInt("MeetingRequestCount", MeetingRequestCount);//<UserMeetingsDate>
        MeetingRequestCountAll = savedInstanceState.getInt("MeetingRequestCountAll", MeetingRequestCountAll);
        if (MeetingRequestCount > 0) {
            requestMeetingListCount.setVisibility(View.VISIBLE);
            requestMeetingListCount.setText(String.valueOf(MeetingRequestCount));
        } else {
            requestMeetingListCount.setVisibility(View.GONE);
        }

        if (MeetingRequestCountAll > 0) {
            requestedMeetingButton.setVisibility(View.VISIBLE);
        } else {
            requestedMeetingButton.setVisibility(View.GONE);
        }

        if (userMeetingsDateList != null && userMeetingsDateList.size() > 0) {
            txtDataFound.setVisibility(View.GONE);
            meetingListImg.setVisibility(View.GONE);
            schoolList_listView_schoolList.setVisibility(View.VISIBLE);
            pmAcExpListAdapter = new SchoolExpListAdapter(SchoolListActivity.this, userMeetingsDateList, onChildItemClickActionListener);//meetingListData
            schoolList_listView_schoolList.setAdapter(pmAcExpListAdapter);
            schoolList_listView_schoolList.setSelectionFromTop(currentVisibleItemInListView, 0);
            for (int i = 0; i < userMeetingsDateList.size(); i++) {//meetingListData.getUserMeetingsDates().size()
                schoolList_listView_schoolList.expandGroup(i);
            }
        } else {
            schoolList_listView_schoolList.setVisibility(View.GONE);
            txtDataFound.setVisibility(View.VISIBLE);
            meetingListImg.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        y_str = "S";
    }


    //------------------------------------- Sync Meetings With Google Calendar-----------------------------------//

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(SchoolListActivity.this, Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(SchoolListActivity.this,
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
                        Toast.makeText(SchoolListActivity.this, "Google account permission not granted", Toast.LENGTH_LONG).show();
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
                SchoolListActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private void chooseAccount() {
        // to do clear mCredential and shared perferance when logout
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null);
            /*if (accountName != null) {
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
            /*showBusyProgress();*/
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            /*hideBusyProgress();*/
            mProgress.hide();

            size = getThreeMonthsMeetingListData.size() - 1;
            if (getThreeMonthsMeetingListData.size() > 0) {

                //for (int i = 0; i < getThreeMonthsMeetingListData.size(); i++) {

                String userDate = getThreeMonthsMeetingListData.get(0).getStartDateTime();
                SimpleDateFormat convertDateTime = new SimpleDateFormat("dd/MMM/yyyy hh:mm a");
                DateTime start = null;
                DateTime end = null;
                Date dateTime = null;
                try {
                    Date dateOfMeeting = convertDateTime.parse(userDate);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    String tempDate = simpleDateFormat.format(dateOfMeeting);
                    dateTime = simpleDateFormat.parse(tempDate);
                    start = new DateTime(dateTime);
                    end = new DateTime(dateTime);
                    Log.d("Date", dateOfMeeting.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                createEventAsync(getThreeMonthsMeetingListData.get(0).getTitle(), "", getThreeMonthsMeetingListData.get(0).getAgenda(), start, end, null,dateTime);
                //}
                //showToast("Sync Success");
            } else {
                showToast("No meetings available.");
            }
        }
    }

    public void createEventAsync(final String summary, final String location, final String des, final DateTime startDate, final DateTime endDate, final EventAttendee[]
            eventAttendees,Date dateTime) {
        //hideBusyProgress();
        new AsyncTask<Void, Void, String>() {
            private com.google.api.services.calendar.Calendar mService = null;
            private Exception mLastError = null;
            private boolean FLAG = false;


            @Override
            protected String doInBackground(Void... voids) {
                try {
                    insertEvent(summary, location, des, startDate, endDate, new EventAttendee[0],dateTime);
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
                    /*hideBusyProgress();*/
                    mProgress.hide();
                    showToast("Meetings sync to Google Calendar successfully.");
                } else {
                    //showBusyProgress();
                    //getThreeMonthsMeetingListData.get(counter);
                    counter++;
                    String userDate = getThreeMonthsMeetingListData.get(counter).getStartDateTime();
                    SimpleDateFormat convertDateTime = new SimpleDateFormat("dd/MMM/yyyy hh:mm a");
                    DateTime start = null;
                    DateTime end = null;
                    Date dateTime = null;
                    try {
                        Date dateOfMeeting = convertDateTime.parse(userDate);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                        String tempDate = simpleDateFormat.format(dateOfMeeting);
                        dateTime = simpleDateFormat.parse(tempDate);
                        start = new DateTime(dateTime);
                        end = new DateTime(dateTime);
                        Log.d("Date", dateOfMeeting.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    createEventAsync(getThreeMonthsMeetingListData.get(counter).getTitle(), "", getThreeMonthsMeetingListData.get(counter).getAgenda(), start, end, null,dateTime);

                }

                //getResultsFromApi();
            }
        }.execute();
    }

    void insertEvent(String summary, String location, String des, DateTime startDate, DateTime endDate, EventAttendee[] eventAttendees, Date dateTime) throws IOException {

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

        /*ContentResolver cr = getApplicationContext().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, dateTime.getTime());
        values.put(CalendarContract.Events.TITLE, summary);
        values.put(CalendarContract.Events.DESCRIPTION, des);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        // Default calendar
        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        values.put(CalendarContract.Events.HAS_ALARM, 1);

        // Insert event to calendar
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            //
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);*/
        //event.send
        if (mService != null) {
            mService.events().insert(calendarId, event).setSendNotifications(true).execute();
            /*try {
                mService.events().insert(calendarId, event).setSendNotifications(true).execute();
            } catch (UserRecoverableAuthIOException e) {
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
}


