package com.sundeep.Rado_Whatsapp_Toolkit;

import android.content.Context;
import android.os.Environment;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class JsonUtils {
    static String getJsonStringFromAssets(String fileName) {
        String jsonString;
        try {
            InputStream is = new FileInputStream(
                    new File(Environment.getExternalStorageDirectory()+"src/main/resources/sample.txt"));

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            jsonString = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return jsonString;
    }

    public static JSONObject getJsonObjectFromString(String jsonString){
        JSONObject json=null;
        try{
            json=new JSONObject(jsonString);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return json;
    }

    public static void addNewFilepathToJsonObject(JSONObject jsonObject){
        //add filepath to array
    }

    public static void writeJsonObjectToFile(JSONObject jsonObject){
        //write the json object to external file
    }


}
