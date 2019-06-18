package com.edbrix.connectbrix.activities;

import android.content.Intent;
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
import com.edbrix.connectbrix.data.MeetingListData;
import com.edbrix.connectbrix.utils.Constants;
import com.edbrix.connectbrix.utils.SessionManager;
import com.edbrix.connectbrix.volley.GsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

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

    FloatingActionButton floating_action_button_fab_with_listview;
    boolean doubleBackToExitPressedOnce = false;
    SessionManager sessionManager;
    private MeetingListData meetingListData;

    private MyContinousAsyncTask myContinouslyRunningAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        sessionManager = new SessionManager(SchoolListActivity.this);

        floating_action_button_fab_with_listview.hide();
        if (sessionManager.getSessionUserType().equals("T") || sessionManager.getSessionUserType().equals("A")) {
            floating_action_button_fab_with_listview.show();
        }

        //prepareListData();

        imgCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SchoolListActivity.this, CalenderViewMeetingListActivity.class));
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

                if (meetingListData != null) {

                    final String meetingDbId = meetingListData.getUserMeetingsDates().get(groupPosition).getUserMeetings().get(childPosition).getId() == null ? "" : meetingListData.getUserMeetingsDates().get(groupPosition).getUserMeetings().get(childPosition).getId().toString();
                    final String meetingId = meetingListData.getUserMeetingsDates().get(groupPosition).getUserMeetings().get(childPosition).getMeetingId() == null ? "" : meetingListData.getUserMeetingsDates().get(groupPosition).getUserMeetings().get(childPosition).getMeetingId().toString();
                    final String isHost = meetingListData.getUserMeetingsDates().get(groupPosition).getUserMeetings().get(childPosition).getIsHost() == null ? "" : meetingListData.getUserMeetingsDates().get(groupPosition).getUserMeetings().get(childPosition).getIsHost().toString();

                    goToEditingMeetingDetails(meetingDbId, meetingId, isHost);
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
                startActivity(intent);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                prepareListData();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

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
        intent.putExtra("IsCalenderActivity", "N");
        startActivity(intent);
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

    private void prepareListData() {
        try {
            //showBusyProgress();
            JSONObject jo = new JSONObject();

            jo.put("UserId", sessionManager.getSessionUserId());
            jo.put("APIKEY", sessionManager.getPrefsOrganizationApiKey());
            jo.put("SECRETKEY", sessionManager.getPrefsOrganizationSecretKey());

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        invalidateOptionsMenu();
        if (requestCode == RESULT_UPDATE_PROFILE && resultCode == RESULT_OK) {
            setImageToUserProfileIcon();
        }

        /*if (resultCode == RESULT_OK) {
            prepareListData();
        }*/
    }

    //clock
    private Timer timer;
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
                            //tvPontuacao.setText(BatalhaConfigs.USUARIO_PONTUACAO);
                            //getAllBattles();
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

    /**
     * timer para atualizar o adapter
     */
    private void clockTask() {
        myContinouslyRunningAsyncTask = new MyContinousAsyncTask();
        myContinouslyRunningAsyncTask.execute("execute");

    }
}


