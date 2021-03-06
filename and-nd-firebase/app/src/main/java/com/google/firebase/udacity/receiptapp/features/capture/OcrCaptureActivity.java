/*
Copyright 2017 Josh Owsiany

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.google.firebase.udacity.receiptapp.features.capture;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.udacity.receiptapp.R;
import com.google.firebase.udacity.receiptapp.features.receiptbox.BoxActivity;
import com.google.firebase.udacity.receiptapp.shared.Receipt;
import com.google.firebase.udacity.receiptapp.ui.camera.CameraSource;
import com.google.firebase.udacity.receiptapp.ui.camera.CameraSourcePreview;
import com.google.firebase.udacity.receiptapp.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.google.firebase.udacity.receiptapp.features.receiptbox.BoxActivity.sRECEIPTS;
import static com.google.firebase.udacity.receiptapp.features.receiptbox.BoxActivity.sUSERS;


/**
 * Activity for the multi-tracker app.  This app detects text and displays the value with the
 * rear facing camera. During detection overlay graphics are drawn to indicate the position,
 * size, and contents of each TextBlock.
 */
public final class OcrCaptureActivity extends AppCompatActivity {
    private static final String TAG = "OcrCaptureActivity";

    // Intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    //Make arraylist for receipt items
    public ArrayList<String> receipt = new ArrayList<>();
    private EditText storeName;
    private EditText storeDate;
    private EditText storeAddress;
    private EditText totalCost;

    // Permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    // Constants used to pass extra data in the intent
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    public static final String TextBlockObject = "String";

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<OcrGraphic> mGraphicOverlay;

    // Helper objects for detecting taps and pinches.
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private TextToSpeech tts;

    // Firebase variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserID;

    /**
     * Initializes the UI and creates the detector pipeline.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_ocr_capture);

        // BEGIN FIREBASE INITIALIZATION
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserID = mFirebaseUser.getUid();
        // END FIREBASE INITIALIZATION

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay<OcrGraphic>) findViewById(R.id.graphicOverlay);

        // read parameters from the intent used to launch the activity.
        boolean autoFocus = getIntent().getBooleanExtra(AutoFocus, false);
        boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocus, useFlash);
        } else {
            requestCameraPermission();
        }

        gestureDetector = new GestureDetector(this, new CaptureGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        TextToSpeech.OnInitListener listener =
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(final int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            Log.d("TTS", "Text to speech engine started successfully.");
                            tts.setLanguage(Locale.US);
                        } else {
                            Log.d("TTS", "Error starting the text to speech engine.");
                        }
                    }
                };
        tts = new TextToSpeech(this.getApplicationContext(), listener);
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.text_permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.text_ok, listener)
                .show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean b = scaleGestureDetector.onTouchEvent(e);

        boolean c = gestureDetector.onTouchEvent(e);

        return b || c || super.onTouchEvent(e);
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the ocr detector to detect small text samples
     * at long distances.
     *
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();

        // A text recognizer is created to find text.  An associated processor instance
        // is set to receive the text recognition results and display graphics for each text block
        // on screen.
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        textRecognizer.setProcessor(new OcrDetectorProcessor(mGraphicOverlay));

        if (!textRecognizer.isOperational()) {
            // Note: The first time that an app using a Vision API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any text,
            // barcodes, or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.text_low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.text_low_storage_error));
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the text recognizer to detect small pieces of text.
        mCameraSource =
                new CameraSource.Builder(getApplicationContext(), textRecognizer)
                        .setFacing(CameraSource.CAMERA_FACING_BACK)
                        .setRequestedPreviewSize(1280, 1024)
                        .setRequestedFps(15.0f)
                        .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                        .setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null)
                        .build();
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // We have permission, so create the camerasource
            boolean autoFocus = getIntent().getBooleanExtra(AutoFocus,false);
            boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);
            createCameraSource(autoFocus, useFlash);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.text_no_camera_permission)
                .setPositiveButton(R.string.text_ok, listener)
                .show();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // Check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    /**
     * onTap is called to capture the first TextBlock under the tap location and return it to
     * the Initializing Activity.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the activity is ending.
     */

