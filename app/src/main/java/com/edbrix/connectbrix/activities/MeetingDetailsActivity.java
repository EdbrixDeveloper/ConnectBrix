package com.edbrix.connectbrix.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.edbrix.connectbrix.helper.ZoomMeetingUISettingHelper;
import com.edbrix.connectbrix.utils.AuthConstants;
import com.edbrix.connectbrix.utils.Constants;
import com.edbrix.connectbrix.utils.SessionManager;
import com.edbrix.connectbrix.volley.GsonRequest;
import com.edbrix.connectbrix.utils.Constants.*;

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
import us.zoom.sdk.StartMeetingOptions;
import us.zoom.sdk.StartMeetingParams4NormalUser;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;
import us.zoom.sdk.ZoomSDKInitializeListener;

public class MeetingDetailsActivity extends BaseActivity implements AuthConstants, ZoomSDKInitializeListener, MeetingServiceListener, ZoomSDKAuthenticationListener {

    private static final String TAG = MeetingDetailsActivity.class.getName();
    private LinearLayout mMainLinear;
    private TextView mTxtMeetingTitle;
    private TextView txtMeetingId;
    private TextView mTxtMeetingDate;
    private TextView mTxtMeetingTime;
    private TextView mTxtMeetingDetails;
    private TextView mTxtQuestion;
    private TextView mTextViewParticipantCount;
    private Button btnMAddParticipants;
    private Button mBtnMJoin;
    private LinearLayout btns;
    private ListView mParticipantList;
    private ImageView mMeetingListImg;
    private TextView mTxtDataFound;
    private SwipeRefreshLayout swipeToRefreshInMeetingDetails;

    RadioButton radioMale;
    RadioButton radioFemale;
    RadioGroup radioSex;
    String msgName = "join";
    private boolean mbPendingStartMeeting = false;

    private AlertDialogManager alertDialogManager;
    MeetingDetailsData meetingDetailsData;
    ArrayList<ParticipantList> participantArrayList = new ArrayList<>();
    ParticipantsListAdapter participantsListAdapter;

    SessionManager sessionManager;
    private String meetingDbId = "", MeetingId = "", isAvailable = "", IsHost = "", RefreshFlag = "", IsCalenderActivity = "";

    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;//by 008
    ParticipantsListAdapter.OnButtonActionListener onButtonActionListener;

    int CheckedFlag = 0;
    private boolean isResumed = false;

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
        isAvailable = intent.getStringExtra("isAvailable");
        IsHost = intent.getStringExtra("IsHost");
        RefreshFlag = intent.getStringExtra("RefreshFlag");
        IsCalenderActivity = intent.getStringExtra("IsCalenderActivity");

        invalidateOptionsMenu();
        fieldsVisibilityBasedOnUser();

