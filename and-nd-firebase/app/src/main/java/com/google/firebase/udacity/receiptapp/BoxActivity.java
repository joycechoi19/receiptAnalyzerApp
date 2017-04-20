package com.google.firebase.udacity.receiptapp;

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

public class BoxActivity extends AppCompatActivity {

    private ArrayList<Receipt> mReceiptList;

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
        RecyclerView.Adapter mAdapter = new BoxAdapter(mReceiptList);
        mRecyclerView.setAdapter(mAdapter);
    }

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
                // TODO: add receipt by calling the receipt scanning activity idk
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
}