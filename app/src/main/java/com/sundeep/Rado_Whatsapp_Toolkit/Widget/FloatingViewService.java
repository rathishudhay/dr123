package com.sundeep.Rado_Whatsapp_Toolkit.Widget;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.sundeep.Rado_Whatsapp_Toolkit.R;
import com.sundeep.Rado_Whatsapp_Toolkit.dummy;

import java.io.File;
import java.util.ArrayList;

public class FloatingViewService extends Service {

    private WindowManager mWindowManager;
    private View mFloatingView;
    private View kapali_widget;
    private View acik_widget;

    public FloatingViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        //getting the widget layout from xml using layout inflater
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);

        //setting the layout parameters
//        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.TYPE_PHONE,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                PixelFormat.TRANSLUCENT);
        final WindowManager.LayoutParams params;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }


        //getting windows services and adding the floating view to it
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);


        //getting the collapsed and expanded view from the floating view
        kapali_widget = mFloatingView.findViewById(R.id.layoutCollapsed);
        acik_widget = mFloatingView.findViewById(R.id.layoutExpanded);


        ImageView btnClose=mFloatingView.findViewById(R.id.buttonClose);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                stopSelf();
                groupTest1();
            }
        });

        acik_widget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kapali_widget.setVisibility(View.VISIBLE);
                acik_widget.setVisibility(View.GONE);
            }
        });



        //adding an touchlistener to make drag movement of the floating widget
        mFloatingView.findViewById(R.id.relativeLayoutParent).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_UP:
                        //when the drag is ended switching the state of the widget
//                        kapali_widget.setVisibility(View.GONE);
//                        acik_widget.setVisibility(View.VISIBLE);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        //this code is helping the widget to move around the screen with fingers
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;

                    case MotionEvent.ACTION_BUTTON_PRESS:
                        kapali_widget.setVisibility(View.GONE);
                        acik_widget.setVisibility(View.VISIBLE);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }


    void groupTest2(){
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        String number="1593440781";
        String message="Hello";
        String textBody="Hello";

        PackageManager pm=getPackageManager();
        try {
//            ArrayList<Uri> imageUriArray=new ArrayList<Uri>();
            Intent waIntent = new Intent(Intent.ACTION_SEND);
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
            getApplicationContext().startActivity(waIntent);
        } catch (Exception e) {
            //error message
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Exception",Toast.LENGTH_SHORT).show();
        }
    }

    void groupTest1(){
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

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




}