package com.edbrix.connectbrix.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.connectbrix.Application;
import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.adapters.ParticipantsListAdapter;
import com.edbrix.connectbrix.adapters.SchoolExpListAdapter;
import com.edbrix.connectbrix.baseclass.BaseActivity;
import com.edbrix.connectbrix.data.MeetingDetailsData;
import com.edbrix.connectbrix.data.MeetingListData;
import com.edbrix.connectbrix.utils.AuthConstants;
import com.edbrix.connectbrix.utils.Constants;
import com.edbrix.connectbrix.utils.SessionManager;
import com.edbrix.connectbrix.volley.GsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import us.zoom.sdk.InviteOptions;
import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.MeetingViewsOptions;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;
import us.zoom.sdk.ZoomSDKInitializeListener;

public class MeetingDetailsActivity extends BaseActivity implements AuthConstants, ZoomSDKInitializeListener, MeetingServiceListener, ZoomSDKAuthenticationListener {

    private static final String TAG = MeetingDetailsActivity.class.getName();
    private LinearLayout mMainLinear;
    private TextView mTxtMeetingTitle;
    private TextView mTxtMeetingDate;
    private TextView mTxtMeetingTime;
    private TextView mTxtMeetingDetails;
    private FrameLayout mBtns;
    private Button mBtnMAvaliable;
    private Button mBtnMJoin;
    private ListView mParticipantList;

    ArrayList<String> participantName = new ArrayList<>();
    ParticipantsListAdapter participantsListAdapter;

