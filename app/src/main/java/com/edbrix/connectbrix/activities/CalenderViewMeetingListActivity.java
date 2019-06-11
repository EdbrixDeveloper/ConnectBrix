package com.edbrix.connectbrix.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.adapters.SchoolListAdapter;
import com.edbrix.connectbrix.data.MyEventDay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalenderViewMeetingListActivity extends AppCompatActivity {

    private LinearLayout mLinearLayoutCal;
    private CalendarView mCalendarView;

    private ListView mMeetingListWithCalender;
    private TextView mTxtSelectedDate;
    private FloatingActionButton mFloatingActionButtonFabWithListview;
    FloatingActionButton floating_action_button_fab_with_listview;
    SchoolListAdapter schoolListAdapter;
    ArrayList<String> date = new ArrayList<>();
    private List<EventDay> mEventDays = new ArrayList<>();
    String str_temp_date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender_view_meeting_list);
        getSupportActionBar().setTitle("Meeting List");
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

        mMeetingListWithCalender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CalenderViewMeetingListActivity.this, MeetingDetailsActivity.class);
                intent.putExtra("meetingTitle", "School Anual Function Meeting");
                startActivity(intent);
            }
        });

        /*floating_action_button_fab_with_listview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CalenderViewMeetingListActivity.this, CreateMeetingActivity.class));
            }
        });*/
        final SimpleDateFormat finalSimpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy");
        Date date = new Date();
        mTxtSelectedDate.setText(finalSimpleDateFormat.format(date));

        /*mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                str_temp_date = "" + dayOfMonth + "/" + (month + 1) + "/" + year;

                try {
                    Date date = simpleDateFormat.parse(str_temp_date);
                    mTxtSelectedDate.setText(finalSimpleDateFormat.format(date));
                    //Toast.makeText(getApplicationContext(),""+str_date,Toast.LENGTH_LONG).show();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });*/

        mCalendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                try {
                    MyEventDay myEventDay = (MyEventDay) eventDay;
                    Calendar cal = myEventDay.getCalendar();
                    Date date = cal.getTime();
                    mTxtSelectedDate.setText(finalSimpleDateFormat.format(date));
                } catch (Exception e) {
                    e.printStackTrace();
                    Calendar cal1 = mCalendarView.getSelectedDate();
                    Date date = cal1.getTime();
                    mTxtSelectedDate.setText(finalSimpleDateFormat.format(date));

                }
            }
        });

        addEventToCalendar();

    }

    private void addEventToCalendar() {

        try {
            for(int i=1;i<=9;i++){
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date dateForEvent = sdf. parse(i+"-06-2019");
                Calendar cal = Calendar. getInstance();
                cal.setTime(dateForEvent);
                MyEventDay myEventDay1 = new MyEventDay(cal, R.drawable.circle, "Note "+i);
                mCalendarView.setDate(myEventDay1.getCalendar());
                mEventDays.add(myEventDay1);
                mCalendarView.setEvents(mEventDays);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void assignViews() {
        mLinearLayoutCal = (LinearLayout) findViewById(R.id.linearLayoutCal);
        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        mMeetingListWithCalender = (ListView) findViewById(R.id.meetingListWithCalender);
        mFloatingActionButtonFabWithListview = (FloatingActionButton) findViewById(R.id.float_btn);
        mTxtSelectedDate = (TextView) findViewById(R.id.txtSelectedDate);
    }

    private void setSchoolListAdapter(ArrayList<String> date) {
        schoolListAdapter = new SchoolListAdapter(CalenderViewMeetingListActivity.this, date);
        mMeetingListWithCalender.setAdapter(schoolListAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
           /* case android.R.id.:
                startActivity(new Intent(this, UserProfileActivity.class));
                return true;*/
            case R.id.menuCalender:
                startActivity(new Intent(this, SchoolListActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.calender_view_menu, menu);
        return true;

    }
}
