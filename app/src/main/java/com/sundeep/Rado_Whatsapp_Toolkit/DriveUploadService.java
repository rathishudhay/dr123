package com.sundeep.Rado_Whatsapp_Toolkit;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.services.drive.DriveScopes;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class DriveUploadService extends Service implements DriveServiceHelper.DriveData {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    Notification notification;
    String ACTION_STOP_SERVICE= "STOP";

    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        if (ACTION_STOP_SERVICE.equals(intent.getAction())) {
            Log.d("Statics.LOG_TAG","called to cancel service");
            stopSelf();
        }

        String input = intent.getStringExtra("inputExtra");
        String broadcastId=intent.getStringExtra("broadcastId");

        DriveServiceHelper driveServiceHelper=MainActivity.getDriveObject();
        driveServiceHelper.setDriveDataInterface(this);
        driveServiceHelper.backupFiles(broadcastId);


//        startForegroundNotification("Uploading 1/5");
//        Notification notification1 = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle("Foreground Service")
//                .setContentText(input)
//                .setSmallIcon(R.drawable.ic_launcher_background)
//                .setContentIntent(pendingIntent)
//                .setProgress(100,0,true)
//                .build();

//
//        NotificationManager notificationManager = (NotificationManager)
//                getSystemService(NOTIFICATION_SERVICE);
//        Intent intent = new Intent(this, NotificationReceiver.class);
// use System.currentTimeMillis() to have a unique ID for the pending intent
//        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

// build notification
// the addAction re-use the same intent to keep the example short
//        Notification n  = new NotificationCompat.Builder(this,CHANNEL_ID)
//                .setContentTitle("New mail from " + "test@gmail.com")
//                .setContentText("Subject")
//                .setSmallIcon(R.drawable.ic_arrow_back_white)
////                .setContentIntent(pIntent)
//                .setAutoCancel(true)
//                .build();
//
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

//        notificationManager.notify(0, n);





//        startForeground(1,notification);



//        this.stopSelf();
        //do heavy work on a background thread
        //stopSelf();
        return START_NOT_STICKY;
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }


    @Override
    public void sendUploadedDetails(int uploadedFiles, int totalFiles) {

        System.out.println(uploadedFiles+" "+totalFiles);
        if(uploadedFiles!=totalFiles){
            startForegroundNotification("Uploading "+(uploadedFiles+1)+"/"+totalFiles);
        }else{
            startForegroundNotification("Upload completed");
        }

    }


    void startForegroundNotification(String content){

        Intent notificationIntent = new Intent(this, MainActivity.class);
//        notificationIntent.putExtra("broadcastId",broadcastId);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent stopSelf = new Intent(this, DriveUploadService.class);
        stopSelf.setAction(ACTION_STOP_SERVICE);

        PendingIntent pStopSelf = PendingIntent.getService(this, 0, stopSelf,PendingIntent.FLAG_CANCEL_CURRENT);

        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_done_white, "Stop", pStopSelf)
                .build();
        startForeground(1,notification);

//
//
//
//
//        Intent stopSelf = new Intent(this, HelloIntentService.class);
//        stopSelf.setAction(ACTION_STOP_SERVICE);
//
//        PendingIntent pStopSelf = PendingIntent
//                .getService(this, 0, stopSelf
//                        ,PendingIntent.FLAG_CANCEL_CURRENT);  // That you should change this part in your code
//
//        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentText("Bla Bla Bla")
//                .setSmallIcon(R.drawable.ic_notification)
//                .setContentIntent(pendingNotificationIntent)
//                .addAction(R.drawable.xxxx,"Close", pStopSelf)
//                .setSound(null)
//                .build();
//
//        startForeground(REQUEST_CODE, notification);
    }


//    void stopForegroundNotification(){
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//        builder.setContentTitle("abc");
//        builder.setContentText("Press below button to stoP.");
//        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
//        builder.setSmallIcon(R.drawable.ic_launcher_background);
//
//        Intent stopSelf = new Intent(this, DriveUploadService.class);
//        stopSelf.setAction(this.ACTION_STOP_SERVICE);
//        PendingIntent pStopSelf = PendingIntent.getService(this, 0, stopSelf,PendingIntent.FLAG_CANCEL_CURRENT);
//        builder.addAction(R.drawable.ic_done_white, "Stop", pStopSelf);
//        manager.notify(1, builder.build());
//    }
}