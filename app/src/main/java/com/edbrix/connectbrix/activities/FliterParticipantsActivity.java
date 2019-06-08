package com.edbrix.connectbrix.activities;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.adapters.FliteredParticipantsAdapter;
import com.edbrix.connectbrix.helper.OnSpinnerItemClick;
import com.edbrix.connectbrix.helper.SearchableSpinnerDialog;

import java.util.ArrayList;
import java.util.Arrays;

public class FliterParticipantsActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fliter_participants);
        getSupportActionBar().setTitle("Add Participants");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        assignViews();
        initCombo();

        participantName.add("Prasad Mane");
        participantName.add("Ram Patil");
        participantName.add("Raju Shirke");
        participantName.add("Amit Rane");
        participantName.add("Mohmmad Befari");

        fliteredParticipantsAdapter = new FliteredParticipantsAdapter(FliterParticipantsActivity.this,participantName);
        mSelectParticipantList.setAdapter(fliteredParticipantsAdapter);

    }

    private void initCombo() {
        mTxtComboType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        if(str_typeOfParticipant.equals("Parents")){
                            mTxtComboStudent.setVisibility(View.VISIBLE);
                        }else {
                            mTxtComboStudent.setVisibility(View.GONE);
                        }
                    }
                });
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
