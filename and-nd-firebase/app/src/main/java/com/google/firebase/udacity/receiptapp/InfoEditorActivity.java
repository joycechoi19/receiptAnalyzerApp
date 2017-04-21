package com.google.firebase.udacity.receiptapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;


public class InfoEditorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_editor);
    }

//    public ArrayList<String> getReceipt(ArrayList<String> receipt){
//        Intent i = new Intent(OcrCaptureActivity.this, InfoEditorActivity.class);
//                startActivity(i);
//
//        ArrayList<String> info = OcrCaptureActivity.getReceipt();
//        return info;
//    }


    public String getStoreName(ArrayList<String> info){
        String storeName = info.get(0);
        return storeName;
    }
    public String getAddress(ArrayList<String> info){
        String address = info.get(1);
        return address;
    }
    public String getPrice(ArrayList<String> info){
        String price = info.get(2);
        return price;
    }

}
