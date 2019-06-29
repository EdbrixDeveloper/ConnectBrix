package com.edbrix.connectbrix.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.edbrix.connectbrix.adapters.MeetingRequestExpListAdapter;
import com.edbrix.connectbrix.adapters.SchoolExpListAdapter;
import com.edbrix.connectbrix.baseclass.BaseActivity;
import com.edbrix.connectbrix.commons.AlertDialogManager;
import com.edbrix.connectbrix.data.MeetingDetailsData;
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

public class MeetingRequestListActivity extends BaseActivity {

    private MeetingRequestExpListAdapter meetingRequestExpListAdapter;
    public ExpandableListView meetingRequestList_listView_meetingRequestList;
    private AlertDialogManager alertDialogManager;
    private TextView txtDataFound;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static final int REFRESH_DATA = 1;
    SessionManager sessionManager;
    private MeetingListData meetingListData;

    // Listview Pagingnation Purpose
    ArrayList<UserMeetingsDate> userMeetingsDateList;
    private int requestCount = 0;
    private boolean loading = true;
    private MeetingRequestExpListAdapter.OnButtonClickActionListener onButtonClickActionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(MeetingRequestListActivity.this);
        setContentView(R.layout.activity_meeting_request_list);
        getSupportActionBar().setTitle("Meeting Requests");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        meetingRequestList_listView_meetingRequestList = (ExpandableListView) findViewById(R.id.meetingRequestList_listView_meetingRequestList);
        txtDataFound = (TextView) findViewById(R.id.txtDataFound);
        txtDataFound.setVisibility(View.GONE);

        meetingListData = new MeetingListData();
        alertDialogManager = new AlertDialogManager(MeetingRequestListActivity.this);

        userMeetingsDateList = new ArrayList<UserMeetingsDate>();

        if (requestCount < 1) {
            if (loading) {
                prepareListData(String.valueOf(requestCount), 0);
            }
        }

