package com.google.firebase.udacity.receiptapp.features.receiptbox;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.udacity.receiptapp.R;
import com.google.firebase.udacity.receiptapp.shared.Receipt;

import java.text.NumberFormat;

import static com.google.firebase.udacity.receiptapp.features.receiptbox.BoxActivity.sRECEIPT;

/**
 * Displays a detailed view of a receipt, complete with
 * a map of the receipt's location.
 */

public class ReceiptActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "ReceiptActivity";
    private static Receipt mReceipt;
    private BottomSheetBehavior mSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        NumberFormat mCurFormat = NumberFormat.getCurrencyInstance();
        setupToolbar();

        // retrieve receipt from intent that started this activity
        Intent i = this.getIntent();
        Bundle bundle = i.getExtras();
        mReceipt = (Receipt) bundle.getSerializable(sRECEIPT);
        String mStore = mReceipt.getStore();
        String mDate = mReceipt.getDate();
        String mAmount = mCurFormat.format(mReceipt.getAmount());
        mSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomsheet_receipt));

        // set height of bottom sheet
//        mSheetBehavior.setPeekHeight(150);

        // populate fields in bottom sheet
        TextView mDateView = (TextView) findViewById(R.id.text_receipt_date);
        TextView mStoreView = (TextView) findViewById(R.id.text_receipt_store);
        TextView mAmountView = (TextView) findViewById(R.id.text_receipt_amount);
        mDateView.setText(mDate);
        mStoreView.setText(mStore);
        mAmountView.setText(mAmount);

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Log.d(TAG, mReceipt.getLongitude().toString());
        Log.d(TAG, mReceipt.getLatitude().toString());
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.menu_receipt);
        setSupportActionBar(toolbar);
        // enables a back button to return to BoxActivity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // removes application title
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.bringToFront();
    }

    /**
     * Inflates toolbar menu
     * @param menu  menu object
     * @return      true always
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.collapsing_menu, menu);
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
            case R.id.home:
                // goes back to BoxActivity
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user receives a prompt to install
     * Play services inside the SupportMapFragment. The API invokes this method after the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // add a marker at the receipt's saved location
        // and move map's camera to same location.
        Double longitude = mReceipt.getLongitude();
        Double latitude = mReceipt.getLatitude();
        LatLng here = new LatLng(longitude, latitude);
        if (here != null) {
            Log.d(TAG, here.toString());
            googleMap.addMarker(new MarkerOptions().position(here)
                    .title("Current Location"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(here));
        }
    }

}
