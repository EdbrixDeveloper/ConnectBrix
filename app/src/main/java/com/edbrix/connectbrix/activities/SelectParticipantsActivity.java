package com.edbrix.connectbrix.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.adapters.ParticipantsListAdapter;

import java.util.ArrayList;

public class SelectParticipantsActivity extends AppCompatActivity {

    private ListView mSelectedParticipantsList;
    ArrayList<String> participantName = new ArrayList<>();
    ParticipantsListAdapter participantsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_participants);
        getSupportActionBar().setTitle("Participants List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        assignViews();

        participantName.add("Prasad Mane");
        participantName.add("Ram Patil");
        participantName.add("Raju Shirke");
        participantName.add("Amit Rane");
        participantName.add("Mohmmad Befari");

        //participantsListAdapter = new ParticipantsListAdapter(SelectParticipantsActivity.this, participantName, "", "", "");
        //mSelectedParticipantsList.setAdapter(participantsListAdapter);
    }

    private void assignViews() {
        mSelectedParticipantsList = (ListView) findViewById(R.id.selectedParticipantsList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menuAdd:
                startActivity(new Intent(this, FliterParticipantsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_add_participants, menu);
        return true;

    }

}