        ZoomSDK zoomSDK = ZoomSDK.getInstance();
        if (savedInstanceState == null) {
            prepareListData();
            zoomSDK.initialize(MeetingDetailsActivity.this, "qjDDhSsOzp5Ln0WSP0Z0LoKo86XFR4S2UIUn", "ePR5WENlisNzQVRJ8vrVeG0UGUsPza2iQ3xL", WEB_DOMAIN, this);
        } else {
            participantArrayList = (ArrayList<ParticipantList>) savedInstanceState.getSerializable("participantArrayList");

            meetingDbId = savedInstanceState.getString("meetingDbId");
            isAvailable = savedInstanceState.getString("isAvailable");
            IsHost = savedInstanceState.getString("IsHost");
            RefreshFlag = savedInstanceState.getString("RefreshFlag");
            IsCalenderActivity = savedInstanceState.getString("IsCalenderActivity");

            msgName = savedInstanceState.getString("msgName");
            mbPendingStartMeeting = savedInstanceState.getBoolean("mbPendingStartMeeting");
            MeetingId = savedInstanceState.getString("MeetingId");
            CheckedFlag = savedInstanceState.getInt("MeetingId");


            mTxtMeetingTitle.setText(savedInstanceState.getString("mTxtMeetingTitle"));
            mTxtMeetingDate.setText(savedInstanceState.getString(" mTxtMeetingDate"));
            mTxtMeetingTime.setText(savedInstanceState.getString("mTxtMeetingTime"));
            mTxtMeetingDetails.setText(savedInstanceState.getString("mTxtMeetingDetails"));
            mTextViewParticipantCount.setText(savedInstanceState.getString("mTextViewParticipantCount"));

            if (participantArrayList != null && participantArrayList.size() > 0) {
                if (Integer.parseInt(mTextViewParticipantCount.getText().toString().isEmpty() ? "0" : mTextViewParticipantCount.getText().toString()) > 0 && isAvailable.equals("1")) {
                    mBtnMJoin.setVisibility(View.VISIBLE);
                    btns.setVisibility(View.VISIBLE);
                }
                mMeetingListImg.setVisibility(View.GONE);
                mTxtDataFound.setVisibility(View.GONE);
                mParticipantList.setVisibility(View.VISIBLE);
                participantsListAdapter = new ParticipantsListAdapter(MeetingDetailsActivity.this, participantArrayList, sessionManager.getSessionUserType(), meetingDbId, IsHost, onButtonActionListener);
                mParticipantList.setAdapter(participantsListAdapter);
                participantsListAdapter.notifyDataSetChanged();

            } else {
                mMeetingListImg.setVisibility(View.VISIBLE);
                mTxtDataFound.setVisibility(View.VISIBLE);
                mParticipantList.setVisibility(View.GONE);
                btns.setVisibility(View.GONE);
            }
        }

