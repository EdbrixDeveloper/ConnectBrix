package com.edbrix.connectbrix.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.edbrix.connectbrix.Application;
import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.baseclass.BaseActivity;
import com.edbrix.connectbrix.data.UploadProfilePicResponseData;
import com.edbrix.connectbrix.utils.Constants;
import com.edbrix.connectbrix.utils.SessionManager;
import com.edbrix.connectbrix.volley.GsonRequest;
import com.edbrix.connectbrix.volley.SettingsMy;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfilePicActivity extends BaseActivity {

    private static final String TAG = UpdateProfilePicActivity.class.getName();
    public static final int REQUEST_IMAGE = 100;

    private CircleImageView mImgProfile;
    private Button mBtnUpdateProfilePic;
    private TextView mSelectPhoto;
    SessionManager sessionManager;

    String base64StringUserProfile = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_pic);
        getSupportActionBar().setTitle("Profile Picture");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(this);

        assignViews();
        clickListners();
        loadProfileDefault();

        ImagePickerActivity.clearCache(this);

    }

    private void loadProfile(String url) {
        Log.d(TAG, "Image cache path: " + url);

        Glide.with(this).load(url)
                .into(mImgProfile);
        mImgProfile.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
    }

    private void loadProfileDefault() {


        if (sessionManager.getSessionProfileImageUrl().isEmpty()) {
            Glide.with(this).load(R.drawable.baseline_account_circle_black_48)
                    .into(mImgProfile);
            mImgProfile.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary));
        } else {
            int randomNumber = generateRandomIntIntRange(0001,9999);
            String imageUrl = sessionManager.getSessionProfileImageUrl()+"?id="+randomNumber;
            Glide.with(this).load(imageUrl)
                    .into(mImgProfile);
        }
    }

    private void clickListners() {

        mSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(UpdateProfilePicActivity.this)
                        .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    showImagePickerOptions();
                                }

                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    showSettingsDialog();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });

        mBtnUpdateProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!base64StringUserProfile.isEmpty()) {
                    updateProfilePicture();
                } else {
                    showToast("Please select image first");
                }

            }
        });
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(UpdateProfilePicActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(UpdateProfilePicActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);

                    byte[] bitmapDataArray = stream.toByteArray();
                    base64StringUserProfile = Base64.encodeToString(bitmapDataArray, Base64.DEFAULT);
                    // loading profile image from local cache
                    loadProfile(uri.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void assignViews() {
        mImgProfile = (CircleImageView) findViewById(R.id.imgProfile);
        mBtnUpdateProfilePic = (Button) findViewById(R.id.btnUpdateProfilePic);
        mSelectPhoto = (TextView) findViewById(R.id.selectPhoto);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

               /* setResult(RESULT_OK);
                finish();
                Intent intent = new Intent(UpdateProfilePicActivity.this,UserProfileActivity.class);
                startActivity(intent);
               */
               onBackPressed();
               return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfilePicActivity.this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void updateProfilePicture() {
        try {
            showBusyProgress();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("UserId", sessionManager.getSessionUserId());
            jsonObject.put("AccessToken", sessionManager.getPrefsSessionAccessToken());
            jsonObject.put("UploadFileTitle", sessionManager.getSessionUserFirstName()+"_"+sessionManager.getSessionUserId() + ".png");
            jsonObject.put("UploadFileEncodeString", base64StringUserProfile);

            GsonRequest<UploadProfilePicResponseData> uploadUserProfilePictureRequest = new GsonRequest<>(Request.Method.POST, Constants.updateUserProfilePicture, jsonObject.toString(), UploadProfilePicResponseData.class,
                    new Response.Listener<UploadProfilePicResponseData>() {
                        @Override
                        public void onResponse(@NonNull UploadProfilePicResponseData response) {
                            hideBusyProgress();
                            if (response.getError() != null) {
                                String error = response.getError().getErrorMessage();
                                showToast(error);
                            } else {
                                if (response.getSuccess() == 1) {
                                    showToast(response.getMessage());
                                    finish();
                                    Intent intent = new Intent(UpdateProfilePicActivity.this,UserProfileActivity.class);
                                    startActivity(intent);

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
            uploadUserProfilePictureRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            uploadUserProfilePictureRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(uploadUserProfilePictureRequest, "uploadUserProfilePictureRequest");

        } catch (Exception e) {
            hideBusyProgress();
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        setResult(RESULT_OK);
        Intent intent = new Intent(UpdateProfilePicActivity.this,UserProfileActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }


}
