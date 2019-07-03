package com.edbrix.connectbrix.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.edbrix.connectbrix.Application;
import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.adapters.SchoolExpListAdapter;
import com.edbrix.connectbrix.baseclass.BaseActivity;
import com.edbrix.connectbrix.commons.AlertDialogManager;
import com.edbrix.connectbrix.commons.EndlessScrollListener;
import com.edbrix.connectbrix.data.MeetingListData;
import com.edbrix.connectbrix.data.UserMeeting;
import com.edbrix.connectbrix.data.UserMeetingsDate;
import com.edbrix.connectbrix.utils.Constants;
import com.edbrix.connectbrix.utils.SessionManager;
import com.edbrix.connectbrix.volley.GsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class SchoolListActivity extends BaseActivity {

    private SchoolExpListAdapter pmAcExpListAdapter;
    public ExpandableListView schoolList_listView_schoolList;
    private AlertDialogManager alertDialogManager;
    private TextView txtDataFound;
    private TextView requestMeetingListCount;
    private ImageView imgCalender;
    private ImageView imgUserProfile;
    private ImageView imgSearch;
    private LinearLayout linearLayoutCircular;
    private FloatingActionButton requestedMeetingButton;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private TextInputLayout mInputLayoutSearch;
    private EditText mInputSearch;
    private boolean searchFlag = false;

    public static final int RESULT_UPDATE_PROFILE = 200;

    public static final int REFRESH_DATA = 1;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(SchoolListActivity.this);
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
            linearLayoutCircular = (LinearLayout) findViewById(R.id.linearLayoutCircular);
            requestedMeetingButton = (FloatingActionButton) findViewById(R.id.requestedMeetingButton);

            mInputLayoutSearch = (TextInputLayout) findViewById(R.id.input_layout_search);
            mInputSearch = (EditText) findViewById(R.id.input_search);

            floating_action_button_fab_with_listview = (FloatingActionButton) findViewById(R.id.floating_action_button_fab_with_listview);
            schoolList_listView_schoolList = (ExpandableListView) findViewById(R.id.schoolList_listView_schoolList);
            requestMeetingListCount = (TextView) findViewById(R.id.requestMeetingListCount);
            txtDataFound = (TextView) findViewById(R.id.txtDataFound);
            txtDataFound.setVisibility(View.GONE);

            meetingListData = new MeetingListData();
            alertDialogManager = new AlertDialogManager(SchoolListActivity.this);


            floating_action_button_fab_with_listview.hide();
            if (sessionManager.getSessionUserType().equals("T") || sessionManager.getSessionUserType().equals("A")) {
                floating_action_button_fab_with_listview.show();
            }
            registerEventReceiver();
            //prepareListData();


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

                if (meetingListData.getUserMeetingsDates() != null && meetingListData.getUserMeetingsDates().size() > 0) {
                    txtDataFound.setVisibility(View.GONE);
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
                    if (searchFlag == true) {
                        searchFlag = false;
                        mInputLayoutSearch.setVisibility(View.GONE);
                        mInputSearch.setText("");
                    } else {
                        searchFlag = true;
                        mInputLayoutSearch.setVisibility(View.VISIBLE);
                        mInputSearch.setText("");
                    }


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


                        goToEditingMeetingDetails(meetingDbId, meetingId, isHost, isAvailable);
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
            int randomNumber = generateRandomIntIntRange(0001, 9999);
            String imageUrl = sessionManager.getSessionProfileImageUrl() + "?id=" + randomNumber;
            Glide.with(this).load(imageUrl)
                    .into(imgUserProfile);
        }
    }


    private void goToEditingMeetingDetails(String meetingDbId, String MeetingId, String IsHost, String isAvailable) {
        Intent intent = new Intent(SchoolListActivity.this, MeetingDetailsActivity.class);
        intent.putExtra("meetingDbId", meetingDbId);
        intent.putExtra("MeetingId", MeetingId);
        intent.putExtra("IsHost", IsHost);
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
            jo.put("APIKEY", sessionManager.getPrefsOrganizationApiKey());
            jo.put("SECRETKEY", sessionManager.getPrefsOrganizationSecretKey());
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
                                    if (meetingListData.getUserMeetingsDates() != null && meetingListData.getUserMeetingsDates().size() > 0) {
                                        txtDataFound.setVisibility(View.GONE);
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
                                        if (Integer.parseInt(meetingListData.getMeetingRequestCount().toString()) > 0) {
                                            requestMeetingListCount.setVisibility(View.VISIBLE);
                                            requestMeetingListCount.setText(meetingListData.getMeetingRequestCount().toString());
                                        } else {
                                            requestMeetingListCount.setVisibility(View.GONE);
                                        }

                                        if (Integer.parseInt(meetingListData.getMeetingRequestCountAll().toString()) > 0) {
                                            requestedMeetingButton.setVisibility(View.VISIBLE);
                                        } else {
                                            requestedMeetingButton.setVisibility(View.GONE);
                                        }

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
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        userMeetingsDateList = (ArrayList<UserMeetingsDate>) savedInstanceState.getSerializable("userMeetingsDateList");
        requestCount = savedInstanceState.getInt("requestCount");
        loading = savedInstanceState.getBoolean("loading");
        currentVisibleItemInListView = savedInstanceState.getInt("currentVisibleItemInListView");
        y_str = savedInstanceState.getString("y_str");

        if (userMeetingsDateList != null && userMeetingsDateList.size() > 0) {
            txtDataFound.setVisibility(View.GONE);
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
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        y_str = "S";
    }
}


