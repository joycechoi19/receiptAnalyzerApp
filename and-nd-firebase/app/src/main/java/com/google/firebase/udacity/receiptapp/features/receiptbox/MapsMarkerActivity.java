package com.google.firebase.udacity.receiptapp.features.receiptbox;

/**
 * Created by joycechoi on 4/28/17.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.udacity.receiptapp.R;
import com.google.firebase.udacity.receiptapp.shared.Receipt;


import static com.google.firebase.udacity.receiptapp.features.receiptbox.ReceiptFragment.sRECEIPTFRAGMENT;

/**
 * An activity that displays a Google map with a marker (pin) to indicate a particular location.
 */
public class MapsMarkerActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private Receipt mReceipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retrieve receipt from intent that started this activity
        Intent i = this.getIntent();
        Bundle bundle = i.getExtras();
        mReceipt = (Receipt) bundle.getSerializable(sRECEIPTFRAGMENT);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        //LatLng sydney = new LatLng(-33.852, 151.211);
        LatLng here = mReceipt.getLatLng();
        if (here != null) {
            Log.d("hello", here.toString());
            googleMap.addMarker(new MarkerOptions().position(here)
                    .title("Current Location"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(here));
        }
    }
}

