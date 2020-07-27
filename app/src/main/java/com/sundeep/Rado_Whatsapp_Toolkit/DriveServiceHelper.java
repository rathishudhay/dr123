package com.sundeep.Rado_Whatsapp_Toolkit;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriveServiceHelper{
    private final Executor mExecutor= Executors.newSingleThreadExecutor();
    private Drive mDriveService;
    String broadcastRootFolderId;
    String currentBroadcastFolderId;
    String broadcastId;
    JSONArray uploadFilePaths=new JSONArray();
    JSONArray removeFilePaths=new JSONArray();
    Context context;
    DriveData mDriveData;
    int totalFilesToUpload;

    public DriveServiceHelper(Drive mDriveService, Context context){
        this.mDriveService=mDriveService;
        this.context=context;
    }

    public Task<String> uploadFile(String parentId,String filePath){
        return Tasks.call(mExecutor,()->{
            System.out.println("Uploading:123:"+parentId);
            File fileMetaData=new File();
            fileMetaData.setName(filePath.substring(filePath.lastIndexOf("/")+1));
            fileMetaData.setParents(Collections.singletonList(parentId));
//            fileMetaData.setParents(Collections.singletonList(parentId));
            java.io.File file=new java.io.File(filePath);
            System.out.println("Uploading:5:"+filePath);
            FileContent mediaContent=new FileContent("application/octet-stream",file);
            System.out.println("Uploading:6");
            File myFile=null;
//            Drive.Builder
            try{
//                myFile=mDriveService.files().create(fileMetaData,mediaContent).setFields("id, parents").execute();
                myFile=mDriveService.files().create(fileMetaData,mediaContent).execute();
                System.out.println("id:"+myFile.getId());
//                myFile.execute();
            }catch(Exception e){
                e.printStackTrace();
            }

            if(myFile==null){
                throw new IOException("Not null when requesting file creation");
            }
            System.out.println("Uploading:7");

            return myFile.getId();
        });
    }

    public Task<String> createFolder(String filePath){
        return Tasks.call(mExecutor,()->{
            File fileMetaData=new File();
            fileMetaData.setName("MyVideo");
            java.io.File file=new java.io.File(filePath);
            FileContent mediaContent=new FileContent("video/mp4",file);
            File myFile=null;
            try{
                myFile=mDriveService.files().create(fileMetaData,mediaContent).execute();
            }catch(Exception e){
                e.printStackTrace();
            }

            if(myFile==null){
                throw new IOException("Not null when requesting file creation");
            }

            return myFile.getId();
        });
    }

    public void createFolder(String parentId,String folderName){
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                File fileMetadata = new File();
                fileMetadata.setName(folderName);
                fileMetadata.setMimeType("application/vnd.google-apps.folder");
                if(parentId!=null){
                    fileMetadata.setParents(Collections.singletonList(parentId));
                }
                File file = null;
                try {
                    file = mDriveService.files().create(fileMetadata)
                            .setFields("id")
                            .execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


    public void moveToFolder(){

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                String pageToken = null;
                do {
                    String rootFileId="1SVxOMY2vQhrJiz5CxIpetvYqgT4MfgzO";
                    String filepath="root5/root6/root8/root10/photo1.png";
                    String parentId=rootFileId;
                    while(filepath.contains("/")) {
                        String folderName = filepath.substring(0, filepath.indexOf("/"));
                        filepath=filepath.substring(filepath.indexOf("/")+1);
                        FileList result = null;
                        try {
                            result = mDriveService.files().list()
//                                .setQ("mimeType='image/jpeg'")
//                                    .setQ(parentId+" in parents and name = '"+folderName+"' and mimeType='application/vnd.google-apps.folder'")
                                    .setQ("mimeType='application/vnd.google-apps.folder' and name = '"+folderName+"' and '"+parentId+"' in parents")
                                    .setSpaces("drive")
                                    .setFields("nextPageToken, files(id, name)")
                                    .setPageToken(pageToken)
                                    .execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if(result.getFiles().size()==0){
                            // Create a new folder with the foldername
                            System.out.println("Result is null:"+folderName);
                            File fileMetadata = new File();
                            fileMetadata.setName(folderName);
                            fileMetadata.setMimeType("application/vnd.google-apps.folder");
                            fileMetadata.setParents(Collections.singletonList(parentId));

                            File file = null;
                            try {
                                file = mDriveService.files().create(fileMetadata)
                                        .setFields("id")
                                        .execute();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.out.println("New Folder: " + file.getId()+" name:"+folderName);
                            parentId=file.getId();
                        }else{
                            System.out.println("Result is not null:"+folderName);
                            parentId=result.getFiles().get(0).getId();
                        }
//                        pageToken = result.getNextPageToken();
                    }

                    if(filepath.contains(".jpg")){
                        String fileName=filepath.substring(0,filepath.indexOf("."));
                        File fileMetaData=new File();
                        fileMetaData.setName(fileName);
                        fileMetaData.setParents(Collections.singletonList(parentId));
                        java.io.File file=new java.io.File(Environment.getExternalStorageDirectory()+"/Pictures/1591813045841.jpg");
                        FileContent mediaContent=new FileContent("image/jpeg",file);
                        File myFile=null;
                        try{
//                myFile=mDriveService.files().create(fileMetaData,mediaContent).setFields("id, parents").execute();
                            myFile=mDriveService.files().create(fileMetaData,mediaContent).execute();
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                        if(myFile==null){
                            try {
                                throw new IOException("Not null when requesting file creation");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }else if(filepath.contains(".png")){
                        String fileName=filepath.substring(0,filepath.indexOf("."));
                        File fileMetaData=new File();
                        fileMetaData.setName(fileName);
                        fileMetaData.setParents(Collections.singletonList(parentId));
                        java.io.File file=new java.io.File(Environment.getExternalStorageDirectory()+"/Pictures/abc.png");
                        System.out.println("Hello123:"+Environment.getExternalStorageDirectory()+"/Pictures/abc.png");
                        FileContent mediaContent=new FileContent("image/png",file);
                        File myFile=null;
                        try{
//                myFile=mDriveService.files().create(fileMetaData,mediaContent).setFields("id, parents").execute();
                            myFile=mDriveService.files().create(fileMetaData,mediaContent).execute();
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                        if(myFile==null){
                            try {
                                throw new IOException("Not null when requesting file creation");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }else{
                        if(filepath.contains(".mp4")){
                            String fileName=filepath.substring(0,filepath.indexOf("."));
                            File fileMetaData=new File();
                            fileMetaData.setName(fileName);
                            fileMetaData.setParents(Collections.singletonList(parentId));
                            java.io.File file=new java.io.File(Environment.getExternalStorageDirectory()+"/Pictures/1591813045841.jpg");
                            FileContent mediaContent=new FileContent("image/mp4",file);
                            File myFile=null;
                            try{
//                myFile=mDriveService.files().create(fileMetaData,mediaContent).setFields("id, parents").execute();
                                myFile=mDriveService.files().create(fileMetaData,mediaContent).execute();
                            }catch(Exception e){
                                e.printStackTrace();
                            }

                            if(myFile==null){
                                try {
                                    throw new IOException("Not null when requesting file creation");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    System.out.println("folderName123:" + filepath);


                } while (pageToken != null);

            }
        });
        thread.start();
    }


//    public static String loadJSONFromAsset(Activity activity,String filepath) {
//        String json = null;
//        try {
//            InputStream is = activity.getAssets().open(filepath);
//            int size = is.available();
//            byte[] buffer = new byte[size];
//            is.read(buffer);
//            is.close();
//            json = new String(buffer, "UTF-8");
//        } catch (IOException ex) {
//            System.out.println("Error123");
//            ex.printStackTrace();
//            return null;
//        }
//        return json;
//    }


    public Task<String> uploadFolder(String parentId,String folderName) {

        return Tasks.call(mExecutor, () -> {
            File fileMetadata = new File();
            fileMetadata.setName(folderName);

            fileMetadata.setMimeType("application/vnd.google-apps.folder");

            System.out.println("ID:"+parentId);
            if(parentId!=null){
                System.out.println("inID:"+parentId);
                fileMetadata.setParents(Collections.singletonList(parentId));
            }

            File file = null;
            try {
                file = mDriveService.files().create(fileMetadata)
                        .setFields("id")
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("FileId:"+file.getId());

            return file.getId();
        });



//
//        Thread thread = new Thread(new Runnable(){
//            @Override
//            public void run() {
//                File fileMetadata = new File();
//                fileMetadata.setName(folderName);
//                fileMetadata.setMimeType("application/vnd.google-apps.folder");
//                if(parentId!=null){
//                    fileMetadata.setParents(Collections.singletonList(parentId));
//                }
//                File file = null;
//                try {
//                    file = mDriveService.files().create(fileMetadata)
//                            .setFields("id")
//                            .execute();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        thread.start();

    }




    void backupFiles(String broadcastId){
        this.broadcastId=broadcastId;
        try {
            broadcastRootFolderId=Broadcaster_JSON.getBroadCastRootInDrive();

            System.out.println("outside root if");
            if(broadcastRootFolderId.equals("null")){
                System.out.println("inside root if");
                createRootFolderInDrive();
            }else{
                //Check broadcaster exists
                System.out.println("bid:"+broadcastId);
                String broadCastDriveFolderId=Broadcaster_JSON.getBroadCastDriveFolderId(broadcastId);
                System.out.println(broadCastDriveFolderId);
                currentBroadcastFolderId=broadCastDriveFolderId;
                if(broadCastDriveFolderId==null){
                    System.out.println("inside"+broadCastDriveFolderId);
                    createBroadcastFolderInDrive(broadcastRootFolderId,broadcastId);
                }else{
                    try {
//                        Broadcaster_JSON.writeBroadcasterFolderId(broadcastId,folderId);
                        JSONObject filePathsJSON=Broadcaster_JSON.getBackupFiles(broadcastId);
                        uploadFilePaths=filePathsJSON.getJSONArray("backupPaths");
                        removeFilePaths=filePathsJSON.getJSONArray("removePaths");
                        totalFilesToUpload=uploadFilePaths.length();
                        mDriveData.sendUploadedDetails(totalFilesToUpload-uploadFilePaths.length(),totalFilesToUpload);
                        performFileUpload();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    void createRootFolderInDrive(){
        ProgressDialog progressDialog=new ProgressDialog(context);
        progressDialog.setTitle("Uploading to google drive");
        progressDialog.setMessage("Please wait");
//        progressDialog.show();

        uploadFolder(null,"Broadcaster").addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String folderId) {
//                progressDialog.dismiss();
                Toast.makeText(context,"Uploaded successfully",Toast.LENGTH_LONG).show();
                System.out.println("BroadcasterId123:"+folderId);
//                filesArray.remove(0);
                broadcastRootFolderId=folderId;
                try {
                    Broadcaster_JSON.writeBroadcastRootFolderId(folderId);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                createBroadcastFolderInDrive(folderId,broadcastId);
//                performFileUpload();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
//                progressDialog.dismiss();
                Toast.makeText(context,"Cannot connect to drive",Toast.LENGTH_LONG).show();
            }
        });
    }


    void createBroadcastFolderInDrive(String parentFolderId,String broadcastId){
        ProgressDialog progressDialog=new ProgressDialog(context);
        progressDialog.setTitle("Uploading to google drive");
        progressDialog.setMessage("Please wait");
//        progressDialog.show();

        uploadFolder(parentFolderId,broadcastId).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String folderId) {
//                progressDialog.dismiss();
                Toast.makeText(context,"Uploaded successfully",Toast.LENGTH_LONG).show();
                System.out.println("BroadcasterFolderId:"+folderId+"  "+broadcastId);
//                filesArray.remove(0);
                currentBroadcastFolderId=folderId;
                try {
                    Broadcaster_JSON.writeBroadcasterFolderId(broadcastId,folderId);
                    JSONObject filePathsJSON=Broadcaster_JSON.getBackupFiles(broadcastId);
                    uploadFilePaths=filePathsJSON.getJSONArray("backupPaths");

                    removeFilePaths=filePathsJSON.getJSONArray("removePaths");
                    totalFilesToUpload=uploadFilePaths.length();
                    mDriveData.sendUploadedDetails(totalFilesToUpload-uploadFilePaths.length(),totalFilesToUpload);
                    System.out.println("TotalFiles:"+totalFilesToUpload);
                    performFileUpload();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
//                progressDialog.dismiss();
                Toast.makeText(context,"Cannot connect to drive",Toast.LENGTH_LONG).show();
            }
        });
    }



    void performFileUpload(){
        System.out.println("uploadFilePaths:"+uploadFilePaths);

        if(uploadFilePaths.length()!=0)
        {
            JSONObject fileObj=new JSONObject();
            String filePath= null;
            int index=-1;
            try {
                fileObj=uploadFilePaths.getJSONObject(0);
                filePath = fileObj.getString("path");
                index=fileObj.getInt("index");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            uploadFile(filePath,index);
        }
        else{
            ArrayList<Integer> removeFilePathIndex=new ArrayList<Integer>();
            for(int i=0;i<removeFilePaths.length();i++){
                try {
                    JSONObject removeObject = removeFilePaths.getJSONObject(i);
                    int removeIndex=removeObject.getInt("index");
                    removeFilePathIndex.add(removeIndex);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Comparator c = Collections.reverseOrder();
            Collections.sort(removeFilePathIndex,c);

            Broadcaster_JSON.removeFilesFromBackup(broadcastId,removeFilePathIndex);
        }
    }


    void uploadFile(String filePath, int fileIndexInJson){
        ProgressDialog progressDialog=new ProgressDialog(context);
        progressDialog.setTitle("Uploading to google drive");
        progressDialog.setMessage("Please wait");
//        progressDialog.show();

//        String mimeType=
        System.out.println("Uploading to google drive:"+filePath);
//        filePath= Environment.getExternalStorageDirectory()+"/Pictures/1591813045841.jpg";
        System.out.println("currentBroadcastFolderId"+currentBroadcastFolderId);
        uploadFile(currentBroadcastFolderId,filePath).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
//                progressDialog.dismiss();
                Toast.makeText(context,"Uploaded successfully",Toast.LENGTH_LONG).show();
                System.out.println("SID:"+s);
//                filesArray.remove(0);
                Broadcaster_JSON.updateUploadFileToTrue(broadcastId,fileIndexInJson);
                uploadFilePaths.remove(0);
                mDriveData.sendUploadedDetails(totalFilesToUpload-uploadFilePaths.length(),totalFilesToUpload);
                performFileUpload();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
//                progressDialog.dismiss();
                Toast.makeText(context,"Cannot connect to drive",Toast.LENGTH_LONG).show();
            }
        });
    }


//    public void cancelUpload(){
//
//        JSONObject currentObject= null;
//        try {
//            currentObject = uploadFilePaths.getJSONObject(0);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        uploadFilePaths = new JSONArray(new ArrayList<String>());
//        uploadFilePaths.put(currentObject);
//    }


    public interface DriveData{
//        void onGridClick(int position);

        void sendUploadedDetails(int uploadedFiles,int totalFiles);
    }

    public void setDriveDataInterface(DriveData driveData){
        this.mDriveData=driveData;
    }

}
