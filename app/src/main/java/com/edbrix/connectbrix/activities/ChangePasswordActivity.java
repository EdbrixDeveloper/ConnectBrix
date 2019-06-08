package com.edbrix.connectbrix.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.edbrix.connectbrix.R;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextView mTextViewPassword;
    private EditText mEdTxtPassword;
    private TextView mTextViewConfirmPassword;
    private EditText mEdTxtConfirmPassword;
    private Button mBtnChangePassSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        assignViews();

    }

    private void assignViews() {
        mTextViewPassword = (TextView) findViewById(R.id.textViewPassword);
        mEdTxtPassword = (EditText) findViewById(R.id.edTxtPassword);
        mTextViewConfirmPassword = (TextView) findViewById(R.id.textViewConfirmPassword);
        mEdTxtConfirmPassword = (EditText) findViewById(R.id.edTxtConfirmPassword);
        mBtnChangePassSubmit = (Button) findViewById(R.id.btnChangePassSubmit);
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
}
