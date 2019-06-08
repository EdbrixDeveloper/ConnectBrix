package com.edbrix.connectbrix.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.edbrix.connectbrix.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private CircleImageView mImgProfile;
    private TextView mEmail;
    private EditText mEmailVal;
    private TextView mFirstName;
    private EditText mFirstNameVal;
    private TextView mLastName;
    private EditText mLastNameVal;
    private TextView mAddress;
    private EditText mAddressVal;
    private TextView mCity;
    private EditText mCityVal;
    private TextView mState;
    private TextView mStateVal;
    private TextView mZip;
    private EditText mZipVal;
    private TextView mPhone1;
    private EditText mPhone1Val;
    private TextView mPhone2;
    private EditText mPhone2Val;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().setTitle("Update Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        assignViews();
    }

    private void assignViews() {
        mImgProfile = (CircleImageView) findViewById(R.id.imgProfile);
        mEmail = (TextView) findViewById(R.id.email);
        mEmailVal = (EditText) findViewById(R.id.emailVal);
        mFirstName = (TextView) findViewById(R.id.firstName);
        mFirstNameVal = (EditText) findViewById(R.id.firstNameVal);
        mLastName = (TextView) findViewById(R.id.lastName);
        mLastNameVal = (EditText) findViewById(R.id.lastNameVal);
        mAddress = (TextView) findViewById(R.id.address);
        mAddressVal = (EditText) findViewById(R.id.addressVal);
        mCity = (TextView) findViewById(R.id.city);
        mCityVal = (EditText) findViewById(R.id.cityVal);
        mState = (TextView) findViewById(R.id.state);
        mStateVal = (TextView) findViewById(R.id.stateVal);
        mZip = (TextView) findViewById(R.id.zip);
        mZipVal = (EditText) findViewById(R.id.zipVal);
        mPhone1 = (TextView) findViewById(R.id.phone1);
        mPhone1Val = (EditText) findViewById(R.id.phone1Val);
        mPhone2 = (TextView) findViewById(R.id.phone2);
        mPhone2Val = (EditText) findViewById(R.id.phone2Val);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.update_profile_menu, menu);
        return true;

    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
