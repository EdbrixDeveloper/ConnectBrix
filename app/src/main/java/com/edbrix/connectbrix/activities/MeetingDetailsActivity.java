package com.edbrix.connectbrix.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.connectbrix.Application;
import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.adapters.ParticipantsListAdapter;
import com.edbrix.connectbrix.baseclass.BaseActivity;
import com.edbrix.connectbrix.commons.AlertDialogManager;
import com.edbrix.connectbrix.data.APIUserInfo;
import com.edbrix.connectbrix.data.MeetingDetailsData;
import com.edbrix.connectbrix.data.ParticipantList;
import com.edbrix.connectbrix.helper.APIUserInfoHelper;
import com.edbrix.connectbrix.helper.ApiUserStartMeetingHelper;
import com.edbrix.connectbrix.utils.AuthConstants;
import com.edbrix.connectbrix.utils.Constants;
import com.edbrix.connectbrix.utils.SessionManager;
import com.edbrix.connectbrix.volley.GsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.StringTokenizer;

import us.zoom.sdk.InviteOptions;
import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.MeetingStatus;
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
    private TextView mTxtQuestion;
    private TextView mTextViewParticipantCount;
    private Button btnMAddParticipants;
    private Button mBtnMJoin;
    private ListView mParticipantList;
    RadioButton radioMale;
    RadioButton radioFemale;
    String msgName = "join";
    private boolean mbPendingStartMeeting = false;

    private AlertDialogManager alertDialogManager;
    MeetingDetailsData meetingDetailsData;
    ArrayList<ParticipantList> participantArrayList = new ArrayList<>();
    ParticipantsListAdapter participantsListAdapter;

    SessionManager sessionManager;
    private String meetingDbId = "", MeetingId = "", IsHost = "", IsCalenderActivity = "";

    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;//by 008
    ParticipantsListAdapter.OnButtonActionListener onButtonActionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_details);
        getSupportActionBar().setTitle("Meeting Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        assignViews();

        sessionManager = new SessionManager(MeetingDetailsActivity.this);
        meetingDetailsData = new MeetingDetailsData();
        alertDialogManager = new AlertDialogManager(MeetingDetailsActivity.this);
        Intent intent = getIntent();
        meetingDbId = intent.getStringExtra("meetingDbId");
        /* MeetingId = intent.getStringExtra("MeetingId");*/
        IsHost = intent.getStringExtra("IsHost");
        IsCalenderActivity = intent.getStringExtra("IsCalenderActivity");

        invalidateOptionsMenu();
        fieldsVisibilityBasedOnUser();
        prepareListData();

        ZoomSDK zoomSDK = ZoomSDK.getInstance();
        if (savedInstanceState == null) {
            zoomSDK.initialize(MeetingDetailsActivity.this, "qjDDhSsOzp5Ln0WSP0Z0LoKo86XFR4S2UIUn", "ePR5WENlisNzQVRJ8vrVeG0UGUsPza2iQ3xL", WEB_DOMAIN, this);
        }

        mBtnMJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialogManager.Dialog("Conformation", "Do you want to " + msgName + " meeting?", "ok", "cancel", new AlertDialogManager.onTwoButtonClickListner() {
                    @Override
                    public void onPositiveClick() {

                        if ((sessionManager.getSessionUserType().equals("T") || sessionManager.getSessionUserType().equals("A")) && IsHost.equals("1")) {
                            startMeeting();
                        } else {
                            joinMeeting();
                        }
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                }).show();

            }
        });

        btnMAddParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(mTextViewParticipantCount.getText().toString()) < 20) {
                Intent intent = new Intent(MeetingDetailsActivity.this, FliterParticipantsActivity.class);
                intent.putExtra("meetingDbId", meetingDbId);
                intent.putExtra("IsHost", IsHost);
                startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
                //startActivity(new Intent(MeetingDetailsActivity.this, FliterParticipantsActivity.class));
                }else {
                    showToast("The participants in the meeting have exceeded the limit, if you want to add participants remove previous one.");
                }
            }
        });

        radioMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meetingAvilabilityStatus("1");
                //showToast("Radio Male");
            }
        });

        radioFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meetingAvilabilityStatus("0");
                //showToast("Radio Female");
            }
        });

        /*mParticipantList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String recordId = meetingDetailsData.getMeeting().getParticipantList().get(position).getRecordId();
                deleteSelectedParticipant(recordId);

            }

        });*/

        onButtonActionListener = new ParticipantsListAdapter.OnButtonActionListener() {
            @Override
            public void onButtonClicked(String ParticipantName, String RecordId, int Position) {

                alertDialogManager.Dialog("Conformation", "Do you want to remove " + ParticipantName + "?", "ok", "cancel", new AlertDialogManager.onTwoButtonClickListner() {
                    @Override
                    public void onPositiveClick() {
                        removeParticipant(RecordId, Position);
                    }

                    @Override
                    public void onNegativeClick() {
                    }
                }).show();
            }

        };


    }

    private void startMeeting() {


        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if (!zoomSDK.isInitialized()) {
            Toast.makeText(this, "ZoomSDK has not been initialized successfully", Toast.LENGTH_LONG).show();
            return;
        }

        final MeetingService meetingService = zoomSDK.getMeetingService();
        if (meetingService.getMeetingStatus() != MeetingStatus.MEETING_STATUS_IDLE) {

            long lMeetingNo = 0;
            try {
                lMeetingNo = Long.parseLong(MeetingId);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid meeting number: " + MeetingId, Toast.LENGTH_LONG).show();
                return;
            }

            if (meetingService.getCurrentRtcMeetingNumber() == lMeetingNo) {
                meetingService.returnToMeeting(this);
                return;
            }

            new AlertDialog.Builder(this)
                    .setMessage("Do you want to leave current meeting and start another?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mbPendingStartMeeting = true;
                            meetingService.leaveCurrentMeeting(false);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
            return;
        }

        int ret = -1;
        ret = ApiUserStartMeetingHelper.getInstance().startMeetingWithNumber(this, MeetingId);
        Log.i(TAG, "onClickBtnStartMeeting, ret=" + ret);
    }

    private void joinMeeting() {
        // Step 1: Get meeting number from input field.
        String meetingNo = MeetingId;//"200395093";

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

    public void fieldsVisibilityBasedOnUser() {
        if ((sessionManager.getSessionUserType().equals("T") || sessionManager.getSessionUserType().equals("A")) && IsHost.equals("1")) {
            mBtnMJoin.setText("Start Meeting");
            btnMAddParticipants.setVisibility(View.VISIBLE);
            mTxtQuestion.setVisibility(View.GONE);
            radioMale.setVisibility(View.GONE);
            radioFemale.setVisibility(View.GONE);
            msgName = "start";
        }
    }

    private void deleteSelectedParticipant(String RecordId) {
        alertDialogManager.Dialog("Conformation", "Do you want to remove participant?", "ok", "cancel", new AlertDialogManager.onTwoButtonClickListner() {
            @Override
            public void onPositiveClick() {
                removeParticipant(RecordId);
                //Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                //AcPreventiveMaintenanceDashboardActivity.this.startActivity(myIntent);
            }

            @Override
            public void onNegativeClick() {
                //Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                //AcPreventiveMaintenanceDashboardActivity.this.startActivity(myIntent);
            }
        }).show();
    }

    private void assignViews() {
        mMainLinear = (LinearLayout) findViewById(R.id.mainLinear);
        mTxtMeetingTitle = (TextView) findViewById(R.id.txtMeetingTitle);
        mTxtMeetingDate = (TextView) findViewById(R.id.txtMeetingDate);
        mTxtMeetingTime = (TextView) findViewById(R.id.txtMeetingTime);
        mTxtMeetingDetails = (TextView) findViewById(R.id.txtMeetingDetails);
        mTxtQuestion = (TextView) findViewById(R.id.txtQuestion);
        // mBtns = (FrameLayout) findViewById(R.id.btns);
        btnMAddParticipants = (Button) findViewById(R.id.btnMAddParticipants);
        mBtnMJoin = (Button) findViewById(R.id.btnMJoin);
        mParticipantList = (ListView) findViewById(R.id.participantList);

        radioMale = (RadioButton) findViewById(R.id.radioMale);
        radioFemale = (RadioButton) findViewById(R.id.radioFemale);
        mTextViewParticipantCount = (TextView) findViewById(R.id.textViewParticipantCount);
    }


    private void prepareListData() {
        try {
            showBusyProgress();
            JSONObject jo = new JSONObject();

            jo.put("APIKEY", sessionManager.getPrefsOrganizationApiKey());
            jo.put("SECRETKEY", sessionManager.getPrefsOrganizationSecretKey());
            jo.put("MeetingId", meetingDbId);

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
                                    meetingDetailsData = response;
                                    mTxtMeetingTitle.setText(meetingDetailsData.getMeeting().getTitle() == null || meetingDetailsData.getMeeting().getTitle().isEmpty() ? "" : meetingDetailsData.getMeeting().getTitle());

                                    if (meetingDetailsData.getMeeting().getStartDateTime() == null || meetingDetailsData.getMeeting().getStartDateTime().isEmpty()) {
                                        mTxtMeetingDate.setText("");
                                        mTxtMeetingTime.setText("00:00 am");
                                    } else {
                                        StringTokenizer tk = new StringTokenizer(meetingDetailsData.getMeeting().getStartDateTime());
                                        String date = tk.nextToken();
                                        String time = tk.nextToken();
                                        String amPm = tk.nextToken();
                                        //SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                                        //SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm a");
                                        //Date dt;
                                        try {
                                            //dt = sdf.parse(time);
                                            mTxtMeetingDate.setText(date);
                                            mTxtMeetingTime.setText(time + " " + amPm);//sdfs.format(dt)
                                            //System.out.println("Time Display: " + sdfs.format(dt)); // <-- I got result here
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                    mTxtMeetingDetails.setText(meetingDetailsData.getMeeting().getAgenda() == null || meetingDetailsData.getMeeting().getAgenda().isEmpty() ? "" : meetingDetailsData.getMeeting().getAgenda());
                                    if (meetingDetailsData.getMeeting().getParticipantList() != null && meetingDetailsData.getMeeting().getParticipantList().size() > 0) {
                                        mTextViewParticipantCount.setText(meetingDetailsData.getMeeting().getParticipantCount());
                                        MeetingId = meetingDetailsData.getMeeting().getMeetingId();
                                        participantArrayList = new ArrayList<>();
                                        participantArrayList = meetingDetailsData.getMeeting().getParticipantList();
                                        participantsListAdapter = new ParticipantsListAdapter(MeetingDetailsActivity.this, participantArrayList, sessionManager.getSessionUserType(), meetingDbId, IsHost, onButtonActionListener);
                                        mParticipantList.setAdapter(participantsListAdapter);
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
            Application.getInstance().addToRequestQueue(getAssignAvailabilityLearnersListRequest, "MeetingDetails");

        } catch (JSONException e) {
            hideBusyProgress();
            showToast("Something went wrong. Please try again later.");
        }

    }

    private void removeParticipant(String RecordId) {
        try {
            showBusyProgress();
            JSONObject jo = new JSONObject();

            jo.put("APIKEY", sessionManager.getPrefsOrganizationApiKey());
            jo.put("SECRETKEY", sessionManager.getPrefsOrganizationSecretKey());
            jo.put("MeetingId", meetingDbId);
            jo.put("RecordId", RecordId);

            Log.i(MeetingDetailsActivity.class.getName(), Constants.deleteMeetingParticipant + "\n\n" + jo.toString());

            GsonRequest<MeetingDetailsData> getAssignAvailabilityLearnersListRequest = new GsonRequest<>(Request.Method.POST, Constants.deleteMeetingParticipant, jo.toString(), MeetingDetailsData.class,
                    new Response.Listener<MeetingDetailsData>() {
                        @Override
                        public void onResponse(@NonNull MeetingDetailsData response) {
                            hideBusyProgress();
                            if (response.getError() != null) {
                                showToast(response.getError().getErrorMessage());
                            } else {
                                if (response.getSuccess() == 1) {
                                    showToast("Removed Participant");
                                    prepareListData();
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
            Application.getInstance().addToRequestQueue(getAssignAvailabilityLearnersListRequest, "deleteMeetingParticipant");

        } catch (JSONException e) {
            hideBusyProgress();
            showToast("Something went wrong. Please try again later.");
        }

    }

    private void meetingAvilabilityStatus(String StatusFlag) {
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
                onBackPressed();
                //finish();
                return true;
            case R.id.menuEdit:
                Intent intent = new Intent(this, CreateMeetingActivity.class);
                intent.putExtra("comesFor", "edit");
                intent.putExtra("meetingId", meetingDbId);
                intent.putExtra("meetingTitle", mTxtMeetingTitle.getText().toString());
                intent.putExtra("meetingDateTime", mTxtMeetingDate.getText().toString() + " " + mTxtMeetingTime.getText().toString());
                intent.putExtra("meetingAgenda", mTxtMeetingDetails.getText().toString());
                intent.putExtra("isHost", IsHost);
                startActivity(intent);
                //startActivity(new Intent(this, FliterParticipantsActivity.class));
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

    //by 008
    // This method is called when the second activity finishes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that it is the SecondActivity with an OK result
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                meetingDbId = data.getStringExtra("meetingDbId");
                /* MeetingId = data.getStringExtra("MeetingId");*/
                IsHost = data.getStringExtra("IsHost");

                invalidateOptionsMenu();
                fieldsVisibilityBasedOnUser();
                prepareListData();

            }
        }
    }

    @Override
    public void onBackPressed() {
        if (IsCalenderActivity.equals("Y")) {
            finish();
        } else {
            Intent mIntent = new Intent(mContext, SchoolListActivity.class);
            finishAffinity();
            startActivity(mIntent);
            super.onBackPressed();
        }

    }


    private void removeParticipant(String RecordId, int position) {
        try {
            showBusyProgress();
            JSONObject jo = new JSONObject();

            jo.put("APIKEY", sessionManager.getPrefsOrganizationApiKey());
            jo.put("SECRETKEY", sessionManager.getPrefsOrganizationSecretKey());
            jo.put("MeetingId", meetingDbId);
            jo.put("RecordId", RecordId);

            Log.i(MeetingDetailsActivity.class.getName(), Constants.deleteMeetingParticipant + "\n\n" + jo.toString());

            GsonRequest<MeetingDetailsData> getAssignAvailabilityLearnersListRequest = new GsonRequest<>(Request.Method.POST, Constants.deleteMeetingParticipant, jo.toString(), MeetingDetailsData.class,
                    new Response.Listener<MeetingDetailsData>() {
                        @Override
                        public void onResponse(@NonNull MeetingDetailsData response) {
                            if (response.getError() != null) {
                                showToast(response.getError().getErrorMessage());
                            } else {
                                hideBusyProgress();
                                if (response.getSuccess() == 1) {
                                    //showToast("Removed "+participantList.get(position).getName());
                                    showToast("Removed selected participant");
                                    //participantList.remove(position);
                                    //notifyDataSetChanged();
                                    prepareListData();
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
            Application.getInstance().addToRequestQueue(getAssignAvailabilityLearnersListRequest, "deleteMeetingParticipant");

        } catch (JSONException e) {
            hideBusyProgress();
            showToast("Something went wrong. Please try again later.");
        }

    }

    ///////////////////////////////////////////////// Start Meeting/////////////////////////////////////////////////////////////
    private class RetrieveUserInfoTask extends AsyncTask<String, Void, APIUserInfo> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showBusyProgress();
        }

        @Override
        protected APIUserInfo doInBackground(String... params) {
            String token = APIUserInfoHelper.getZoomToken(params[0]);
            String accessToken = APIUserInfoHelper.getZoomAccessToken(params[0]);

            if (token != null && !token.isEmpty() && accessToken != null && !accessToken.isEmpty()) {
                APIUserInfo apiUserInfo = new APIUserInfo(params[0], token, accessToken);
                APIUserInfoHelper.saveAPIUserInfo(apiUserInfo);
                return apiUserInfo;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(APIUserInfo apiUserInfo) {
            super.onPostExecute(apiUserInfo);
            hideBusyProgress();
            if (apiUserInfo == null)
                Toast.makeText(MeetingDetailsActivity.this, "Faild to retrieve Api user info!", Toast.LENGTH_LONG).show();
        }
    }

}
