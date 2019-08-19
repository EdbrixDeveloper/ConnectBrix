package com.edbrix.connectbrix.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.connectbrix.Application;
import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.baseclass.BaseActivity;
import com.edbrix.connectbrix.data.CreateMeetingResponseData;
import com.edbrix.connectbrix.data.UpdateMeetingResponseData;
import com.edbrix.connectbrix.utils.Constants;
import com.edbrix.connectbrix.utils.SessionManager;
import com.edbrix.connectbrix.volley.GsonRequest;
import com.edbrix.connectbrix.volley.SettingsMy;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

public class CreateMeetingActivity extends BaseActivity {

    private static final String TAG = CreateMeetingActivity.class.getName();
    private TextView mCMeetingTitle;
    private EditText mCMeetingTitleVal;
    private TextView mCMeetingDate;
    private EditText mCMeetingDateVal;
    private TextView mCMeetingAgenda;
    private EditText mCMeetingAgendaVal;
    private Button mBtnCreateMeeting;
    String str_date;
    String str_temp_date;
    String str_time;
    String str_temp_time;
    String meetingDate;
    String meetingDbId;
    String tempMeetingDate;
    String comesFor;
    String isHost;
    String IsCalenderActivity = "", isAvailable = "";
    SessionManager sessionManager;
    boolean isDateSelect = false, isTimeSelect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meeting);
        getSupportActionBar().setTitle("Create Meeting");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(this);
        assignViews();

        Intent intent = getIntent();
        comesFor = intent.getStringExtra("comesFor");
        isHost = intent.getStringExtra("IsHost");
        isAvailable = intent.getStringExtra("isAvailable");
        IsCalenderActivity = intent.getStringExtra("IsCalenderActivity");
        if (comesFor.equals("edit")) {
            getSupportActionBar().setTitle("Edit Meeting");
            mBtnCreateMeeting.setText("Update Meeting");
            meetingDbId = intent.getStringExtra("meetingId");

            mCMeetingTitleVal.setText(intent.getStringExtra("meetingTitle"));
            mCMeetingDateVal.setText(intent.getStringExtra("meetingDateTime"));
            mCMeetingAgendaVal.setText(intent.getStringExtra("meetingAgenda"));

        }
        mBtnCreateMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(CreateMeetingActivity.this, SelectParticipantsActivity.class));
                if (fieldValidation() == true) {
                    if (comesFor.equals("new")) {
                        createMeeting();
                    } else {
                        updateMeeting();
                    }

                }
            }
        });

        mCMeetingDateVal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final SimpleDateFormat finalSimpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy");
                final AlertDialog dialogBuilder = new AlertDialog.Builder(CreateMeetingActivity.this).create();
                LayoutInflater inflater = CreateMeetingActivity.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.date_time_picker_dialog, null);

                final CalendarView calendarView = (CalendarView) dialogView.findViewById(R.id.calendarDatePicker);
                final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.timePicker);
                final Button btnSet = (Button) dialogView.findViewById(R.id.btnSet);
                final Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);

                if (!mCMeetingDateVal.getText().toString().isEmpty()) {
                    try {
                        StringTokenizer tk = new StringTokenizer(mCMeetingDateVal.getText().toString());
                        String str_date = tk.nextToken();
                        String time = tk.nextToken();
                        String amPm = tk.nextToken();


                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
                        Date date = sdf.parse(str_date);

                        long startDate = date.getTime();
                        calendarView.setDate(startDate);


                        int hour = Integer.valueOf(time.substring(0, time.indexOf(":")));
                        int min = Integer.valueOf(time.substring(time.indexOf(":") + 1, time.length()));
                        //for set am/pm in time picker
                        if (amPm.toLowerCase().equals("pm")) {
                            hour += 12;
                        } else {
                            if (hour > 11) {
                                hour -= 12;
                            }
                        }
                        if (Build.VERSION.SDK_INT >= 23) {
                            timePicker.setHour(hour);
                            timePicker.setMinute(min);
                        } else {
                            timePicker.setCurrentHour(hour);
                            timePicker.setCurrentMinute(min);
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                calendarView.setMinDate(System.currentTimeMillis() - 1000);

                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    calendarView.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                        @Override
                        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            str_temp_date = "" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                            isDateSelect = true;
                            try {
                                Date date = simpleDateFormat.parse(str_temp_date);
                                str_date = "" + finalSimpleDateFormat.format(date);
                                //Toast.makeText(getApplicationContext(),""+str_date,Toast.LENGTH_LONG).show();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } else {


                }*/

                calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        str_temp_date = "" + dayOfMonth + "/" + (month + 1) + "/" + year;
                        isDateSelect = true;
                        try {
                            Date date = simpleDateFormat.parse(str_temp_date);
                            str_date = "" + finalSimpleDateFormat.format(date);
                            //Toast.makeText(getApplicationContext(),""+str_date,Toast.LENGTH_LONG).show();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                });

                timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        String am_pm = "";

                        Calendar datetime = Calendar.getInstance();
                        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        datetime.set(Calendar.MINUTE, minute);

                        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                            am_pm = "AM";
                        else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                            am_pm = "PM";

                        String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ? "12" : datetime.get(Calendar.HOUR) + "";

                        //((Button)getActivity().findViewById(R.id.btnEventStartTime)).setText( strHrsToShow+":"+datetime.get(Calendar.MINUTE)+" "+am_pm );

                        str_time = "";
                        String min = "";
                        if (Integer.valueOf(strHrsToShow) < 10) {
                            strHrsToShow = "0" + strHrsToShow;
                        }

                        if (datetime.get(Calendar.MINUTE) < 10) {
                            min = "0" + datetime.get(Calendar.MINUTE);
                        } else {
                            min = String.valueOf(datetime.get(Calendar.MINUTE));
                        }
                        str_time += strHrsToShow + ":" + min + " " + am_pm;
                        str_temp_time += strHrsToShow + ":" + min + " " + am_pm;
                        isTimeSelect = true;
                        /*isTimeSelect = true;
                        int hour, minuteTemp;
                        String am_pm;
                        str_time = "";
                        if (Build.VERSION.SDK_INT >= 23) {
                            hour = timePicker.getHour();
                            minuteTemp = timePicker.getMinute();
                        } else {
                            hour = timePicker.getCurrentHour();
                            minuteTemp = timePicker.getCurrentMinute();
                        }
                        if (hour > 12) {
                            am_pm = "PM";
                            hour = hour - 12;
                        } else {
                            am_pm = "AM";
                        }

                        if (minuteTemp < 10) {
                            str_time += " " + hour + ":0" + minuteTemp + " " + am_pm;
                            str_temp_time += " " + hour + ":0" + minuteTemp;
                        } else {
                            str_time += " " + hour + ":" + minuteTemp + " " + am_pm;
                            str_temp_time += " " + hour + ":" + minuteTemp;
                        }*/

                    }
                });

                btnSet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        meetingDate = new String();

                        ////////////////
                        if (str_date == null || str_date.isEmpty()) {
                            String todate = finalSimpleDateFormat.format(currentdate());
                            str_date = todate.toString(); //here you get current date
                        }

                        if (str_time == null || str_time.isEmpty()) {
                            str_time = "";
                            int hour, minuteTemp;
                            String am_pm;
                            hour = timePicker.getCurrentHour();
                            minuteTemp = timePicker.getCurrentMinute();
                            if (hour > 12) {
                                am_pm = "PM";
                                hour = hour - 12;
                            } else {
                                am_pm = "AM";
                            }

                            if (minuteTemp < 10) {
                                str_time += " " + hour + ":0" + minuteTemp + " " + am_pm;
                                str_temp_time += " " + hour + ":0" + minuteTemp;
                            } else {
                                str_time += " " + hour + ":" + minuteTemp + " " + am_pm;
                                str_temp_time += " " + hour + ":" + minuteTemp;
                            }
                        }
                        ///////////////

                        meetingDate = str_date + " " + str_time;
                        Date date = new Date();
                        if (meetingDate.equals("null null")) {
                            str_date = finalSimpleDateFormat.format(date);
                            String strDateFormat = "hh:mm a";
                            DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
                            str_time = dateFormat.format(date);
                            meetingDate = str_date + " " + str_time;

                        } else if (isDateSelect != true) {
                            str_date = finalSimpleDateFormat.format(date);
                        } else if (isTimeSelect != true) {
                            String strDateFormat = "hh:mm a";
                            DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
                            str_time = dateFormat.format(date);
                            meetingDate = str_date + " " + str_time;
                        }

                        mCMeetingDateVal.setText(meetingDate);

                        //SimpleDateFormat tempSimpleDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        SimpleDateFormat tempSimpleDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
                        SimpleDateFormat convertDateTime = new SimpleDateFormat("dd/MMM/yyyy hh:mm a");
                        try {
                            Date dateTime = convertDateTime.parse(meetingDate);
                            tempMeetingDate = tempSimpleDateTimeFormat.format(dateTime);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        dialogBuilder.dismiss();
                    }
                });

                dialogBuilder.setView(dialogView);
                dialogBuilder.setCancelable(false);
                dialogBuilder.show();

            }
        });

        if (savedInstanceState != null) {
            mCMeetingTitleVal.setText(savedInstanceState.getString("title"));
            mCMeetingDateVal.setText(savedInstanceState.getString("date"));
            mCMeetingAgendaVal.setText(savedInstanceState.getString("agenda"));
        }
    }


    private Date currentdate() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 0);
        return cal.getTime();
    }

    private void updateMeeting() {

        try {
            showBusyProgress();
            JSONObject jsonObject = new JSONObject();
            /*jsonObject.put("APIKEY", sessionManager.getPrefsOrganizationApiKey());
            jsonObject.put("SECRETKEY", sessionManager.getPrefsOrganizationSecretKey());*/
            jsonObject.put("AccessToken", sessionManager.getPrefsSessionAccessToken());
            jsonObject.put("UserId", sessionManager.getSessionUserId());
            jsonObject.put("MeetingId", meetingDbId);
            jsonObject.put("Title", mCMeetingTitleVal.getText().toString().trim());
            jsonObject.put("Agenda", mCMeetingAgendaVal.getText().toString().trim());

            SimpleDateFormat tempSimpleDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");//"yyyy-MM-dd hh:mm:ss"
            SimpleDateFormat convertDateTime = new SimpleDateFormat("dd/MMM/yyyy hh:mm a");
            Date dateTime = convertDateTime.parse(mCMeetingDateVal.getText().toString());

            jsonObject.put("MeetingDate", tempSimpleDateTimeFormat.format(dateTime));

            GsonRequest<UpdateMeetingResponseData> updateMeetingRequest = new GsonRequest<>(Request.Method.POST, Constants.updateMeeting, jsonObject.toString(), UpdateMeetingResponseData.class,
                    new Response.Listener<UpdateMeetingResponseData>() {
                        @Override
                        public void onResponse(@NonNull UpdateMeetingResponseData response) {
                            hideBusyProgress();
                            if (response.getError() != null) {
                                String error = response.getError().getErrorMessage();
                                showToast(error);
                            } else {
                                if (response.getSuccess() == 1) {
                                    //showToast(response.getMessage());
                                    meetingDbId = response.getMeetingId();
                                    Intent intent = new Intent(CreateMeetingActivity.this, MeetingDetailsActivity.class);
                                    intent.putExtra("meetingDbId", meetingDbId);
                                    intent.putExtra("IsHost", isHost);
                                    intent.putExtra("isAvailable", isAvailable);
                                    intent.putExtra("RefreshFlag", "Y");
                                    intent.putExtra("hostName",sessionManager.getSessionUserFirstName()+" "+sessionManager.getSessionUserFirstLast());
                                    //intent.putExtra("IsCalenderActivity", "C");
                                    intent.putExtra("IsCalenderActivity", IsCalenderActivity.equals("Y") ? "YC" : "C");
                                    startActivity(intent);
                                    finish();
                                }
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideBusyProgress();
                    showToast(SettingsMy.getErrorMessage(error));
                }
            });
            updateMeetingRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            updateMeetingRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(updateMeetingRequest, "updateMeetingRequest");

        } catch (Exception e) {
            hideBusyProgress();
            Log.e(TAG, e.getMessage());
        }
    }

    private void createMeeting() {

        try {

            showBusyProgress();
            JSONObject jsonObject = new JSONObject();
           /* jsonObject.put("APIKEY", sessionManager.getPrefsOrganizationApiKey());
            jsonObject.put("SECRETKEY", sessionManager.getPrefsOrganizationSecretKey());*/
            jsonObject.put("AccessToken", sessionManager.getPrefsSessionAccessToken());
            jsonObject.put("UserId", sessionManager.getSessionUserId());
            jsonObject.put("Title", mCMeetingTitleVal.getText().toString().trim());
            jsonObject.put("Agenda", mCMeetingAgendaVal.getText().toString().trim());
            jsonObject.put("MeetingDate", tempMeetingDate);

            GsonRequest<CreateMeetingResponseData> createMeetingRequest = new GsonRequest<>(Request.Method.POST, Constants.createMeeting, jsonObject.toString(), CreateMeetingResponseData.class,
                    new Response.Listener<CreateMeetingResponseData>() {
                        @Override
                        public void onResponse(@NonNull CreateMeetingResponseData response) {
                            hideBusyProgress();
                            if (response.getError() != null) {
                                String error = response.getError().getErrorMessage();
                                showToast(error);
                            } else {
                                if (response.getSuccess() == 1) {
                                    // showToast(response.getMessage());
                                    meetingDbId = "" + response.getMeetingId();
                                    Intent intent = new Intent(CreateMeetingActivity.this, MeetingDetailsActivity.class);
                                    intent.putExtra("meetingDbId", meetingDbId);
                                    intent.putExtra("IsHost", "1");
                                    intent.putExtra("isAvailable", "0");
                                    intent.putExtra("RefreshFlag", "Y");
                                    intent.putExtra("hostName",sessionManager.getSessionUserFirstName()+" "+sessionManager.getSessionUserFirstLast());
                                    intent.putExtra("IsCalenderActivity", IsCalenderActivity.equals("Y") ? "YC" : "C");
                                    startActivity(intent);
                                    finish();
                                }
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideBusyProgress();
                    showToast(SettingsMy.getErrorMessage(error));
                }
            });
            createMeetingRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            createMeetingRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(createMeetingRequest, "createMeetingRequest");

        } catch (Exception e) {
            hideBusyProgress();
            Log.e(TAG, e.getMessage());

        }

    }

    private void assignViews() {
        mCMeetingTitle = (TextView) findViewById(R.id.cMeetingTitle);
        mCMeetingTitleVal = (EditText) findViewById(R.id.cMeetingTitleVal);
        mCMeetingDate = (TextView) findViewById(R.id.cMeetingDate);
        mCMeetingDateVal = (EditText) findViewById(R.id.cMeetingDateVal);
        mCMeetingAgenda = (TextView) findViewById(R.id.cMeetingAgenda);
        mCMeetingAgendaVal = (EditText) findViewById(R.id.cMeetingAgendaVal);
        mBtnCreateMeeting = (Button) findViewById(R.id.btnCreateMeeting);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean fieldValidation() {
        String meetingTitle = mCMeetingTitleVal.getText().toString().trim();
        String meetingDate = mCMeetingDateVal.getText().toString().trim();
        String meetingAjenda = mCMeetingAgendaVal.getText().toString().trim();

        if (meetingTitle.isEmpty() || meetingTitle == null) {
            showToast("Please fill meeting title.");
            return false;
        } else if (meetingDate.isEmpty() || meetingDate == null) {
            showToast("Please Select Meeting Date.");
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("title", mCMeetingTitleVal.getText().toString());
        outState.putString("date", mCMeetingDateVal.getText().toString());
        outState.putString("agenda", mCMeetingAgendaVal.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCMeetingTitleVal.setText(savedInstanceState.getString("title"));
        mCMeetingDateVal.setText(savedInstanceState.getString("date"));
        mCMeetingAgendaVal.setText(savedInstanceState.getString("agenda"));
    }
}
