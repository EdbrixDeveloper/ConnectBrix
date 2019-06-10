package com.edbrix.connectbrix.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.adapters.SchoolListAdapter;
import com.edbrix.connectbrix.baseclass.BaseActivity;

import java.util.ArrayList;

public class SchoolListActivity extends BaseActivity {

    private ListView mSchoolMeetingList;
    SchoolListAdapter schoolListAdapter;
    ArrayList<String> date = new ArrayList<>();
    FloatingActionButton floating_action_button_fab_with_listview;
    boolean doubleBackToExitPressedOnce = false;

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

}
