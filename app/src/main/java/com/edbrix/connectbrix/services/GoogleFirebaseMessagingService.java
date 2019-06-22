package com.edbrix.connectbrix.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.activities.SchoolListActivity;
import com.edbrix.connectbrix.app.Config;
import com.edbrix.connectbrix.utils.NotificationUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Random;

public class GoogleFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = GoogleFirebaseMessagingService.class.getName();
    private NotificationUtils notificationUtils;
    private LocalBroadcastManager localBroadcastManager;

    private final String SERVICE_RESULT = "com.service.result";
    private final String SERVICE_MESSAGE = "com.service.message";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            //Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

    }

    /*********************************/
    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        } else {
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject json) {

        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            //String tempMsg = String.valueOf(Html.fromHtml(data.getString("message"), null, null));
            //String massage = tempMsg.replaceAll("\\[", "").replaceAll("\\]","");
            String massage = data.getString("message");
            String agenda = data.getString("agenda");
            String meetingDate = data.getString("meeting_date");
            String imageUrl = data.getString("image");


            Intent resultIntent;
            resultIntent = new Intent(getApplicationContext(), SchoolListActivity.class);

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                //Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                //pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);

                //0008
                localBroadcastManager = LocalBroadcastManager.getInstance(this);
                //0008


                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
                sendNotification(getApplicationContext(), title, massage, agenda, meetingDate, resultIntent);

                sendResult("receive");//0008 for notification list update

            } else {
                // app is in background, show the notification in notification tray

                //Intent resultIntent = new Intent(getApplicationContext(), UsersHotoListActivity.class);//DashboardCircularActivity
                //resultIntent.putExtra("message", message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, massage, agenda, meetingDate, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, massage, agenda, meetingDate, resultIntent, imageUrl);
                }
            }
            Log.i(TAG, data.toString());


        } catch (Exception e) {

        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String agenda, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, agenda, timeStamp, intent);
        sendNotification(context, title, message, agenda, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String agenda, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, agenda, timeStamp, intent, imageUrl);
        sendNotification(context, title, message, agenda, timeStamp, intent);

    }

    private void sendNotification(Context context, String title, String message, String agenda, String timeStamp, Intent intent) {
        Bitmap icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.connect_brix_flash);

        String content = message+" "+timeStamp;

        //Intent intent = new Intent(this, DashboardActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setLargeIcon(icon)
                        .setSmallIcon(R.drawable.connect_brix_flash_small)//.setSmallIcon(R.drawable.ic_circle)
                        .setContentTitle(title)//.setContentTitle(getString(R.string.fcm_message))
                        .setContentText(content)
                        //.setGroup(getApplicationContext().getPackageName())
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
        //.setGroupSummary(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        final int SIMPLE_NOTIFICATION_RANDOM_ID = new Random().nextInt(851) + 90;
        notificationManager.notify(SIMPLE_NOTIFICATION_RANDOM_ID /* ID of notification */, notificationBuilder.build());
    }


    private void sendResult(String message) {
        Intent intent = new Intent(SERVICE_RESULT);
        if (message != null)
            intent.putExtra(SERVICE_MESSAGE, message);
        localBroadcastManager.sendBroadcast(intent);
    }
}