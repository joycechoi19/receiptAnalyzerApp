package com.google.firebase.udacity.receiptapp.features.receiptbox;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.udacity.receiptapp.app.MainActivity;
import com.google.firebase.udacity.receiptapp.features.capture.PreReaderActivity;
import com.google.firebase.udacity.receiptapp.R;
import com.google.firebase.udacity.receiptapp.shared.Receipt;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;

import java.util.ArrayList;

/**
 * Displays all receipts scanned and saved using CardView.
 * Can select a receipt for a detailed view, or select
 * option to add another receipt. Fetches user's receipts
 * from Firebase and displays in a grid list.
 */

public class BoxActivity extends AppCompatActivity
implements BoxAdapter.OnChoiceSelectedListener {

    private static final String TAG = "BoxActivity";
    public static final String sRECEIPTS = "receipts";
    public static final String sUSERS = "users";
    public static final String sRECEIPT = "com.google.firebase.udacity.receiptapp.features.receiptbox.BoxActivity.sRECEIPT";

    private ArrayList<Receipt> mReceiptList;
    private FirebaseAuth mFirebaseAuth;

    private RecyclerView mRecyclerView;
    public RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box);

        // set up toolbar
        Toolbar mToolBar = (Toolbar) findViewById(R.id.menu_toolbar);
        setSupportActionBar(mToolBar);

        mReceiptList = new ArrayList<>();

        // initialize Firebase references
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        if (mFirebaseUser == null) {
            // user not logged in
            // redirect to main screen
            startActivity(new Intent(this, MainActivity.class));
        } else {
            String mUserID = mFirebaseUser.getUid();

            // initialize recyclerview for receipts
            mRecyclerView = (RecyclerView) findViewById(R.id.view_recycler_box);
            mRecyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayoutManager);

            // adds a listener that fires whenever the structure of the
            // receipt nodes under this user changes in the database
            mDatabase.child(sUSERS).child(mUserID).child(sRECEIPTS)
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            updateDataset(dataSnapshot);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Failed to read value
                            Log.d(TAG, "Database data retrieval failed.", databaseError.toException());
                        }
                    });

        }
    }

    /**
     * Starts activity.
     */
    @Override
    protected void onStart() {
        super.onStart();
        // populate recyclerview with initial data
        Log.d(TAG + " init", Integer.toString(mReceiptList.size()));
        mAdapter = new BoxAdapter(this, mReceiptList);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Inflates toolbar menu
     * @param menu  menu object
     * @return      true always
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        // replace add receipt button with pretty icon
        menu.findItem(R.id.action_add_receipt).setIcon(
                new IconDrawable(this, MaterialIcons.md_add)
                        .actionBarSize().color(getColor(R.color.textTitle)));
        return true;
    }

    /**
     * Defines behavior for each menu item in the toolbar
     * @param item   each option item in the toolbar
     * @return       result of each toolbar item (may not always return)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_add_receipt:
                startActivity(new Intent(this, PreReaderActivity.class));
                return true;
            case R.id.action_sign_out:
                mFirebaseAuth.signOut();
                startActivity(new Intent(this, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Using the index of the selected cardView passed from
     * BoxAdapter, this method creates and populates a fragment
     * for a detailed view of the receipt stored at the index
     * @param index location of selected item in receipt list
     */
    @Override
    public void onChoiceSelected(int index) {
        Bundle args = new Bundle();
        Receipt receipt = mReceiptList.get(index);
        args.putSerializable(sRECEIPT, receipt);
        Intent i = new Intent(getApplicationContext(), ReceiptActivity.class);
        i.putExtras(args);
        startActivity(i);
    }

    /**
     * This method grabs the data from the Firebase database instance
     * and handles the serialization back into Receipt objects.
     * The method then calls datasetChanged that runs on the main
     * UI thread to ensure that the RecyclerView is updated correctly.
     * @param snapshot  a snapshot of the Database
     */
    public void updateDataset(DataSnapshot snapshot) {
        Receipt receipt = snapshot.getValue(Receipt.class);
        mReceiptList.add(receipt);
        Log.d(TAG, snapshot.getValue().toString());
        datasetChanged();
    }

    /**
     * Runs on the main UI thread; lets the adapter
     * know that the dataset has been changed and that
     * the RecyclerView must be updated.
     */
    @UiThread
    protected void datasetChanged() {
        mAdapter.notifyDataSetChanged();
    }
}