        meetingRequestList_listView_meetingRequestList.setOnScrollListener(
                new AbsListView.OnScrollListener() {
                    private int currentVisibleItemCount;
                    private int currentScrollState;
                    private int currentFirstVisibleItem;
                    private int totalItem;

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

                    }

                    private void isScrollCompleted() {
                        if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                                && this.currentScrollState == SCROLL_STATE_IDLE) {
                            /** To do code here*/


                        }
                    }
                }
        );


        //////////////////////////////////////

        onButtonClickActionListener = new MeetingRequestExpListAdapter.OnButtonClickActionListener() {
            @Override
            public void onChildItemClicked(UserMeeting usermeeting, int position, String Status) {
                if (usermeeting != null) {

                    final String meetingDbId = usermeeting.getId() == null ? "" : usermeeting.getId().toString();

                    //final String meetingId = usermeeting.getMeetingId() == null ? "" : usermeeting.getMeetingId().toString();
                    //final String isHost = usermeeting.getIsHost() == null ? "" : usermeeting.getIsHost().toString();
                    //final String isAvailable = usermeeting.getIsAvailable() == null ? "" : usermeeting.getIsAvailable().toString();
                    final String status = Status;
                    String msg = "";
                    if(status=="1"){
                        msg = "Accept";
                    }else{
                        msg = "Reject";
                    }

                    alertDialogManager.Dialog("Confirmation", "Continue with "+msg+" meeting?", "ok", "cancel", new AlertDialogManager.onTwoButtonClickListner() {
                        @Override
                        public void onPositiveClick() {
                            meetingAvilabilityStatus(meetingDbId, status);
                        }

                        @Override
                        public void onNegativeClick() {

                        }
                    }).show();

                }
            }

        };

        meetingRequestList_listView_meetingRequestList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                meetingRequestList_listView_meetingRequestList.expandGroup(groupPosition);
                return true;
            }
        });

        ///////////
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

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
        return;
        /*if (doubleBackToExitPressedOnce) {
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
        }, 2000);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        /*menuInflater.inflate(R.menu.meeting_details_menu, menu);

        MenuItem shareItem = menu.findItem(R.id.menuEdit);
        MenuItem shareItem2 = menu.findItem(R.id.menuDelete);

        // show the button when some condition is true
        shareItem.setVisible(false);
        shareItem2.setVisible(false);
        if ((sessionManager.getSessionUserType().equals("T") || sessionManager.getSessionUserType().equals("A")) && IsHost.equals("1")) {
            shareItem.setVisible(true);
            shareItem2.setVisible(true);
        }*/
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                //finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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


            Log.i(MeetingRequestListActivity.class.getName(), Constants.getMeetingRequestList + "\n\n" + jo.toString());

            GsonRequest<MeetingListData> getAssignAvailabilityLearnersListRequest = new GsonRequest<>(Request.Method.POST, Constants.getMeetingRequestList, jo.toString(), MeetingListData.class,
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
                                        meetingRequestList_listView_meetingRequestList.setVisibility(View.VISIBLE);

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

                                        meetingRequestExpListAdapter = new MeetingRequestExpListAdapter(MeetingRequestListActivity.this, userMeetingsDateList, onButtonClickActionListener);
                                        meetingRequestList_listView_meetingRequestList.setAdapter(meetingRequestExpListAdapter);
                                        meetingRequestList_listView_meetingRequestList.setSelectionFromTop(currentFirstVisibleItem, 0);
                                        for (int i = 0; i < userMeetingsDateList.size(); i++) {
                                            meetingRequestList_listView_meetingRequestList.expandGroup(i);
                                        }
                                        meetingRequestExpListAdapter.notifyDataSetChanged();
                                    } else {
                                        loading = false;
                                        if (userMeetingsDateList.size() < 1) {
                                            meetingRequestList_listView_meetingRequestList.setVisibility(View.GONE);
                                            txtDataFound.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //hideBusyProgress();

                }
            });
            getAssignAvailabilityLearnersListRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            getAssignAvailabilityLearnersListRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(getAssignAvailabilityLearnersListRequest, "MeetingListData");

        } catch (JSONException e) {
            //hideBusyProgress();
            showToast("Something went wrong. Please try again later.");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void meetingAvilabilityStatus(String meetingDbId, String StatusFlag) {
        try {
            showBusyProgress();
            JSONObject jo = new JSONObject();

            jo.put("APIKEY", sessionManager.getPrefsOrganizationApiKey());
            jo.put("SECRETKEY", sessionManager.getPrefsOrganizationSecretKey());
            jo.put("UserId", sessionManager.getSessionUserId());
            jo.put("MeetingId", meetingDbId);
            jo.put("Available", StatusFlag);

            Log.i(MeetingDetailsActivity.class.getName(), Constants.updateMeetingAvilabilityStatus + "\n\n" + jo.toString());

            GsonRequest<MeetingDetailsData> getAssignAvailabilityLearnersListRequest = new GsonRequest<>(Request.Method.POST, Constants.updateMeetingAvilabilityStatus, jo.toString(), MeetingDetailsData.class,
                    new Response.Listener<MeetingDetailsData>() {
                        @Override
                        public void onResponse(@NonNull MeetingDetailsData response) {
                            hideBusyProgress();
                            if (response.getError() != null) {
                                showToast(response.getError().getErrorMessage());
                            } else {
                                if (response.getSuccess() == 1) {
                                    showToast("Successfully updated meeting status");
                                    userMeetingsDateList = new ArrayList<UserMeetingsDate>();
                                    requestCount = 0;
                                    loading = true;
                                    prepareListData("0", 0);
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
            Application.getInstance().addToRequestQueue(getAssignAvailabilityLearnersListRequest, "MeetingAvailabilityStatus");

        } catch (JSONException e) {
            hideBusyProgress();
            showToast("Something went wrong. Please try again later.");
        }

    }

}


