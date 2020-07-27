package com.sundeep.Rado_Whatsapp_Toolkit;


import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.gson.internal.$Gson$Preconditions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    Button uploadButton,downloadButton,startServiceButton,stopServiceButton;
    static DriveServiceHelper driveServiceHelper;
String broadcastId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String b=getIntent().getStringExtra("broadcastId");
//        if(b!=null){
            System.out.println("Intent123"+b);
//        }


        broadcastId="1594733037694";
System.out.println("Env:"+Environment.getExternalStorageDirectory());
//        filesArray.add(Environment.getExternalStorageDirectory()+"/Temp/zip.zip");
//        filesArray.add(Environment.getExternalStorageDirectory()+"/Temp/csv.csv");
//        filesArray.add(Environment.getExternalStorageDirectory()+"/Temp/xls.xls");
//        filesArray.add(Environment.getExternalStorageDirectory()+"/Temp/xlsx.xlsx");
//        filesArray.add(Environment.getExternalStorageDirectory()+"/Temp/xlw.xlw");
//        filesArray.add(Environment.getExternalStorageDirectory()+"/Temp/ppt.ppt");
//        filesArray.add(Environment.getExternalStorageDirectory()+"/Temp/pptx.pptx");
//        filesArray.add(Environment.getExternalStorageDirectory()+"/Temp/webp.webp");
//        filesArray.add(Environment.getExternalStorageDirectory()+"/Temp/png.png");
//        filesArray.add(Environment.getExternalStorageDirectory()+"/Temp/gif.gif");
//        filesArray.add(Environment.getExternalStorageDirectory()+"/Temp/jpeg.jpeg");
//        filesArray.add(Environment.getExternalStorageDirectory()+"/Temp/jpg.jpg");
//        filesArray.add(Environment.getExternalStorageDirectory()+"/Temp/pdf.pdf");
//        filesArray.add(Environment.getExternalStorageDirectory()+"/Temp/3gp.3gp");
//        filesArray.add(Environment.getExternalStorageDirectory()+"/Temp/3gpp.3gpp");
//        filesArray.add(Environment.getExternalStorageDirectory()+"/Temp/mp4.mp4");
//        filesArray.add(Environment.getExternalStorageDirectory()+"/Temp/wmv.wmv");
//        filesArray.add(Environment.getExternalStorageDirectory()+"/Temp/txt.txt");
//        filesArray.add(Environment.getExternalStorageDirectory()+"/Temp/apk.apk");
//        filesArray.add(Environment.getExternalStorageDirectory()+"/Temp/doc.doc");
//        filesArray.add(Environment.getExternalStorageDirectory()+"/Temp/docx.docx");
//        filesArray.add(Environment.getExternalStorageDirectory()+"/Temp/aac.aac");
//        filesArray.add(Environment.getExternalStorageDirectory()+"/Temp/amr.amr");
//        filesArray.add(Environment.getExternalStorageDirectory()+"/Temp/m4a.m4a");
//        filesArray.add(Environment.getExternalStorageDirectory()+"/Temp/mp3.mp3");

        uploadButton=(Button)findViewById(R.id.uploadButton);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                requestSignIn();
//                try {
//                    System.out.println(Broadcaster_JSON.getBackupFiles("1594733037694").length()+": "+Broadcaster_JSON.getBackupFiles("1594733037694"));//1594733037694
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        });

        downloadButton=(Button)findViewById(R.id.downloadButton);

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                driveServiceHelper.cancelUpload();
//                driveServiceHelper.createFolder(null,"Hello");
                stopService();
            }
        });

        startServiceButton=(Button)findViewById(R.id.start_service);

        stopServiceButton=(Button)findViewById(R.id.stop_service);

        startServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService();
            }
        });

        stopServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService();
            }
        });

    }

    private void requestSignIn(){
        GoogleSignInOptions signInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                .build();

        GoogleSignInClient client= GoogleSignIn.getClient(this,signInOptions);
        startActivityForResult(client.getSignInIntent(),400);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 400:
                if(resultCode==RESULT_OK){
                    handleSignInIntent(data);
                }
                break;
        }
    }

    private void handleSignInIntent(Intent data) {
        GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                        GoogleAccountCredential credential= GoogleAccountCredential.usingOAuth2(MainActivity.this, Collections.singleton(DriveScopes.DRIVE_FILE));

                        credential.setSelectedAccount(googleSignInAccount.getAccount());

                        Drive googleDriveService=new Drive.Builder(
                                AndroidHttp.newCompatibleTransport(),
                                new GsonFactory(),
                                credential)
                                .setApplicationName("My Drive Tutorial")
                                .build();

                        driveServiceHelper=new DriveServiceHelper(googleDriveService,getApplicationContext());

                        //driveServiceHelper.backupFiles(broadcastId);
                        startService();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }


    public void startService() {
        Intent serviceIntent = new Intent(this, DriveUploadService.class);
        System.out.println("Hello");
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
        serviceIntent.putExtra("broadcastId",broadcastId);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, DriveUploadService.class);
        stopService(serviceIntent);
    }

    public static DriveServiceHelper getDriveObject(){
        return driveServiceHelper;
    }




















    private LocalService mBoundService;
    private boolean mShouldUnbind;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((LocalService.LocalBinder)service).getService();
            // Tell the user about this for our demo.
            Toast.makeText(MainActivity.this, "local_service_connected",
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null;
            Toast.makeText(MainActivity.this, "local_service_disconnected",
                    Toast.LENGTH_SHORT).show();
        }
    };

    void doBindService() {
        // Attempts to establish a connection with the service.  We use an
        // explicit class name because we want a specific service
        // implementation that we know will be running in our own process
        // (and thus won't be supporting component replacement by other
        // applications).
        if (bindService(new Intent(MainActivity.this, LocalService.class),
                mConnection, Context.BIND_AUTO_CREATE)) {
            mShouldUnbind = true;
        } else {
            Log.e("MY_APP_TAG", "Error: The requested service doesn't " +
                    "exist, or this client isn't allowed access to it.");
        }
    }

    void doUnbindService() {
        if (mShouldUnbind) {
            // Release information about the service's state.
            unbindService(mConnection);
            mShouldUnbind = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }
}
