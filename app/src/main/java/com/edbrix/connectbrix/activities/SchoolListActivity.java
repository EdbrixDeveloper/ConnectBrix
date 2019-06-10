package com.edbrix.connectbrix.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.connectbrix.Application;
import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.adapters.SchoolExpListAdapter;
import com.edbrix.connectbrix.adapters.SchoolListAdapter;
import com.edbrix.connectbrix.baseclass.BaseActivity;
import com.edbrix.connectbrix.data.MeetingListData;
import com.edbrix.connectbrix.utils.Constants;
import com.edbrix.connectbrix.utils.SessionManager;
import com.edbrix.connectbrix.volley.GsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class SchoolListActivity extends BaseActivity {

    private ListView mSchoolMeetingList;
    SchoolListAdapter schoolListAdapter;
    ArrayList<String> date = new ArrayList<>();
    FloatingActionButton floating_action_button_fab_with_listview;
    boolean doubleBackToExitPressedOnce = false;

    private SchoolExpListAdapter pmAcExpListAdapter;
    public ExpandableListView pmSiteList_listView_siteList;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_list);
        getSupportActionBar().setTitle("Meeting List");
        /*  getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        assignViews();

        date.add("02/03/2019");
        date.add("12/03/2019");
        date.add("05/03/2019");
        date.add("04/03/2019");
        date.add("01/03/2019");
        date.add("14/03/2019");
        date.add("17/03/2019");
        date.add("20/03/2019");

        setSchoolListAdapter(date);

        mSchoolMeetingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SchoolListActivity.this, MeetingDetailsActivity.class);
                intent.putExtra("meetingTitle", "School Anual Function Meeting");
                startActivity(intent);
            }
        });

        floating_action_button_fab_with_listview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SchoolListActivity.this, CreateMeetingActivity.class));
            }
        });

    }

    private void setSchoolListAdapter(ArrayList<String> date) {
        schoolListAdapter = new SchoolListAdapter(SchoolListActivity.this, date);
        mSchoolMeetingList.setAdapter(schoolListAdapter);
    }

    private void assignViews() {
        mSchoolMeetingList = (ListView) findViewById(R.id.schoolMeetingList);
        floating_action_button_fab_with_listview = (FloatingActionButton) findViewById(R.id.floating_action_button_fab_with_listview);
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
            jo.put("AccessToken", sessionManager.getSessionDeviceToken());

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

                                    /*acPmTicketList = response;
                                    if (acPmTicketList.getSitePMTicketsDates() != null && acPmTicketList.getSitePMTicketsDates().size() > 0) {
                                        txtNoTicketFound.setVisibility(View.GONE);
                                        pmSiteList_listView_siteList.setVisibility(View.VISIBLE);
                                        pmAcExpListAdapter = new PmAcExpListAdapter(AcPreventiveMaintenanceDashboardActivity.this, acPmTicketList);
                                        pmSiteList_listView_siteList.setAdapter(pmAcExpListAdapter);
                                        for (int i = 0; i < acPmTicketList.getSitePMTicketsDates().size(); i++) {
                                            pmSiteList_listView_siteList.expandGroup(i);
                                        }
                                    } else {
                                        pmSiteList_listView_siteList.setVisibility(View.GONE);
                                        txtNoTicketFound.setVisibility(View.VISIBLE);
                                    }*/

                                }
                            }
                        }
                    }, new Response.ErrorListener()

            {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideBusyProgress();

                }
            });
            getAssignAvailabilityLearnersListRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            getAssignAvailabilityLearnersListRequest.setShouldCache(false);
            Application.getInstance().

                    addToRequestQueue(getAssignAvailabilityLearnersListRequest, "MeetingListData");

        } catch (JSONException e) {
            hideBusyProgress();
            showToast("Something went wrong. Please try again later.");
        }

    }

}
