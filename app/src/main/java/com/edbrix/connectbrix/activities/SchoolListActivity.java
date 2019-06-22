package com.edbrix.connectbrix.activities;

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
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
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
import com.edbrix.connectbrix.data.UserMeetingsDate;
import com.edbrix.connectbrix.utils.Constants;
import com.edbrix.connectbrix.utils.SessionManager;
import com.edbrix.connectbrix.volley.GsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SchoolListActivity extends BaseActivity {

    private SchoolExpListAdapter pmAcExpListAdapter;
    public ExpandableListView schoolList_listView_schoolList;
    private AlertDialogManager alertDialogManager;
    private TextView txtDataFound;
    private ImageView imgCalender;
    private ImageView imgUserProfile;
    private SwipeRefreshLayout mSwipeRefreshLayout;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(SchoolListActivity.this);
        if (!validateUser()) {
            finish();
            startActivity(new Intent(SchoolListActivity.this, LoginActivity.class));
        } else {
            setContentView(R.layout.activity_school_list);

            mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
            mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
            imgCalender = (ImageView) findViewById(R.id.calender);
            imgUserProfile = (ImageView) findViewById(R.id.imgUserProfile);
            floating_action_button_fab_with_listview = (FloatingActionButton) findViewById(R.id.floating_action_button_fab_with_listview);
            schoolList_listView_schoolList = (ExpandableListView) findViewById(R.id.schoolList_listView_schoolList);
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

            if (requestCount < 1) {
                if (loading) {
                    prepareListData(String.valueOf(requestCount), 0);
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

            //////////////// User Profile //////////////////////

            setImageToUserProfileIcon();
            imgUserProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SchoolListActivity.this, UserProfileActivity.class);
                    startActivityForResult(intent, RESULT_UPDATE_PROFILE);
                }
            });

            //////////////////////////////////////

            schoolList_listView_schoolList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    schoolList_listView_schoolList.expandGroup(groupPosition);
                    return true;
                }
            });

            schoolList_listView_schoolList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, final int childPosition, long id) {
                    try {
                        if (userMeetingsDateList != null && userMeetingsDateList.size() > 0) {

                    /*final String meetingDbId = meetingListData.getUserMeetingsDates().get(groupPosition).getUserMeetings().get(childPosition).getId() == null ? "" : meetingListData.getUserMeetingsDates().get(groupPosition).getUserMeetings().get(childPosition).getId().toString();
                    final String meetingId = meetingListData.getUserMeetingsDates().get(groupPosition).getUserMeetings().get(childPosition).getMeetingId() == null ? "" : meetingListData.getUserMeetingsDates().get(groupPosition).getUserMeetings().get(childPosition).getMeetingId().toString();
                    final String isHost = meetingListData.getUserMeetingsDates().get(groupPosition).getUserMeetings().get(childPosition).getIsHost() == null ? "" : meetingListData.getUserMeetingsDates().get(groupPosition).getUserMeetings().get(childPosition).getIsHost().toString();*/

                            final String meetingDbId = userMeetingsDateList.get(groupPosition).getUserMeetings().get(childPosition).getId() == null ? "" : userMeetingsDateList.get(groupPosition).getUserMeetings().get(childPosition).getId().toString();
                            final String meetingId = userMeetingsDateList.get(groupPosition).getUserMeetings().get(childPosition).getMeetingId() == null ? "" : userMeetingsDateList.get(groupPosition).getUserMeetings().get(childPosition).getMeetingId().toString();
                            final String isHost = userMeetingsDateList.get(groupPosition).getUserMeetings().get(childPosition).getIsHost() == null ? "" : userMeetingsDateList.get(groupPosition).getUserMeetings().get(childPosition).getIsHost().toString();

                            goToEditingMeetingDetails(meetingDbId, meetingId, isHost);
                        }
                    } catch (Exception ex) {
                        Log.i("SchoolList:onChildClick", ex.getMessage().toString());
                    }
                    return false;
                }
            });


            ///////////
            floating_action_button_fab_with_listview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SchoolListActivity.this, CreateMeetingActivity.class);
                    intent.putExtra("comesFor", "new");
                    intent.putExtra("IsCalenderActivity", "N");
                    startActivity(intent);
                }
            });

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    userMeetingsDateList = new ArrayList<UserMeetingsDate>();
                    requestCount = 0;
                    loading = true;
                    prepareListData("0", 0);
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
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


    private void goToEditingMeetingDetails(String meetingDbId, String MeetingId, String IsHost) {
        Intent intent = new Intent(SchoolListActivity.this, MeetingDetailsActivity.class);
        intent.putExtra("meetingDbId", meetingDbId);
        intent.putExtra("MeetingId", MeetingId);
        intent.putExtra("IsHost", IsHost);
        intent.putExtra("RefreshFlag", "N");
        intent.putExtra("IsCalenderActivity", "N");
        //startActivity(intent);
        startActivityForResult(intent, REFRESH_DATA);
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
           *//* case android.R.id.:
                startActivity(new Intent(this, UserProfileActivity.class));
                return true;*//*
            case R.id.menuProfile:
                startActivity(new Intent(this, UserProfileActivity.class));
                return true;
            case R.id.menuCalender:
                startActivity(new Intent(this, CalenderViewMeetingListActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.dashboard_menu, menu);
        return true;

    }*/

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

            GsonRequest<MeetingListData> getAssignAvailabilityLearnersListRequest = new GsonRequest<>(Request.Method.POST, Constants.getMeetingList, jo.toString(), MeetingListData.class,
                    new Response.Listener<MeetingListData>() {
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

                                        pmAcExpListAdapter = new SchoolExpListAdapter(SchoolListActivity.this, userMeetingsDateList);//meetingListData
                                        schoolList_listView_schoolList.setAdapter(pmAcExpListAdapter);
                                        schoolList_listView_schoolList.setSelectionFromTop(currentFirstVisibleItem, 0);
                                        for (int i = 0; i < userMeetingsDateList.size(); i++) {//meetingListData.getUserMeetingsDates().size()
                                            schoolList_listView_schoolList.expandGroup(i);
                                        }
                                        pmAcExpListAdapter.notifyDataSetChanged();
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

        if (requestCode == 1) {
            //prepareListData();
            requestCount = 0;
            loading = true;
            userMeetingsDateList = new ArrayList<UserMeetingsDate>();
            prepareListData("0", 0);
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
                userMeetingsDateList = new ArrayList<UserMeetingsDate>();
                prepareListData("0", 0);
            }
            //This code will be executed when the broadcast in activity B is launched
        }
    };

    /*@Override
    protected void onStop() {
        unregisterReceiver(eventReceiver);
        super.onStop();
    }

    @Override
    protected void onResume() {
        registerEventReceiver();
        super.onResume();
    }*/

    private boolean validateUser() {
        //sessionManager = new SessionManager(LoginActivity.this);
        if (!sessionManager.getSessionUsername().equals("") && !sessionManager.getSessionUserId().equals("") && !sessionManager.getPrefsOrganizationApiKey().equals("") && !sessionManager.getPrefsOrganizationSecretKey().equals("")) {
            return true;////user available
        } else {
            return false;////user not available
        }
    }

    //clock
    /*private Timer timer;
    private TimerTask timerTask;


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (myContinouslyRunningAsyncTask != null) {
            myContinouslyRunningAsyncTask.cancel(true);
        }

        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        clockTask();
    }

    public class MyContinousAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    SchoolListActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(SchoolListActivity.class.getName(), "Calling From Timer Async Task...");
                            prepareListData();
                        }
                    });
                }
            };
            timer.scheduleAtFixedRate(timerTask, 100, 6000);//10000
            return "execute";
        }

    }

    private void clockTask() {
        myContinouslyRunningAsyncTask = new MyContinousAsyncTask();
        myContinouslyRunningAsyncTask.execute("execute");
    }*/
}


