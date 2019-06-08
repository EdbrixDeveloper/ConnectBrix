package com.edbrix.connectbrix.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.edbrix.connectbrix.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateMeetingActivity extends AppCompatActivity {

    private TextView mCMeetingTitle;
    private EditText mCMeetingTitleVal;
    private TextView mCMeetingDate;
    private EditText mCMeetingDateVal;
    private TextView mCMeetingAgenda;
    private EditText mCMeetingAgendaVal;
    private Button mBtnNext;
    String str_date;
    String str_temp_date;
    String str_time;
    String meetingDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meeting);
        getSupportActionBar().setTitle("Create Meeting");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        assignViews();

        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateMeetingActivity.this, SelectParticipantsActivity.class));
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

                calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        str_temp_date = "" + dayOfMonth + "/" + (month + 1) + "/" + year;

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

                        str_time += " " + hour + ":" + minuteTemp + " " + am_pm;
                    }
                });

                btnSet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        meetingDate = new String();
                        meetingDate = str_date +" "+str_time;
                        if (meetingDate.equals("nullnull")) {
                            Date date = new Date();
                            str_date = finalSimpleDateFormat.format(date);
                            String strDateFormat = "hh:mm a";
                            DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
                            str_time = dateFormat.format(date);
                            meetingDate = str_date +" "+str_time;

                        }
                        //Toast.makeText(getApplicationContext(),""+meetingDate,Toast.LENGTH_LONG).show();
                        mCMeetingDateVal.setText(meetingDate);
                        dialogBuilder.dismiss();
                    }
                });

                dialogBuilder.setView(dialogView);
                dialogBuilder.setCancelable(false);
                dialogBuilder.show();

            }
        });
    }

    private void assignViews() {
        mCMeetingTitle = (TextView) findViewById(R.id.cMeetingTitle);
        mCMeetingTitleVal = (EditText) findViewById(R.id.cMeetingTitleVal);
        mCMeetingDate = (TextView) findViewById(R.id.cMeetingDate);
        mCMeetingDateVal = (EditText) findViewById(R.id.cMeetingDateVal);
        mCMeetingAgenda = (TextView) findViewById(R.id.cMeetingAgenda);
        mCMeetingAgendaVal = (EditText) findViewById(R.id.cMeetingAgendaVal);
        mBtnNext = (Button) findViewById(R.id.btnNext);
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
