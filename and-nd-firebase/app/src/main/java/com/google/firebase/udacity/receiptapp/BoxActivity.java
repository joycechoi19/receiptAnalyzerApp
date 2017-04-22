package com.google.firebase.udacity.receiptapp;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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
    static final String sRECEIPT = "com.google.firebase.udacity.receiptapp.BoxActivity.sRECEIPT";

    private ArrayList<Receipt> mReceiptList;

    /**
     * On creation of the activity, we set up the toolbar and
     * fetch the logged in user's data from the database in order
     * to initialize a RecyclerView for the receipts.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box);

        // set up toolbar
        Toolbar mToolBar = (Toolbar) findViewById(R.id.menu_toolbar);
        setSupportActionBar(mToolBar);

        // BEGIN TODO: replace with database invocation
        mReceiptList = createDummy();

        // initialize recyclerview for receipts
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.view_recycler_box);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerView.Adapter mAdapter = new BoxAdapter(this, mReceiptList);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Starts activity.
     */
    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Inflates toolbar menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        // replace add receipt button with pretty icon
        menu.findItem(R.id.action_add_receipt).setIcon(
                new IconDrawable(this, MaterialIcons.md_add)
                        .actionBarSize());
        return true;
    }

    /**
     * Defines behavior for each menu item in the toolbar
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_add_receipt:
                Intent i = new Intent(this,
                        PreReaderActivity.class);
                startActivity(i);
                return true;
            case R.id.action_sign_out:
                // TODO: sign out of firebase here
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * TODO: remove this method at submission
     * @return
     */
    ArrayList<Receipt> createDummy() {
        ArrayList<Receipt> ret = new ArrayList<>();
        ret.add(new Receipt("07-09-2016", "HOME DEPOT", 9.97));
        ret.add(new Receipt("09-09-1999", "SCRIVENSHAFT'S QUILL SHOP", 124.02));
        return ret;
    }

    /**
     * Using the index of the selected cardView passed from
     * BoxAdapter, this method creates and populates a fragment
     * for a detailed view of the receipt stored at the index
     * @param index location of selected item in receipt list
     */
    @Override
    public void onChoiceSelected(int index) {
        FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
        fragTransaction.setCustomAnimations(R.animator.anim_slide_up, R.animator.anim_slide_down);
        ReceiptFragment mFragment = new ReceiptFragment();
        Bundle args = new Bundle();
        Receipt receipt = mReceiptList.get(index);
        args.putSerializable(sRECEIPT, receipt);
        mFragment.setArguments(args);
        fragTransaction.add(R.id.container_fragment_receipt, mFragment).commit();
    }
}