package com.edbrix.connectbrix.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.connectbrix.Application;
import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.adapters.SchoolExpListAdapter;
import com.edbrix.connectbrix.adapters.SchoolListAdapter;
import com.edbrix.connectbrix.baseclass.BaseActivity;
import com.edbrix.connectbrix.commons.AlertDialogManager;
import com.edbrix.connectbrix.data.MeetingListData;
import com.edbrix.connectbrix.utils.Constants;
import com.edbrix.connectbrix.utils.SessionManager;
import com.edbrix.connectbrix.volley.GsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class SchoolListActivity extends BaseActivity {

    private SchoolExpListAdapter pmAcExpListAdapter;
    public ExpandableListView schoolList_listView_schoolList;
    private AlertDialogManager alertDialogManager;
    private TextView txtDataFound;

    FloatingActionButton floating_action_button_fab_with_listview;
    boolean doubleBackToExitPressedOnce = false;
    SessionManager sessionManager;
    private MeetingListData meetingListData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_list);
        getSupportActionBar().setTitle("Meeting List");
        /*  getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        //////////

        floating_action_button_fab_with_listview = (FloatingActionButton) findViewById(R.id.floating_action_button_fab_with_listview);
        schoolList_listView_schoolList = (ExpandableListView) findViewById(R.id.schoolList_listView_schoolList);
        txtDataFound = (TextView) findViewById(R.id.txtDataFound);
        txtDataFound.setVisibility(View.GONE);

        meetingListData = new MeetingListData();
        alertDialogManager = new AlertDialogManager(SchoolListActivity.this);
        sessionManager = new SessionManager(SchoolListActivity.this);

        floating_action_button_fab_with_listview.hide();
        if (sessionManager.getSessionUserType().equals("T") || sessionManager.getSessionUserType().equals("A")) {
            floating_action_button_fab_with_listview.show();
        }

        prepareListData();

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

                if (meetingListData != null) {

                    final String meetingId = meetingListData.getUserMeetingsDates().get(groupPosition).getUserMeetings().get(childPosition).getId() == null ? "" : meetingListData.getUserMeetingsDates().get(groupPosition).getUserMeetings().get(childPosition).getId().toString();
                    final String isHost = meetingListData.getUserMeetingsDates().get(groupPosition).getUserMeetings().get(childPosition).getId() == null ? "" : meetingListData.getUserMeetingsDates().get(groupPosition).getUserMeetings().get(childPosition).getIsHost().toString();
                    goToEditingMeetingDetails(meetingId, isHost);
                    /*final String meetingDate = meetingListData.getUserMeetingsDates().get(groupPosition).getUserMeetings().get(childPosition).getMeetingDate() == null ? "" : meetingListData.getUserMeetingsDates().get(groupPosition).getUserMeetings().get(childPosition).getMeetingDate().toString();
                    final String sitePMAcTicketDate = meetingListData.getUserMeetingsDates().get(groupPosition).getUserMeetings().get(childPosition).getMeetingDate() == null ? "" : meetingListData.getSitePMTicketsDates().get(groupPosition).getSitePMAcTickets().get(childPosition).getSitePMAcTicketDate().toString();
                    final String pmPlanDate = meetingListData.getUserMeetingsDates().get(groupPosition).getUserMeetings().get(childPosition).getMeetingDate() == null ? "" : meetingListData.getSitePMTicketsDates().get(groupPosition).getSitePMAcTickets().get(childPosition).getPmPlanDate().toString();
                    final String submittedDate = meetingListData.getUserMeetingsDates().get(groupPosition).getUserMeetings().get(childPosition).getMeetingDate() == null ? "" : meetingListData.getSitePMTicketsDates().get(groupPosition).getSitePMAcTickets().get(childPosition).getSubmittedDate().toString();
                    final String sheduledDateOfAcPm = meetingListData.getUserMeetingsDates().get(groupPosition).getUserMeetings().get(childPosition).getMeetingDate() == null ? "" : meetingListData.getSitePMTicketsDates().get(groupPosition).getSitePMAcTickets().get(childPosition).getSheduledDateOfAcPm().toString();

                    checkSystemLocation(customerName, circleName, stateName, ssaName, siteDBId, siteId, siteName, siteType,
                            sitePMAcTicketId, sitePMAcTicketNo, sitePMAcTicketDate, pmPlanDate,
                            submittedDate, sheduledDateOfAcPm, numberOfAc, modeOfOpration,
                            vendorName, acTechnicianName, acTechnicianMobileNo, accessType, ticketAccess, acPmTickStatus);*/
                    //showToast("Clicked on Meeting");
                }

                return false;
            }
        });


        ///////////
        floating_action_button_fab_with_listview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SchoolListActivity.this, CreateMeetingActivity.class));
            }
        });

    }

    private void goToEditingMeetingDetails(String MeetingId, String IsHost) {
        Intent intent = new Intent(SchoolListActivity.this, MeetingDetailsActivity.class);
        intent.putExtra("MeetingId", MeetingId);
        intent.putExtra("IsHost", IsHost);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
           /* case android.R.id.:
                startActivity(new Intent(this, UserProfileActivity.class));
                return true;*/
            case R.id.menuProfile:
                startActivity(new Intent(this, UserProfileActivity.class));
                return true;
            case R.id.menuCalender:
                startActivity(new Intent(this, CalenderViewMeetingListActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.dashboard_menu, menu);
        return true;

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

    private void prepareListData() {
        try {
            showBusyProgress();
            JSONObject jo = new JSONObject();

            jo.put("UserId", sessionManager.getSessionUserId());
            jo.put("APIKEY", sessionManager.getPrefsOrganizationApiKey());
            jo.put("SECRETKEY", sessionManager.getPrefsOrganizationSecretKey());

            Log.i(SchoolListActivity.class.getName(), Constants.getMeetingList + "\n\n" + jo.toString());

            GsonRequest<MeetingListData> getAssignAvailabilityLearnersListRequest = new GsonRequest<>(Request.Method.POST, Constants.getMeetingList, jo.toString(), MeetingListData.class,
                    new Response.Listener<MeetingListData>() {
                        @Override
                        public void onResponse(@NonNull MeetingListData response) {
                            hideBusyProgress();
                            if (response.getError() != null) {
                                showToast(response.getError().getErrorMessage());
                            } else {
                                if (response.getSuccess() == 1) {

                                    meetingListData = response;
                                    if (meetingListData.getUserMeetingsDates() != null && meetingListData.getUserMeetingsDates().size() > 0) {
                                        txtDataFound.setVisibility(View.GONE);
                                        schoolList_listView_schoolList.setVisibility(View.VISIBLE);
                                        pmAcExpListAdapter = new SchoolExpListAdapter(SchoolListActivity.this, meetingListData);
                                        schoolList_listView_schoolList.setAdapter(pmAcExpListAdapter);
                                        for (int i = 0; i < meetingListData.getUserMeetingsDates().size(); i++) {
                                            schoolList_listView_schoolList.expandGroup(i);
                                        }
                                    } else {
                                        schoolList_listView_schoolList.setVisibility(View.GONE);
                                        txtDataFound.setVisibility(View.VISIBLE);
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

}
