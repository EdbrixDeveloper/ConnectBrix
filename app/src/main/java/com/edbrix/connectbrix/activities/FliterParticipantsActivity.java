package com.edbrix.connectbrix.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.doodle.android.chips.ChipsView;
import com.doodle.android.chips.model.Contact;
import com.edbrix.connectbrix.Application;
import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.adapters.FliteredParticipantsAdapter;
import com.edbrix.connectbrix.baseclass.BaseActivity;
import com.edbrix.connectbrix.data.AddParticipantList;
import com.edbrix.connectbrix.data.AddParticipantsListData;
import com.edbrix.connectbrix.data.MeetingOrganizationList;
import com.edbrix.connectbrix.data.MeetingOrganizationListData;
import com.edbrix.connectbrix.data.MeetingParticipantList;
import com.edbrix.connectbrix.data.MeetingParticipantListData;
import com.edbrix.connectbrix.data.MeetingStudentList;
import com.edbrix.connectbrix.data.MeetingStudentListData;
import com.edbrix.connectbrix.helper.OnSpinnerItemClick;
import com.edbrix.connectbrix.helper.SearchableSpinnerDialog;
import com.edbrix.connectbrix.utils.Constants;
import com.edbrix.connectbrix.utils.SessionManager;
import com.edbrix.connectbrix.volley.GsonRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

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
    LinearLayout ll_contacts;

    String str_typeOfParticipant;
    FliteredParticipantsAdapter fliteredParticipantsAdapter;
    private SessionManager sessionManager;
    private ArrayList<String> CampusArray;
    private ArrayList<String> StudentArray;
    private MeetingOrganizationListData meetingOrganizationListData;
    private MeetingStudentListData meetingStudentListData;
    String str_CampusName = "", str_UserType = "", str_StudentName = "";
    int CampusId = 0, StudentId = 0;
    MeetingParticipantListData meetingParticipantListData;
    private String MeetingDbId = "", IsHost = "";

    private ChipsView mChipsView;
    private FliteredParticipantsAdapter.OnButtonActionListener onButtonActionListener;

    AddParticipantsListData addParticipantsListData;
    AddParticipantList addParticipantList;

    ArrayList<AddParticipantList> arrayAddParticipantList;

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
        addParticipantsListData = new AddParticipantsListData();
        addParticipantList = new AddParticipantList();
        arrayAddParticipantList = new ArrayList<AddParticipantList>();
        meetingParticipantListData = new MeetingParticipantListData();

        onButtonActionListener = new FliteredParticipantsAdapter.OnButtonActionListener() {
            @Override
            public void onCheckBoxPressed(MeetingParticipantList datum, int position, boolean isChecked) {
                addParticipantList = new AddParticipantList();
                if (isChecked) {
                    //meetingParticipantListData.getMeetingParticipantList().get(position).setChecked(true);
                    addParticipantList.setType(str_UserType);
                    if (str_UserType.equals("G")) {
                        addParticipantList.setGroupId(datum.getId());
                    } else {
                        addParticipantList.setUserId(datum.getId());
                    }
                    int i = 0;
                    if (arrayAddParticipantList == null) {
                        i = 0;
                    } else {
                        if (arrayAddParticipantList.size() < 1) {
                            i = 0;
                        } else {
                            i = arrayAddParticipantList.size();
                        }
                    }

                    boolean flag = true;
                    for (int j = 0; j < arrayAddParticipantList.size(); j++) {
                        if (str_UserType.equals("G")) {
                            if (addParticipantList.getGroupId().equals(arrayAddParticipantList.get(j).getGroupId())) {
                                flag = false;
                            }
                        } else if (!str_UserType.equals("O")) {
                            if (addParticipantList.getUserId().equals(arrayAddParticipantList.get(j).getUserId())) {
                                flag = false;
                            }
                        }
                    }
                    if (flag == true) {
                        arrayAddParticipantList.add(i, addParticipantList);
                    }
                } else {
                    addParticipantList.setType(str_UserType);
                    if (str_UserType.equals("G")) {
                        addParticipantList.setGroupId(datum.getId());
                    } else {
                        addParticipantList.setUserId(datum.getId());
                    }

                    for (int i = 0; i < arrayAddParticipantList.size(); i++) {
                        if (str_UserType.equals("G")) {
                            if (addParticipantList.getGroupId().equals(arrayAddParticipantList.get(i).getGroupId())) {
                                arrayAddParticipantList.remove(i);
                            }
                        } else if (!str_UserType.equals("O")) {
                            if (addParticipantList.getUserId().equals(arrayAddParticipantList.get(i).getUserId())) {
                                arrayAddParticipantList.remove(i);
                            }
                        }
                    }
                }
            }

        };


        //////////////////////////////

        mInputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //System.out.println("Text [" + s + "]");

                fliteredParticipantsAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        /////////////////////////////


    }

    private void initCombo() {

        mTxtComboCampus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectParticipantList.setVisibility(View.GONE);
                mInputSearch.setVisibility(View.GONE);
                ll_contacts.setVisibility(View.GONE);
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
                    showToast("Campuses are not found");
                }
            }
        });


        mTxtComboType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectParticipantList.setVisibility(View.GONE);
                mInputSearch.setVisibility(View.GONE);
                ll_contacts.setVisibility(View.GONE);
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
                            mTxtComboStudent.setText("Select Student");
                            /*Teachers,Students,Parents,System User,Group,Other*/
                            boolean processdFlag = false;
                            if (str_typeOfParticipant.equals("Teachers")) {
                                str_UserType = "T";
                                processdFlag = true;
                            } else if (str_typeOfParticipant.equals("Students")) {
                                str_UserType = "S";
                                processdFlag = true;
                            } else if (str_typeOfParticipant.equals("System User")) {
                                str_UserType = "A";
                                processdFlag = true;
                            } else if (str_typeOfParticipant.equals("Group")) {
                                str_UserType = "G";
                                processdFlag = true;
                            } else if (str_typeOfParticipant.equals("Other")) {
                                str_UserType = "O";
                                processdFlag = false;
                                ll_contacts.setVisibility(View.VISIBLE);
                                mChipsView.setFocusable(true);
                            }
                            if (processdFlag == true) {
                                prepareListData(MeetingDbId, String.valueOf(CampusId), str_UserType);
                                mInputSearch.setVisibility(View.VISIBLE);
                                if (!mInputSearch.getText().toString().isEmpty()) {
                                    mInputSearch.setText("");
                                }
                            }

                            if (str_typeOfParticipant.equals("Parents")) {
                                str_UserType = "P";
                                prepareStudentListForParents();
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
                mSelectParticipantList.setVisibility(View.GONE);
                if (StudentArray != null) {
                    SearchableSpinnerDialog searchableSpinnerDialog = new SearchableSpinnerDialog(FliterParticipantsActivity.this,
                            StudentArray,
                            "Select Student",
                            "Close", "#000000");
                    searchableSpinnerDialog.showSearchableSpinnerDialog();

                    searchableSpinnerDialog.bindOnSpinerListener(new OnSpinnerItemClick() {
                        @Override
                        public void onClick(ArrayList<String> item, int position) {

                            str_StudentName = item.get(position);
                            mTxtComboStudent.setText(str_StudentName);
                            StudentId = Integer.valueOf(meetingStudentListData.getMeetingStudentList().get(position).getId());
                            prepareListDataForStudent(MeetingDbId, String.valueOf(StudentId), str_UserType);
                            mInputSearch.setVisibility(View.VISIBLE);
                            if (!mInputSearch.getText().toString().isEmpty()) {
                                mInputSearch.setText("");
                            }
                        }
                    });
                } else {
                    showToast("Students are not found");
                }

            }
        });

        mBtnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayAddParticipantList != null) {

                    if (arrayAddParticipantList.size() > 0) {
                        addParticipantsListData.setAPIKEY(sessionManager.getPrefsOrganizationApiKey());
                        addParticipantsListData.setSECRETKEY(sessionManager.getPrefsOrganizationSecretKey());
                        addParticipantsListData.setMeetingId(MeetingDbId);
                        addParticipantsListData.setParticipantList(arrayAddParticipantList);

                        Gson gson2 = new GsonBuilder().create();
                        String jsonString = gson2.toJson(addParticipantsListData);

                        addParticipantsList(jsonString);

                        //Log.e("AddParticipantsListData", jsonString.toString());
                    } else {
                        showToast("Please select or add minimum one participants in list");
                    }
                } else {
                    showToast("Please select or add minimum one Participants in list");
                }
            }
        });

        /////////////////

        mChipsView.setChipsValidator(new ChipsView.ChipValidator() {
            @Override
            public boolean isValid(Contact contact) {
                if (contact.getDisplayName().equals("asd@qwe.de")) {
                    return false;
                }
                return true;
            }
        });

        mChipsView.setChipsListener(new ChipsView.ChipsListener() {
            @Override
            public void onChipAdded(ChipsView.Chip chip) {

                for (int p = 0; p < mChipsView.getChips().size(); p++) {
                    /*Log.d("ChipList", "chip: " + mChipsView.getChips().get(j).getContact().getEmailAddress().toString());
                }
                for (ChipsView.Chip chipItem : mChipsView.getChips()) {*/
                    //Log.d("ChipList", "chip: " + chipItem.toString());

                    /////008
                    addParticipantList = new AddParticipantList();

                    addParticipantList.setType(str_UserType);
                    addParticipantList.setEmail(mChipsView.getChips().get(p).getContact().getEmailAddress().toString());
                    int i = 0;
                    if (arrayAddParticipantList == null) {
                        i = 0;
                    } else {
                        if (arrayAddParticipantList.size() < 1) {
                            i = 0;
                        } else {
                            i = arrayAddParticipantList.size();
                        }
                    }

                    boolean flag = true;
                    for (int j = 0; j < arrayAddParticipantList.size(); j++) {
                        if (str_UserType.equals("O")) {
                            if (addParticipantList.getEmail().equals(arrayAddParticipantList.get(j).getEmail())) {
                                flag = false;
                            }
                        }
                    }
                    if (flag == true) {
                        arrayAddParticipantList.add(i, addParticipantList);
                    }


                    /////008
                }
            }

            @Override
            public void onChipDeleted(ChipsView.Chip chip) {
                ///////008

                addParticipantList.setType(str_UserType);
                addParticipantList.setEmail(chip.getContact().getEmailAddress());

                for (int i = 0; i < arrayAddParticipantList.size(); i++) {
                    if (str_UserType.equals("O")) {
                        if (addParticipantList.getGroupId().equals(arrayAddParticipantList.get(i).getGroupId())) {
                            arrayAddParticipantList.remove(i);
                        }
                    }
                }


                ///////008

            }

            @Override
            public void onTextChanged(CharSequence text) {
                //mAdapter.filterItems(text);
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
        mChipsView = (ChipsView) findViewById(R.id.cv_contacts);
        ll_contacts = (LinearLayout) findViewById(R.id.ll_contacts);
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

            jo.put("MeetingId", MeetingDbId);
            jo.put("OrgId", String.valueOf(CampusId));
            jo.put("Type", "S");

            GsonRequest<MeetingStudentListData> getStudentListRequest = new GsonRequest<>(Request.Method.POST, Constants.getMeetingStudentList, jo.toString(), MeetingStudentListData.class,
                    new Response.Listener<MeetingStudentListData>() {
                        @Override
                        public void onResponse(MeetingStudentListData response) {
                            hideBusyProgress();
                            if (response.getError() != null) {
                                showToast(response.getError().getErrorMessage());
                            } else {
                                if (response.getSuccess() == 1) {
                                    meetingStudentListData = response;

                                    if (meetingOrganizationListData.getMeetingOrganizationList().size() > 0) {
                                        StudentArray = new ArrayList<String>();
                                        for (MeetingStudentList meetingStudentList : meetingStudentListData.getMeetingStudentList()) {
                                            StudentArray.add(meetingStudentList.getName());
                                        }
                                    } else {
                                        mTxtComboCampus.setText("No Student Found");
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
            getStudentListRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            getStudentListRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(getStudentListRequest, "getStudentListRequest");


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
                                        fliteredParticipantsAdapter = new FliteredParticipantsAdapter(FliterParticipantsActivity.this, meetingParticipantListData.getMeetingParticipantList(), onButtonActionListener);
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

    private void prepareListDataForStudent(String MeetingDbId, String StudentId, String Type) {
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
            jo.put("StudentId", StudentId);
            jo.put("Type", "P");


            Log.i(SchoolListActivity.class.getName(), Constants.getMeetingStudentParentList + "\n\n" + jo.toString());

            GsonRequest<MeetingParticipantListData> getAssignAvailabilityLearnersListRequest = new GsonRequest<>(Request.Method.POST, Constants.getMeetingStudentParentList, jo.toString(), MeetingParticipantListData.class,
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
                                        fliteredParticipantsAdapter = new FliteredParticipantsAdapter(FliterParticipantsActivity.this, meetingParticipantListData.getMeetingParticipantList(), onButtonActionListener);
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

    private void addParticipantsList(String JsonMeetingParticipant) {
        try {
            showBusyProgress();
            //JSONObject jo = new JSONObject();

            //Gson gson2 = new GsonBuilder().create();
            //String jsonString = gson2.toJson(meetingParticipantListData);

            Log.i(SchoolListActivity.class.getName(), Constants.saveMeetingParticipants + "\n\n" + JsonMeetingParticipant);

            GsonRequest<MeetingParticipantListData> saveMeetingParticipantsRequest = new GsonRequest<>(Request.Method.POST, Constants.saveMeetingParticipants, JsonMeetingParticipant, MeetingParticipantListData.class,
                    new Response.Listener<MeetingParticipantListData>() {
                        @Override
                        public void onResponse(@NonNull MeetingParticipantListData response) {
                            hideBusyProgress();
                            if (response.getError() != null) {
                                showToast(response.getError().getErrorMessage());
                            } else {
                                if (response.getSuccess() == 1) {
                                    showToast("Participant Successfully added");
                                    //showToast(response.getMessage().toString());
                                    /*Intent intent = new Intent(FliterParticipantsActivity.this,MeetingDetailsActivity.class);
                                    intent.putExtra("meetingDbId",MeetingDbId);
                                    intent.putExtra("IsHost",IsHost);
                                    startActivity(intent);
                                    finish();*/

                                    Intent resultIntent = new Intent();
                                    // TODO Add extras or a data URI to this intent as appropriate.
                                    resultIntent.putExtra("meetingDbId",MeetingDbId);
                                    resultIntent.putExtra("IsHost",IsHost);
                                    setResult(Activity.RESULT_OK, resultIntent);
                                    finish();

                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideBusyProgress();

                }
            });
            saveMeetingParticipantsRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            saveMeetingParticipantsRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(saveMeetingParticipantsRequest, "SaveMeetingParticipants");

        } catch (Exception ex) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mInputSearch.getWindowToken(), 0);
    }



}
