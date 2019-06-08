package com.edbrix.connectbrix.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.edbrix.connectbrix.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfilePicActivity extends AppCompatActivity {

    private CircleImageView mImgProfile;
    private Button mBtnUpdateProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_pic);
        getSupportActionBar().setTitle("Profile Picture");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        assignViews();

    }

    private void assignViews() {
        mImgProfile = (CircleImageView) findViewById(R.id.imgProfile);
        mBtnUpdateProfilePic = (Button) findViewById(R.id.btnUpdateProfilePic);
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