    private boolean onTap(float rawX, float rawY) {
        OcrGraphic graphic = mGraphicOverlay.getGraphicAtLocation(rawX, rawY);
        TextBlock text = null;

        //int i = 0;
        //could use a while true loop here? then have an if statement inside with a counter
        //that calls on a different method when prompted
        if (graphic != null) {
            text = graphic.getTextBlock();
            if (text != null && text.getValue() != null) {
                receipt.add(text.getValue());
                Log.d(TAG, "receipt is:" + receipt.toString());
                Log.d(TAG, "text tapped is:" + text.getValue());
                // 0) tap store name 1) "tap the address of the store",
                // 2) tap total price and save them in an array

            }
            else {
                Log.d(TAG, "text data is null");
            }
        }
        else {
            Log.d(TAG,"no text detected");
        }
        return text != null;
    }


    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        /**
         * Responds to scaling events for a gesture in progress.
         * Reported by pointer motion.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should consider this event
         * as handled. If an event was not handled, the detector
         * will continue to accumulate movement until an event is
         * handled. This can be useful if an application, for example,
         * only wants to update scaling factors if the change is
         * greater than 0.01.
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        /**
         * Responds to the beginning of a scaling gesture. Reported by
         * new pointers going down.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should continue recognizing
         * this gesture. For example, if a gesture is beginning
         * with a focal point outside of a region where it makes
         * sense, onScaleBegin() may return false to ignore the
         * rest of the gesture.
         */
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        /**
         * Responds to the end of a scale gesture. Reported by existing
         * pointers going up.
         * <p/>
         * Once a scale has ended, {@link ScaleGestureDetector#getFocusX()}
         * and {@link ScaleGestureDetector#getFocusY()} will return focal point
         * of the pointers remaining on the screen.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         */
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mCameraSource.doZoom(detector.getScaleFactor());
        }
    }

    /**
     * changes layout to info editing screen and initializes appropriate
     * view references for checking input and saving to database.
     * @param v  the view of this activity
     */
    public void onButtonClick(View v){

        setContentView(R.layout.activity_info_editor);

        // generate references to views
        storeName = (EditText) findViewById(R.id.store_name);
        storeDate = (EditText) findViewById(R.id.store_date);
        storeAddress = (EditText) findViewById(R.id.store_address);
        totalCost = (EditText) findViewById(R.id.total_cost);

        if (receipt.size() == 1) {
            storeName.setText(receipt.get(0));
            storeAddress.setText(R.string.hint_receipt_address);
        } else if (receipt.size() == 2) {
            storeName.setText(receipt.get(0));
            storeDate.setText(receipt.get(1));
            storeAddress.setText(R.string.hint_receipt_address);
        } else if (receipt.size() == 3){
            storeName.setText(receipt.get(0));
            storeDate.setText(receipt.get(1));
            storeAddress.setText(receipt.get(2));

        } else if (receipt.size() == 4) {
            storeName.setText(receipt.get(0));
            storeDate.setText(receipt.get(1));
            storeAddress.setText(receipt.get(2));
            totalCost.setText(receipt.get(3));
        }
    }
    /**
     * Once the save button is clicked, this method takes the input
     * and creates a Receipt object, which is then saved to the database.
     * After pushing the data, the method returns to BoxActivity.
     * @param v  the current View that is being displayed
     */
    public void onClicker(View v) throws IOException {
        int validate = validateReceipt();
        if(validate < 0) {
            return;
        } else {
            String key = mDatabase.child(sUSERS).child(mUserID).child(sRECEIPTS)
                    .push().getKey();
            String path = "/" + sUSERS + "/" + mUserID + "/" + sRECEIPTS + "/";
            // create a Receipt object from the entered data
            Receipt curReceipt = processReceipt(receipt);
            // serialize the Receipt object to store in database
            Map<String, Object> addReceipt = curReceipt.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(path + key, addReceipt);
            // update the database
            mDatabase.updateChildren(childUpdates);
            // go back to BoxActivity
            Intent i = new Intent(OcrCaptureActivity.this,
                    //need to name class below
                    PhotoIntentActivity.class);
//            Intent i = new Intent(OcrCaptureActivity.this,
//                    //need to name class below
//                    BoxActivity.class);
            startActivity(i);
        }
    }
    /**
     * This method creates a Receipt object from the input data
     * that was saved in an ArrayList. Only called if all required
     * fields have been filled out by user.
     * @param r   the ArrayList from the editing screen
     * @return    a Receipt object generated from the ArrayList
     */
    Receipt processReceipt(ArrayList<String> r) throws IOException {
        String name = storeName.getText().toString();
        String date = storeDate.getText().toString();
        String addr = storeAddress.getText().toString();
        Double cost = Double.parseDouble(totalCost.getText().toString());
        ArrayList<Double> latlng = getLocationFromAddress(addr, getApplicationContext());
        if (latlng == null || latlng.size() == 0) {
            return new Receipt(date, name, addr, cost, 0.0, 0.0);
        }
        return new Receipt(date, name, addr, cost, latlng.get(0), latlng.get(1));
    }

    /**
     * Upon pressing the "save receipt" button, we check all the inputs
     * to make sure they are valid. If invalid, error message displayed
     * in applicable fields and return -1. If all is well, return 0.
     * note : we do not check for address input field, as this is optional
     * @return  -1 or 0 depending on validation results
     */
    private int validateReceipt() {
        int ret = 0;
        if (storeName.getText().toString().trim().isEmpty()) {
            TextInputLayout layout = (TextInputLayout) findViewById(R.id.input_layout_name);
            layout.setError(getString(R.string.error_receipt_store));
            ret = -1;
        }
        if (storeDate.getText().toString().trim().isEmpty()) {
            TextInputLayout layout = (TextInputLayout) findViewById(R.id.input_layout_date);
            layout.setError(getString(R.string.error_receipt_date));
            ret = -1;
        }
        if (totalCost.getText().toString().trim().isEmpty()) {
            TextInputLayout layout = (TextInputLayout) findViewById(R.id.input_layout_amount);
            layout.setError(getString(R.string.error_receipt_amount));
            ret = -1;
        }
        return ret;
    }
    /**
     * returns LatLng using geocoder
     *
     *
     * @param addr    String address of store
     * @param context Context of the activity
     * @return ret    LatLng object, null if invalid address
     */
    public ArrayList<Double> getLocationFromAddress(String addr, Context context) throws IOException {
        ArrayList<Double> ret = new ArrayList<>();
        //Create coder with Activity context - this
        Geocoder geocoder = new Geocoder(context);
        ArrayList<Address> address = (ArrayList<Address>) geocoder.getFromLocationName(addr, 1);
        Log.d(TAG, "calling getLocationFromAddress");
        if (address != null && address.size() > 0) {
            Address add = address.get(0);
            if (add != null) {
                //get latLng from String
                ret.add(add.getLongitude());
                ret.add(add.getLatitude());
                Log.d(TAG, "LatLng :" + ret.toString());
                return ret;
            }
        }
        else {
            Log.d(TAG, "null LatLng");
            ret = null;
        }
        return ret;
    }
}