    SessionManager sessionManager;
    private String MeetingId = "", IsHost = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_details);
        getSupportActionBar().setTitle("Meeting Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        assignViews();

        participantName.add("Prasad Mane");
        participantName.add("Ram Patil");
        participantName.add("Raju Shirke");
        participantName.add("Amit Rane");
        participantName.add("Mohmmad Befari");

        sessionManager = new SessionManager(MeetingDetailsActivity.this);
        Intent intent = getIntent();
        MeetingId = intent.getStringExtra("MeetingId");
        IsHost = intent.getStringExtra("IsHost");

        invalidateOptionsMenu();
        if ((sessionManager.getSessionUserType().equals("T") || sessionManager.getSessionUserType().equals("A")) && IsHost.equals("1")) {
            mBtnMJoin.setText("Start");
        }

        //mTxtMeetingTitle.setText(intent.getStringExtra("meetingTitle"));
        //mTxtMDate.setText(intent.getStringExtra("meetingDate"));
        //mTxtMTime.setText(intent.getStringExtra("meetingScheduledTime"));

        participantsListAdapter = new ParticipantsListAdapter(MeetingDetailsActivity.this, participantName, sessionManager.getSessionUserType(), MeetingId, IsHost);
        mParticipantList.setAdapter(participantsListAdapter);

        /*ZoomSDK zoomSDK = ZoomSDK.getInstance();
        if(savedInstanceState == null) {
            zoomSDK.initialize(MeetingDetailsActivity.this, "qjDDhSsOzp5Ln0WSP0Z0LoKo86XFR4S2UIUn", "ePR5WENlisNzQVRJ8vrVeG0UGUsPza2iQ3xL", WEB_DOMAIN, this);
        }*/

        mBtnMJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Step 1: Get meeting number from input field.
                String meetingNo = "200395093";

                // Check if the meeting number is empty.
                if (meetingNo.length() == 0) {
                    Toast.makeText(MeetingDetailsActivity.this, "You need to enter a meeting number/ vanity id which you want to join.", Toast.LENGTH_LONG).show();
                    return;
                }
                // Step 2: Get Zoom SDK instance.
                ZoomSDK zoomSDK = ZoomSDK.getInstance();

                // Check if the zoom SDK is initialized
                if (!zoomSDK.isInitialized()) {
                    Toast.makeText(MeetingDetailsActivity.this, "ZoomSDK has not been initialized successfully", Toast.LENGTH_LONG).show();
                    return;
                }

                // Step 3: Get meeting service from zoom SDK instance.
                MeetingService meetingService = zoomSDK.getMeetingService();

                // Step 4: Configure meeting options.
                JoinMeetingOptions opts = new JoinMeetingOptions();

                // Some available options
                opts.no_driving_mode = false;
                opts.no_invite = false;
                opts.no_meeting_end_message = false;
                opts.no_titlebar = false;
                opts.no_bottom_toolbar = false;
                opts.no_dial_in_via_phone = true;
                opts.no_dial_out_to_phone = true;
                opts.no_disconnect_audio = false;
                opts.no_share = false;
                opts.invite_options = InviteOptions.INVITE_VIA_EMAIL + InviteOptions.INVITE_VIA_SMS + InviteOptions.INVITE_COPY_URL + InviteOptions.INVITE_ENABLE_ALL;
                opts.no_audio = true;
                opts.no_video = false;
                //  opts.meeting_views_options = MeetingViewsOptions.NO_BUTTON_SHARE + MeetingViewsOptions.NO_BUTTON_VIDEO;
                opts.no_meeting_error_message = true;
                opts.participant_id = "participant id";

                // Step 5: Setup join meeting parameters
                JoinMeetingParams params = new JoinMeetingParams();

                params.displayName = "Hello World From Zoom SDK";
                params.meetingNo = meetingNo;

                // Step 6: Call meeting service to join meeting
                meetingService.joinMeetingWithParams(MeetingDetailsActivity.this, params, opts);

            }
        });

    }

    private void assignViews() {
        mMainLinear = (LinearLayout) findViewById(R.id.mainLinear);
        mTxtMeetingTitle = (TextView) findViewById(R.id.txtMeetingTitle);
        mTxtMeetingDate = (TextView) findViewById(R.id.txtMeetingDate);
        mTxtMeetingTime = (TextView) findViewById(R.id.txtMeetingTime);
        mTxtMeetingDetails = (TextView) findViewById(R.id.txtMeetingDetails);
        // mBtns = (FrameLayout) findViewById(R.id.btns);
        // mBtnMAvaliable = (Button) findViewById(R.id.btnMAvaliable);
        mBtnMJoin = (Button) findViewById(R.id.btnMJoin);
        mParticipantList = (ListView) findViewById(R.id.participantList);
    }


    private void prepareListData() {
        try {
            showBusyProgress();
            JSONObject jo = new JSONObject();

            jo.put("APIKEY", sessionManager.getPrefsOrganizationApiKey());
            jo.put("SECRETKEY", sessionManager.getPrefsOrganizationSecretKey());
            jo.put("MeetingId", MeetingId);

            Log.i(MeetingDetailsActivity.class.getName(), Constants.getMeetingDetails + "\n\n" + jo.toString());

            GsonRequest<MeetingDetailsData> getAssignAvailabilityLearnersListRequest = new GsonRequest<>(Request.Method.POST, Constants.getMeetingDetails, jo.toString(), MeetingDetailsData.class,
                    new Response.Listener<MeetingDetailsData>() {
                        @Override
                        public void onResponse(@NonNull MeetingDetailsData response) {
                            hideBusyProgress();
                            if (response.getError() != null) {
                                showToast(response.getError().getErrorMessage());
                            } else {
                                if (response.getSuccess() == 1) {

                                    mTxtMeetingTitle.setText(response.getMeeting().getTitle() == null || response.getMeeting().getTitle().isEmpty() ? "" : response.getMeeting().getTitle());
                                    //mTxtMDate.setText(intent.getStringExtra("meetingDate"));
                                    //mTxtMTime.setText(intent.getStringExtra("meetingScheduledTime"));
                                    /*meetingListData = response;
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
                                    }*/
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
            Application.getInstance().addToRequestQueue(getAssignAvailabilityLearnersListRequest, "MeetingDetails");

        } catch (JSONException e) {
            hideBusyProgress();
            showToast("Something went wrong. Please try again later.");
        }

    }

    ////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.meeting_details_menu, menu);

        MenuItem shareItem = menu.findItem(R.id.menuEdit);

        // show the button when some condition is true
        shareItem.setVisible(false);
        if ((sessionManager.getSessionUserType().equals("T") || sessionManager.getSessionUserType().equals("A")) && IsHost.equals("1")) {
            shareItem.setVisible(true);
        }

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menuEdit:
                startActivity(new Intent(this, CreateMeetingActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMeetingStatusChanged(MeetingStatus meetingStatus, int errorCode, int internalErrorCode) {

    }

    @Override
    public void onZoomSDKLoginResult(long result) {

    }

    @Override
    public void onZoomSDKLogoutResult(long result) {

    }

    @Override
    public void onZoomIdentityExpired() {

    }

    @Override
    public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {

        Log.i(TAG, "onZoomSDKInitializeResult, errorCode=" + errorCode + ", internalErrorCode=" + internalErrorCode);

    }
}
