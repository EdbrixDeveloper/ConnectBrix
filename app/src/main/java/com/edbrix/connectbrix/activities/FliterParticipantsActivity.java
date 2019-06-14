package com.edbrix.connectbrix.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.connectbrix.Application;
import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.adapters.FliteredParticipantsAdapter;
import com.edbrix.connectbrix.baseclass.BaseActivity;
import com.edbrix.connectbrix.data.MeetingOrganizationList;
import com.edbrix.connectbrix.data.MeetingOrganizationListData;
import com.edbrix.connectbrix.data.MeetingParticipantListData;
import com.edbrix.connectbrix.helper.OnSpinnerItemClick;
import com.edbrix.connectbrix.helper.SearchableSpinnerDialog;
import com.edbrix.connectbrix.utils.Constants;
import com.edbrix.connectbrix.utils.SessionManager;
import com.edbrix.connectbrix.volley.GsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class FliterParticipantsActivity extends BaseActivity {

    private LinearLayout mLinearSearch;
    private TextView mTxtComboCampus;
    private TextView mTxtComboType;
    private TextView mTxtComboStudent;
    private TextInputLayout mInputLayoutSearch;
    private EditText mInputSearch;
    private ListView mSelectParticipantList;
    private FrameLayout mBtns;
    private Button mBtnSelect;

    String str_typeOfParticipant;
    ArrayList<String> participantName = new ArrayList<>();
    FliteredParticipantsAdapter fliteredParticipantsAdapter;
    private SessionManager sessionManager;
    private ArrayList<String> CampusArray;
    private MeetingOrganizationListData meetingOrganizationListData;
    String str_CampusName = "", str_UserType = "";
    int CampusId = 0;
    MeetingParticipantListData meetingParticipantListData;
    private String MeetingDbId = "", IsHost = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fliter_participants);
        getSupportActionBar().setTitle("Add Participants");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        assignViews();
        sessionManager = new SessionManager(FliterParticipantsActivity.this);
        Intent intent = getIntent();
        MeetingDbId = intent.getStringExtra("meetingDbId");
        IsHost = intent.getStringExtra("IsHost");

        prepareCampusList();
        initCombo();
        meetingParticipantListData = new MeetingParticipantListData();

        participantName.add("Prasad Mane");
        participantName.add("Ram Patil");
        participantName.add("Raju Shirke");
        participantName.add("Amit Rane");
        participantName.add("Mohmmad Befari");

        //fliteredParticipantsAdapter = new FliteredParticipantsAdapter(FliterParticipantsActivity.this, participantName);
        //mSelectParticipantList.setAdapter(fliteredParticipantsAdapter);

    }

    private void initCombo() {

        mTxtComboCampus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //prepareSite();
                //on 20022019 if (!mEbProcessTextViewSsaVal.getText().toString().trim().isEmpty()) {
                //prepareSite();
                if (CampusArray != null) {
                    SearchableSpinnerDialog searchableSpinnerDialog = new SearchableSpinnerDialog(FliterParticipantsActivity.this,
                            CampusArray,
                            "Select Campus",
                            "Close", "#000000");
                    searchableSpinnerDialog.showSearchableSpinnerDialog();

                    searchableSpinnerDialog.bindOnSpinerListener(new OnSpinnerItemClick() {
                        @Override
                        public void onClick(ArrayList<String> item, int position) {

                            mTxtComboType.setText("Select Participants");
                            mTxtComboStudent.setText("Select Student");
                            mTxtComboStudent.setVisibility(View.GONE);

                            str_CampusName = item.get(position);
                            mTxtComboCampus.setText(str_CampusName);
                            CampusId = Integer.valueOf(meetingOrganizationListData.getMeetingOrganizationList().get(position).getId());
                            //mEbProcessTextViewSiteIDVal.setText(site.getSiteList().get(position).getSiteId());
                        }
                    });
                } else {
                    showToast("Sites are not found");
                }

                /* on 20022019 } else {
                    showToast("Please Select SSA");
                }*/

            }
        });


        mTxtComboType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mTxtComboCampus.getText().toString().trim().equals("Select Campus")) {
                    SearchableSpinnerDialog searchableSpinnerDialog = new SearchableSpinnerDialog(FliterParticipantsActivity.this,
                            new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.types_of_participant))),
                            "Types of participants",
                            "Close", "#000000");
                    searchableSpinnerDialog.showSearchableSpinnerDialog();

                    searchableSpinnerDialog.bindOnSpinerListener(new OnSpinnerItemClick() {
                        @Override
                        public void onClick(ArrayList<String> item, int position) {
                            str_typeOfParticipant = item.get(position);
                            mTxtComboType.setText(str_typeOfParticipant);

                            /*<item>Teachers</item>
        <item>Students</item>
        <item>Parents</item>
        <item>System User</item>
        <item>Group</item>
        <item>Other</item>*/
                            boolean processdFlag = false;
                            if (str_typeOfParticipant.equals("Teachers")) {
                                str_UserType = "T";
                                processdFlag = true;
                            } else if (str_typeOfParticipant.equals("Students")) {
                                str_UserType = "S";
                                processdFlag = true;
                            } else if (str_typeOfParticipant.equals("System")) {
                                str_UserType = "A";
                                processdFlag = true;
                            } else if (str_typeOfParticipant.equals("Group")) {
                                str_UserType = "G";
                                processdFlag = true;
                            } else if (str_typeOfParticipant.equals("Other")) {
                                str_UserType = "O";
                                processdFlag = true;
                            }
                            if (processdFlag == true) {
                                prepareListData(MeetingDbId, String.valueOf(CampusId), str_UserType);
                                mInputSearch.setVisibility(View.VISIBLE);
                            }
                            if (str_typeOfParticipant.equals("Parents")) {
                                mTxtComboStudent.setVisibility(View.VISIBLE);
                            } else {
                                mTxtComboStudent.setVisibility(View.GONE);
                            }

                        }
                    });
                } else {
                    showToast("Select Campus");
                }
            }
        });

        mTxtComboStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (CampusArray != null) {
                    SearchableSpinnerDialog searchableSpinnerDialog = new SearchableSpinnerDialog(FliterParticipantsActivity.this,
                            CampusArray,
                            "Select Student",
                            "Close", "#000000");
                    searchableSpinnerDialog.showSearchableSpinnerDialog();

                    searchableSpinnerDialog.bindOnSpinerListener(new OnSpinnerItemClick() {
                        @Override
                        public void onClick(ArrayList<String> item, int position) {

                            str_CampusName = item.get(position);
                            mTxtComboStudent.setText(str_CampusName);
                            CampusId = Integer.valueOf(meetingOrganizationListData.getMeetingOrganizationList().get(position).getId());
                        }
                    });
                } else {
                    showToast("Students are not found");
                }*/

            }
        });

    }

    private void assignViews() {
        mLinearSearch = (LinearLayout) findViewById(R.id.linearSearch);
        mTxtComboCampus = (TextView) findViewById(R.id.txtComboCampus);
        mTxtComboType = (TextView) findViewById(R.id.txtComboType);
        mTxtComboStudent = (TextView) findViewById(R.id.txtComboStudent);
        mInputLayoutSearch = (TextInputLayout) findViewById(R.id.input_layout_search);
        mInputSearch = (EditText) findViewById(R.id.input_search);
        mSelectParticipantList = (ListView) findViewById(R.id.selectParticipantList);
        mBtns = (FrameLayout) findViewById(R.id.btns);
        mBtnSelect = (Button) findViewById(R.id.btnSelect);
    }

    private void prepareCampusList() {
        try {
            showBusyProgress();
            JSONObject jo = new JSONObject();
            jo.put("APIKEY", sessionManager.getPrefsOrganizationApiKey());
            jo.put("SECRETKEY", sessionManager.getPrefsOrganizationSecretKey());

            GsonRequest<MeetingOrganizationListData> getCampusListRequest = new GsonRequest<>(Request.Method.POST, Constants.getMeetingOrganizationList, jo.toString(), MeetingOrganizationListData.class,
                    new Response.Listener<MeetingOrganizationListData>() {
                        @Override
                        public void onResponse(MeetingOrganizationListData response) {
                            hideBusyProgress();
                            if (response.getError() != null) {
                                showToast(response.getError().getErrorMessage());
                            } else {
                                if (response.getSuccess() == 1) {
                                    meetingOrganizationListData = response;

                                    if (meetingOrganizationListData.getMeetingOrganizationList().size() > 0) {
                                        CampusArray = new ArrayList<String>();
                                        for (MeetingOrganizationList meetingOrganizationList : meetingOrganizationListData.getMeetingOrganizationList()) {
                                            CampusArray.add(meetingOrganizationList.getOrganizationName());
                                        }
                                    } else {
                                        mTxtComboCampus.setText("No Campus Found");
                                    }
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            hideBusyProgress();
                            Log.e("D100", error.toString());
                        }
                    });
            getCampusListRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            getCampusListRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(getCampusListRequest, "getCampusListRequest");


        } catch (JSONException e) {
            hideBusyProgress();
            showToast("Something went wrong. Please try again later.");
        }


    }

    private void prepareStudentListForParents() {
        try {
            showBusyProgress();
            JSONObject jo = new JSONObject();
            jo.put("APIKEY", sessionManager.getPrefsOrganizationApiKey());
            jo.put("SECRETKEY", sessionManager.getPrefsOrganizationSecretKey());

            GsonRequest<MeetingOrganizationListData> getCampusListRequest = new GsonRequest<>(Request.Method.POST, Constants.getMeetingParticipantList, jo.toString(), MeetingOrganizationListData.class,
                    new Response.Listener<MeetingOrganizationListData>() {
                        @Override
                        public void onResponse(MeetingOrganizationListData response) {
                            hideBusyProgress();
                            if (response.getError() != null) {
                                showToast(response.getError().getErrorMessage());
                            } else {
                                if (response.getSuccess() == 1) {
                                    meetingOrganizationListData = response;

                                    if (meetingOrganizationListData.getMeetingOrganizationList().size() > 0) {
                                        CampusArray = new ArrayList<String>();
                                        for (MeetingOrganizationList meetingOrganizationList : meetingOrganizationListData.getMeetingOrganizationList()) {
                                            CampusArray.add(meetingOrganizationList.getOrganizationName());
                                        }
                                    } else {
                                        mTxtComboCampus.setText("No Campus Found");
                                    }
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            hideBusyProgress();
                            Log.e("D100", error.toString());
                        }
                    });
            getCampusListRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            getCampusListRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(getCampusListRequest, "getCampusListRequest");


        } catch (JSONException e) {
            hideBusyProgress();
            showToast("Something went wrong. Please try again later.");
        }


    }

    private void prepareListData(String MeetingDbId, String OrgId, String Type) {
        try {
            showBusyProgress();
            JSONObject jo = new JSONObject();

            /*"APIKEY":"QVBAMTIjMllIRC1TREFTNUQtNUFTRksyMjEy",
                    "SECRETKEY":"MjQ1QDEyIzJZSEQtODVEQTJTM0RFQTg1Mz1JRTVCNEE1Mg==",
                    "MeetingId":"1",
                    "OrgId":"2",
                    "Type":"T"*/


            jo.put("APIKEY", sessionManager.getPrefsOrganizationApiKey());
            jo.put("SECRETKEY", sessionManager.getPrefsOrganizationSecretKey());

            jo.put("MeetingId", MeetingDbId);
            jo.put("OrgId", OrgId);
            jo.put("Type", Type);


            Log.i(SchoolListActivity.class.getName(), Constants.getMeetingParticipantList + "\n\n" + jo.toString());

            GsonRequest<MeetingParticipantListData> getAssignAvailabilityLearnersListRequest = new GsonRequest<>(Request.Method.POST, Constants.getMeetingParticipantList, jo.toString(), MeetingParticipantListData.class,
                    new Response.Listener<MeetingParticipantListData>() {
                        @Override
                        public void onResponse(@NonNull MeetingParticipantListData response) {
                            hideBusyProgress();
                            if (response.getError() != null) {
                                showToast(response.getError().getErrorMessage());
                            } else {
                                if (response.getSuccess() == 1) {
                                    meetingParticipantListData = response;

                                    if (meetingParticipantListData.getMeetingParticipantList() != null && meetingParticipantListData.getMeetingParticipantList().size() > 0) {
                                        //txtDataFound.setVisibility(View.GONE);
                                        mSelectParticipantList.setVisibility(View.VISIBLE);
                                        fliteredParticipantsAdapter = new FliteredParticipantsAdapter(FliterParticipantsActivity.this, meetingParticipantListData.getMeetingParticipantList());
                                        mSelectParticipantList.setAdapter(fliteredParticipantsAdapter);
                                        /*pmAcExpListAdapter = new SchoolExpListAdapter(SchoolListActivity.this, meetingListData);
                                        schoolList_listView_schoolList.setAdapter(pmAcExpListAdapter);
                                        for (int i = 0; i < meetingListData.getUserMeetingsDates().size(); i++) {
                                            schoolList_listView_schoolList.expandGroup(i);
                                        }*/
                                    } else {
                                        mSelectParticipantList.setVisibility(View.GONE);
                                        //txtDataFound.setVisibility(View.VISIBLE);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            /*case R.id.menuDone:
                submitDetails();
                finish();
                startActivity(new Intent(this, ServoStabilizer.class));
                return true;*/
        }
        return super.onOptionsItemSelected(item);
    }
}
