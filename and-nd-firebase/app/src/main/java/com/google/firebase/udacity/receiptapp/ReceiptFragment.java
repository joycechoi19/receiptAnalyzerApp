package com.google.firebase.udacity.receiptapp;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.NumberFormat;

import static com.google.firebase.udacity.receiptapp.BoxActivity.sRECEIPT;

/**
 * Created by helen on 4/20/17.
 * Displays a detailed view of the selected receipt,
 * fragment based in BoxActivity screen.
 */

public class ReceiptFragment extends Fragment {

    private static final String DEBUG_TAG = "Gestures";
    private GestureDetector mDetector;

    private static Receipt mReceipt;
    private static String mStore;
    private static String mDate;
    private static String mAmount;

    private ReceiptFragment mFragment;

    /**
     * Gesture listener watches for swipe down action
     * and removes this current fragment if action is
     * initiated by user.
     * Some code sourced and modified from:
     * http://stackoverflow.com/questions/19056693/how-to-properly-implement-onfling-method
     */
    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_MIN_DISTANCE = 80;
        private static final int SWIPE_THRESHOLD_VELOCITY = 50;
        /**
         * onDown override is necessary for GestureDetector to
         * work properly as onDown begins every gesture event
         * @param event motion
         * @return true always
         */
        @Override
        public boolean onDown(MotionEvent event) {
            Log.d(DEBUG_TAG,"onDown: " + event.toString());
            return true;
        }

        /**
         * Detects a vigorous swipe event and removes this fragment
         * if the gesture was a swipe down
         * @param event1      when user first touches screen
         * @param event2      when user's finger leaves screen
         * @param velocityX   velocity in x-direction
         * @param velocityY   velocity in y-direction
         * @return            true always
         */
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d(DEBUG_TAG, "onFling: " + event1.toString()+event2.toString());
            if(event2.getY() - event1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY){
                getActivity().getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.animator.anim_slide_down, R.animator.anim_slide_down)
                        .remove(mFragment)
                        .commit();
            }
            return true;
        }
    }

    /**
     * Creates fragment and populates component views using
     * data from bundle. Also creates gesture detector to
     * detect swipes on screen.
     * @param savedInstanceState   null if not returning from saved
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NumberFormat mCurFormat = NumberFormat.getCurrencyInstance();
        mReceipt = (Receipt) getArguments().getSerializable(sRECEIPT);
        mStore = mReceipt.getStore();
        mDate = mReceipt.getDate();
        mAmount = mCurFormat.format(mReceipt.getAmount());

        mDetector = new GestureDetector(getActivity(),
                new MyGestureListener());
    }


    /**
     * Inflates the layout for this fragment by initializing the
     * various components required, and populates the appropriate
     * fields with data passed from BoxAdapter.
     * @param inflater             defines fragment layout
     * @param container            view to be inflated
     * @param savedInstanceState   data to populate view
     * @return                     the root view for this fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_receipt, container, false);
        TextView mDateView = (TextView) rootView.findViewById(R.id.text_receipt_date);
        TextView mStoreView = (TextView) rootView.findViewById(R.id.text_receipt_store);
        TextView mAmountView = (TextView) rootView.findViewById(R.id.text_receipt_amount);
        Button mLocationBttn = (Button) rootView.findViewById(R.id.mapButton);

        mDateView.setText(mDate);
        mStoreView.setText(mStore);
        mAmountView.setText(mAmount);

        mFragment = this;

        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return mDetector.onTouchEvent(motionEvent);
            }
        });

        mLocationBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), MapsMarkerActivity.class));
            }
        });

        return rootView;
    }




}
