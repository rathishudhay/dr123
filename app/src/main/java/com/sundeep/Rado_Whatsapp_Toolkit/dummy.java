package com.sundeep.Rado_Whatsapp_Toolkit;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;

public class dummy extends AppCompatActivity {

    Button uploadButton,downloadButton;
    final int REQUEST_CODE_PICK_WHATSAPP=200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        uploadButton=(Button)findViewById(R.id.uploadButton);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupTest1();
            }
        });

        downloadButton=(Button)findViewById(R.id.downloadButton);

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                shareOnWhatsapp(dummy.this,"Hello",null);
//                shareDirectlyToWhatsapp();
                groupTest2();
            }
        });
    }


    void uploadFile(){
        Intent intentWhatsapp = new Intent(Intent.ACTION_VIEW);
        String url = "https://chat.whatsapp.com/EJkJa5e8krK0vTaGfjFktP";
        intentWhatsapp.setData(Uri.parse(url));
        intentWhatsapp.setPackage("com.whatsapp");
        startActivity(intentWhatsapp);
    }

    public static void shareOnWhatsapp(AppCompatActivity appCompatActivity, String textBody, Uri fileUri) {
        String filePath= Environment.getExternalStorageDirectory()+"/Pictures/1591813045841.jpg";
        fileUri=Uri.fromFile(new File(filePath));
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String url = "https://chat.whatsapp.com/EJkJa5e8krK0vTaGfjFktP";
        intent.setData(Uri.parse(url));

        intent.setPackage("com.whatsapp");
        intent.putExtra(Intent.EXTRA_TEXT,!TextUtils.isEmpty(textBody) ? textBody : "");

        if (fileUri != null) {
            intent.putExtra(Intent.EXTRA_STREAM, fileUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/*");
        }

        try {
            appCompatActivity.startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();
//            showWarningDialog(appCompatActivity, appCompatActivity.getString(R.string.error_activity_not_found));
        }
    }


    void shareDirectlyToWhatsapp(){
        PackageManager packageManager = getApplicationContext().getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);
        String phone="+919994114106";
        String message="abc";
//        phone="+911593440781";

        try {
//            String url = "https://api.whatsapp.com/send?id="+ phone +"&text=" + URLEncoder.encode(message, "UTF-8");
            String url = "https://wa.me/"+ phone +"?text=" + URLEncoder.encode(message, "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                getApplicationContext().startActivity(i);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    void groupCheck(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setPackage("com.whatsapp");
        try{
            startActivityForResult(intent, REQUEST_CODE_PICK_WHATSAPP);
        } catch (Exception e) {
            Toast.makeText(dummy.this, "Error", Toast.LENGTH_SHORT).show();  //no activity found to handle this intent means whatsapp is not installed
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        System.out.println("Hello");
        switch (requestCode) {
            case REQUEST_CODE_PICK_WHATSAPP:
                if(resultCode == RESULT_OK){
                    if(intent.hasExtra("contact")){
                        String address = intent.getStringExtra("contact");
                        Log.d("TAG", "The selected Whatsapp address is: "+address);
                        Toast.makeText(dummy.this,"Callback",Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case 100:
                if(resultCode == RESULT_OK){
                    Log.d("Hello","World");
                    Toast.makeText(dummy.this,"Callback100",Toast.LENGTH_SHORT).show();
                }
                break;



            default:
                break;
        }
    }


    public void openWhatsApp(View view){
        PackageManager pm=getPackageManager();
        try {


            String toNumber = "EJkJa5e8krK0vTaGfjFktP"; // Replace with mobile phone number without +Sign or leading zeros, but with country code.
            //Suppose your country is India and your phone number is “xxxxxxxxxx”, then you need to send “91xxxxxxxxxx”.



            Intent sendIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:" + "" + toNumber + "?body=" + "abc"));
            sendIntent.setPackage("com.whatsapp");
            startActivity(sendIntent);
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(dummy.this,"it may be you dont have whats app",Toast.LENGTH_LONG).show();

        }
    }



    void groupTest1(){

        String number="1593440781";
        String message="Hello";
        String textBody="Hello";

        PackageManager pm=getPackageManager();
        try {
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/txt");

            PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            waIntent.putExtra("jid", "916374497441-1593440781@g.us");
            waIntent.setPackage("com.whatsapp");
            String filePath= Environment.getExternalStorageDirectory()+"/Pictures/1591813045841.jpg";
            Uri fileUri=Uri.fromFile(new File(filePath));
            waIntent.putExtra(Intent.EXTRA_TEXT,!TextUtils.isEmpty(textBody) ? textBody : "");
            if (fileUri != null) {
                waIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                waIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                waIntent.setType("image/*");
            }


//            Uri uri = Uri.parse( String.format("https://api.whatsapp.com/sendphone=%s&text=%s",
//                    number, message));
//            waIntent.putExtra(Intent.EXTRA_STREAM,uri);
            startActivity(Intent.createChooser(waIntent, "Share with"));
        } catch (PackageManager.NameNotFoundException e) {
            //error message
        }
    }


    void groupTest2(){

        String number="1593440781";
        String message="Hello";
        String textBody="Hello";

        PackageManager pm=getPackageManager();
        try {
            ArrayList<Uri> imageUriArray=new ArrayList<Uri>();
            Intent waIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
//            waIntent.setType("text/txt");
//            PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            waIntent.putExtra("jid", "916374497441-1593440781@g.us");
            waIntent.setPackage("com.whatsapp");
            String filePath= Environment.getExternalStorageDirectory()+"/1.pdf";
            Uri fileUri=Uri.fromFile(new File(filePath));
//            imageUriArray.add(fileUri);
//            filePath= Environment.getExternalStorageDirectory()+"/Pictures/1593087310554.jpg";
//            fileUri=Uri.fromFile(new File(filePath));
//            imageUriArray.add(fileUri);
//            ArrayList<String> messageArray=new ArrayList<String>();
//            messageArray.add("Hello");
//            messageArray.add("World");
            waIntent.putExtra(Intent.EXTRA_TEXT,"Hello");
//            waIntent.putExtra(Intent.EXTRA_SUBJECT,"Rathish");
//            if (fileUri != null) {
//
//            }

            waIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            waIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            waIntent.setType("image/*");

//            waIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUriArray);
//            waIntent.putParcelableArrayListExtra(Intent.EXTRA_TEXT,messageArray);


//            Uri uri = Uri.parse( String.format("https://api.whatsapp.com/sendphone=%s&text=%s",
//                    number, message));
//            waIntent.putExtra(Intent.EXTRA_STREAM,uri);
            startActivityForResult(waIntent,100);
        } catch (Exception e) {
            //error message
            e.printStackTrace();
            Toast.makeText(dummy.this,"Exception",Toast.LENGTH_SHORT).show();
        }
    }






}
