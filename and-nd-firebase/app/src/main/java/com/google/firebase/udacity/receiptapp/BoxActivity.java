package com.google.firebase.udacity.receiptapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Displays all receipts scanned and saved using CardView.
 * Can select a receipt for a detailed view, or select
 * option to add another receipt. Fetches user's receipts
 * from Firebase and displays in a grid list.
 */

public class BoxActivity extends Activity {

    private ArrayList<Receipt> mReceiptList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.view_recycler_box);

        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mReceiptList = createDummy();

        RecyclerView.Adapter mAdapter = new BoxAdapter(mReceiptList);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    ArrayList<Receipt> createDummy() {
        ArrayList<Receipt> ret = new ArrayList<>();
        ret.add(new Receipt("07-09-2016", "HOME DEPOT", 9.97));
        ret.add(new Receipt("09-09-1999", "SCRIVENSHAFT'S QUILLS", 124.02));
        return ret;
    }
}