        /*if (isAvailable.equals("1")) {
            radioMale.setChecked(true);
            radioFemale.setChecked(false);
            mBtnMJoin.setVisibility(View.VISIBLE);
        } else if (isAvailable.equals("2")) {
            radioMale.setChecked(false);
            radioFemale.setChecked(true);
            mBtnMJoin.setVisibility(View.GONE);
        }*/
        mBtnMJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialogManager.Dialog("Confirmation", "Do you want to " + msgName + " meeting?", "ok", "cancel", new AlertDialogManager.onTwoButtonClickListner() {
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
                String Count = mTextViewParticipantCount.getText().toString().isEmpty() ? "0" : mTextViewParticipantCount.getText().toString();
                if (Integer.parseInt(Count) < 20) {
                    Intent intent = new Intent(MeetingDetailsActivity.this, FliterParticipantsActivity.class);
                    intent.putExtra("meetingDbId", meetingDbId);
                    intent.putExtra("IsHost", IsHost);
                    startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
                    //startActivity(new Intent(MeetingDetailsActivity.this, FliterParticipantsActivity.class));
                } else {
                    showToast("The participants in the meeting have exceeded the limit, if you want to add participants remove previous one.");
                }
            }
        });

        radioMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CheckedFlag == 0 || CheckedFlag == 2) {
                    CheckedFlag = 1;
                    mBtnMJoin.setVisibility(View.VISIBLE);
                    btns.setVisibility(View.VISIBLE);
                    meetingAvilabilityStatus("1");
                    //showToast("Radio Male");
                }
            }
        });

        radioFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckedFlag == 0 || CheckedFlag == 1) {
                    CheckedFlag = 2;
                    mBtnMJoin.setVisibility(View.GONE);
                    btns.setVisibility(View.GONE);
                    meetingAvilabilityStatus("2");
                    //showToast("Radio Female");
                }
            }
        });

        onButtonActionListener = new ParticipantsListAdapter.OnButtonActionListener() {
            @Override
            public void onButtonClicked(String ParticipantName, String RecordId, int Position) {

                alertDialogManager.Dialog("Confirmation", "Do you want to remove " + ParticipantName + "?", "ok", "cancel", new AlertDialogManager.onTwoButtonClickListner() {
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

        swipeToRefreshInMeetingDetails.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                prepareListData();
                swipeToRefreshInMeetingDetails.setRefreshing(false);
            }
        });

        mMeetingListImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Count = mTextViewParticipantCount.getText().toString().isEmpty() ? "0" : mTextViewParticipantCount.getText().toString();
                if (Integer.parseInt(Count) < 20) {
                    Intent intent = new Intent(MeetingDetailsActivity.this, FliterParticipantsActivity.class);
                    intent.putExtra("meetingDbId", meetingDbId);
                    intent.putExtra("IsHost", IsHost);
                    startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
                    //startActivity(new Intent(MeetingDetailsActivity.this, FliterParticipantsActivity.class));
                } else {
                    showToast("The participants in the meeting have exceeded the limit, if you want to add participants remove previous one.");
                }

            }
        });
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

        /*StartMeetingOptions opts = ZoomMeetingUISettingHelper.getMeetingOptions();

        StartMeetingParams4NormalUser params = new StartMeetingParams4NormalUser();
        params.meetingNo = MeetingId;

        meetingService.startMeetingWithParams(getApplicationContext(), params, opts);*/

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

        params.displayName = sessionManager.getSessionUserFirstName() + " " + sessionManager.getSessionUserFirstLast();
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
            radioSex.setVisibility(View.GONE);
            msgName = "start";
        }
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
        btns = (LinearLayout)findViewById(R.id.btns);
        mParticipantList = (ListView) findViewById(R.id.participantList);

        mMeetingListImg = (ImageView) findViewById(R.id.meetingListImg);
        mTxtDataFound = (TextView) findViewById(R.id.txtDataFound);

        radioMale = (RadioButton) findViewById(R.id.radioMale);
        radioFemale = (RadioButton) findViewById(R.id.radioFemale);
        radioSex = (RadioGroup) findViewById(R.id.radioSex);
        mTextViewParticipantCount = (TextView) findViewById(R.id.textViewParticipantCount);
        txtMeetingId = (TextView) findViewById(R.id.txtMeetingID);
        swipeToRefreshInMeetingDetails = (SwipeRefreshLayout)findViewById(R.id.swipeToRefreshInMeetingDetails);
    }


    private void prepareListData() {
        try {
            //showBusyProgress();
            JSONObject jo = new JSONObject();

            jo.put("AccessToken", sessionManager.getPrefsSessionAccessToken());
            jo.put("UserId", sessionManager.getSessionUserId());
            jo.put("MeetingId", meetingDbId);

            Log.i(MeetingDetailsActivity.class.getName(), Constants.getMeetingDetails + "\n\n" + jo.toString());

            GsonRequest<MeetingDetailsData> getAssignAvailabilityLearnersListRequest = new GsonRequest<>(Request.Method.POST, Constants.getMeetingDetails, jo.toString(), MeetingDetailsData.class,
                    new Response.Listener<MeetingDetailsData>() {
                        @Override
                        public void onResponse(@NonNull MeetingDetailsData response) {
                            //hideBusyProgress();
                            if (response.getError() != null) {
                                showToast(response.getError().getErrorMessage());
                            } else {
                                if (response.getSuccess() == 1) {
                                    String str_HostId = response.getMeeting().getHostId() == null ? "" : response.getMeeting().getHostId();
                                    Log.i(TAG + "HOST_ID : ", str_HostId);
                                    meetingDetailsData = response;
                                    Constants.HOST_ID = str_HostId;//meetingDetailsData.getMeeting().getHostId();
                                    if (Constants.HOST_ID != null) {
                                        executeBackgroundTaskForUserInfo();
                                    }
                                    String meetingID = meetingDetailsData.getMeeting().getMeetingId() == null || meetingDetailsData.getMeeting().getMeetingId().isEmpty() ? "Meeting ID: "+"" : "Meeting ID: "+meetingDetailsData.getMeeting().getMeetingId();
                                    txtMeetingId.setText(meetingID);
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
                                    if (Integer.parseInt(meetingDetailsData.getMeeting().getParticipantCount()) > 0 /*&& isAvailable.equals("1")*/) {
                                        mBtnMJoin.setVisibility(View.VISIBLE);
                                        btns.setVisibility(View.VISIBLE);
                                    }else {
                                        mBtnMJoin.setVisibility(View.GONE);
                                        btns.setVisibility(View.GONE);
                                    }
                                    mTxtMeetingDetails.setText(meetingDetailsData.getMeeting().getAgenda() == null || meetingDetailsData.getMeeting().getAgenda().isEmpty() ? "" : meetingDetailsData.getMeeting().getAgenda());
                                    if (meetingDetailsData.getMeeting().getParticipantList() != null && meetingDetailsData.getMeeting().getParticipantList().size() > 0) {
                                        mTextViewParticipantCount.setText(meetingDetailsData.getMeeting().getParticipantCount());

                                        MeetingId = meetingDetailsData.getMeeting().getMeetingId();
                                        mMeetingListImg.setVisibility(View.GONE);
                                        mTxtDataFound.setVisibility(View.GONE);
                                        mParticipantList.setVisibility(View.VISIBLE);
                                        participantArrayList = new ArrayList<>();
                                        // for host user,shows all participant user list &
                                        // for participant user, shows only request accepted participant user list....
                                        if (IsHost.equals("1")) {
                                            participantArrayList = meetingDetailsData.getMeeting().getParticipantList();
                                        } else {
                                            participantArrayList.clear();
                                            for (ParticipantList participantList : meetingDetailsData.getMeeting().getParticipantList()) {
                                                if (participantList.getStatus().equals("1")) {
                                                    participantArrayList.add(participantList);
                                                }
                                            }
                                            mTextViewParticipantCount.setText("");
                                            String ss = String.valueOf(participantArrayList.size());
                                            if (participantArrayList.size() > 0) {
                                                mTextViewParticipantCount.setText(ss);
                                            }

                                        }
                                        participantsListAdapter = new ParticipantsListAdapter(MeetingDetailsActivity.this, participantArrayList, sessionManager.getSessionUserType(), meetingDbId, IsHost, onButtonActionListener);
                                        mParticipantList.setAdapter(participantsListAdapter);
                                        participantsListAdapter.notifyDataSetChanged();

                                    } else {
                                        mMeetingListImg.setVisibility(View.VISIBLE);
                                        mTxtDataFound.setVisibility(View.VISIBLE);
                                        mParticipantList.setVisibility(View.GONE);
                                        mTextViewParticipantCount.setText("0");
                                        /*mTextViewParticipantCount.setText(meetingDetailsData.getMeeting().getParticipantCount());
                                        participantArrayList = new ArrayList<>();
                                        participantsListAdapter = new ParticipantsListAdapter(MeetingDetailsActivity.this, participantArrayList, sessionManager.getSessionUserType(), meetingDbId, IsHost, onButtonActionListener);
                                        mParticipantList.setAdapter(participantsListAdapter);
                                        participantsListAdapter.notifyDataSetChanged();*/

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
            Application.getInstance().addToRequestQueue(getAssignAvailabilityLearnersListRequest, "MeetingDetails");

        } catch (JSONException e) {
            //hideBusyProgress();
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
        MenuItem shareItem2 = menu.findItem(R.id.menuDelete);

        // show the button when some condition is true
        shareItem.setVisible(false);
        shareItem2.setVisible(false);
        if ((sessionManager.getSessionUserType().equals("T") || sessionManager.getSessionUserType().equals("A")) && IsHost.equals("1")) {
            shareItem.setVisible(true);
            shareItem2.setVisible(true);
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
                intent.putExtra("IsHost", IsHost);
                intent.putExtra("isAvailable", isAvailable);
                intent.putExtra("IsCalenderActivity", IsCalenderActivity);
                startActivity(intent);
                finish();
                //finish();25062019 by008
                //startActivity(new Intent(this, FliterParticipantsActivity.class));
                return true;

            case R.id.menuDelete:
                showConfirmationDialogForDeletionMeetingByHost();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showConfirmationDialogForDeletionMeetingByHost() {
        alertDialogManager.Dialog("Confirmation", "Do you want to delete this meeting?", "ok", "cancel", new AlertDialogManager.onTwoButtonClickListner() {
            @Override
            public void onPositiveClick() {
                deleteMeetingByHostUser();
            }

            @Override
            public void onNegativeClick() {
            }
        }).show();
    }

    private void deleteMeetingByHostUser() {
        try {
            showBusyProgress();
            JSONObject jo = new JSONObject();

            jo.put("AccessToken", sessionManager.getPrefsSessionAccessToken());
            jo.put("UserId", sessionManager.getSessionUserId());
            jo.put("MeetingId", meetingDbId);
            //jo.put("UserId", sessionManager.getSessionUserId());

            Log.i(MeetingDetailsActivity.class.getName(), Constants.deleteMeetingDetails + "\n\n" + jo.toString());

            GsonRequest<MeetingDetailsData> deleteMeetingRequest = new GsonRequest<>(Request.Method.POST, Constants.deleteMeetingDetails, jo.toString(), MeetingDetailsData.class,
                    new Response.Listener<MeetingDetailsData>() {
                        @Override
                        public void onResponse(@NonNull MeetingDetailsData response) {
                            hideBusyProgress();
                            if (response.getError() != null) {
                                showToast(response.getError().getErrorMessage());
                            } else {
                                if (response.getSuccess() == 1) {
                                    RefreshFlag = "Y";
                                    onBackPressed();
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideBusyProgress();

                }
            });
            deleteMeetingRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            deleteMeetingRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(deleteMeetingRequest, "DeleteMeetingDetails");

        } catch (JSONException e) {
            hideBusyProgress();
            showToast("Something went wrong. Please try again later.");
        }

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
                /*btns.setVisibility(View.VISIBLE);
                mBtnMJoin.setVisibility(View.VISIBLE);*/

                meetingDbId = data.getStringExtra("meetingDbId");
                /* MeetingId = data.getStringExtra("MeetingId");*/
                IsHost = data.getStringExtra("IsHost");
                RefreshFlag = data.getStringExtra("RefreshFlag");
                invalidateOptionsMenu();
                fieldsVisibilityBasedOnUser();
                prepareListData();



            }
        }
    }

    @Override
    public void onBackPressed() {
        if (IsCalenderActivity.equals("Y")) {
            if (RefreshFlag.equals("Y")) {

                Intent resultIntent = new Intent();
                // TODO Add extras or a data URI to this intent as appropriate.
                resultIntent.putExtra("RefreshFlag", "Y");
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            } else {
                finish();
            }
        } else if (IsCalenderActivity.equals("YC")) {
            launchEvent2();
        } else if (IsCalenderActivity.equals("C")) {
            launchEvent();
        } else {
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

    }


    final String eventName = "com.edbrix.connectbrix.EVENT";
    final String eventName2 = "com.edbrix.connectbrix.CALEN";

    private void launchEvent() {
        Intent eventIntent = new Intent(eventName);
        eventIntent.putExtra("RefreshFlag", "Y");
        this.sendBroadcast(eventIntent);
        finish();
    }

    private void launchEvent2() {
        Intent eventIntent = new Intent(eventName2);
        eventIntent.putExtra("RefreshFlag", "Y");
        this.sendBroadcast(eventIntent);
        finish();
    }

    private void removeParticipant(String RecordId, int position) {
        try {
            showBusyProgress();
            JSONObject jo = new JSONObject();

            /*jo.put("APIKEY", sessionManager.getPrefsOrganizationApiKey());
            jo.put("SECRETKEY", sessionManager.getPrefsOrganizationSecretKey());*/
            jo.put("AccessToken", sessionManager.getPrefsSessionAccessToken());
            jo.put("UserId", sessionManager.getSessionUserId());
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
                                    RefreshFlag = "Y";
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
            if (apiUserInfo == null) {
                //Toast.makeText(MeetingDetailsActivity.this, "Faild to retrieve Api user info!", Toast.LENGTH_LONG).show();
            }

        }
    }

    /*@Override
    protected void onPause() {
        super.onPause();
        isResumed = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResumed = true;

        if (APIUserInfoHelper.getAPIUserInfo() == null) {
            RetrieveUserInfoTask task = new RetrieveUserInfoTask();//retrieve api user token
            task.execute(Constants.HOST_ID);
        }
    }*/

    private void executeBackgroundTaskForUserInfo() {
        if (APIUserInfoHelper.getAPIUserInfo() == null) {
            RetrieveUserInfoTask task = new RetrieveUserInfoTask();//retrieve api user token
            task.execute(Constants.HOST_ID);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("participantArrayList", participantArrayList);

        outState.putString("meetingDbId", meetingDbId);
        outState.putString("isAvailable", isAvailable);
        outState.putString("IsHost", IsHost);
        outState.putString("RefreshFlag", RefreshFlag);
        outState.putString("IsCalenderActivity", IsCalenderActivity);

        outState.putString("msgName", msgName);
        outState.putBoolean("mbPendingStartMeeting", mbPendingStartMeeting);
        outState.putString("MeetingId", MeetingId);
        outState.putInt("MeetingId", CheckedFlag);


        outState.putString("mTxtMeetingTitle", mTxtMeetingTitle.getText().toString());
        outState.putString(" mTxtMeetingDate", mTxtMeetingDate.getText().toString());
        outState.putString("mTxtMeetingTime", mTxtMeetingTime.getText().toString());
        outState.putString("mTxtMeetingDetails", mTxtMeetingDetails.getText().toString());
        outState.putString("mTextViewParticipantCount", mTextViewParticipantCount.getText().toString());


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        participantArrayList = (ArrayList<ParticipantList>) savedInstanceState.getSerializable("participantArrayList");

        meetingDbId = savedInstanceState.getString("meetingDbId");
        isAvailable = savedInstanceState.getString("isAvailable");
        IsHost = savedInstanceState.getString("IsHost");
        RefreshFlag = savedInstanceState.getString("RefreshFlag");
        IsCalenderActivity = savedInstanceState.getString("IsCalenderActivity");

        msgName = savedInstanceState.getString("msgName");
        mbPendingStartMeeting = savedInstanceState.getBoolean("mbPendingStartMeeting");
        MeetingId = savedInstanceState.getString("MeetingId");
        CheckedFlag = savedInstanceState.getInt("MeetingId");


        mTxtMeetingTitle.setText(savedInstanceState.getString("mTxtMeetingTitle"));
        mTxtMeetingDate.setText(savedInstanceState.getString(" mTxtMeetingDate"));
        mTxtMeetingTime.setText(savedInstanceState.getString("mTxtMeetingTime"));
        mTxtMeetingDetails.setText(savedInstanceState.getString("mTxtMeetingDetails"));
        mTextViewParticipantCount.setText(savedInstanceState.getString("mTextViewParticipantCount"));

        if (participantArrayList != null && participantArrayList.size() > 0) {
            if (Integer.parseInt(mTextViewParticipantCount.getText().toString().isEmpty() ? "0" : mTextViewParticipantCount.getText().toString()) > 0 && isAvailable.equals("1")) {
                mBtnMJoin.setVisibility(View.VISIBLE);
                btns.setVisibility(View.VISIBLE);
            }
            mMeetingListImg.setVisibility(View.GONE);
            mTxtDataFound.setVisibility(View.GONE);
            mParticipantList.setVisibility(View.VISIBLE);
            participantsListAdapter = new ParticipantsListAdapter(MeetingDetailsActivity.this, participantArrayList, sessionManager.getSessionUserType(), meetingDbId, IsHost, onButtonActionListener);
            mParticipantList.setAdapter(participantsListAdapter);
            participantsListAdapter.notifyDataSetChanged();

        } else {
            mMeetingListImg.setVisibility(View.VISIBLE);
            mTxtDataFound.setVisibility(View.VISIBLE);
            mParticipantList.setVisibility(View.GONE);
            btns.setVisibility(View.GONE);
        }

    }

}
