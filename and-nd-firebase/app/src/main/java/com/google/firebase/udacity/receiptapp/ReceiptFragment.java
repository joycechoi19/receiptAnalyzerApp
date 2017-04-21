package com.google.firebase.udacity.receiptapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;

import static com.google.firebase.udacity.receiptapp.BoxActivity.sRECEIPT;

/**
 * Created by helen on 4/20/17.
 * Displays a detailed view of the selected receipt.
 */

public class ReceiptFragment extends Fragment {

    private static Receipt mReceipt;
    private static String mStore;
    private static String mDate;
    private static String mAmount;
    private NumberFormat mCurFormat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurFormat = NumberFormat.getCurrencyInstance();
        mReceipt = (Receipt) getArguments().getSerializable(sRECEIPT);
        mStore = mReceipt.getStore();
        mDate = mReceipt.getDate();
        mAmount = mCurFormat.format(mReceipt.getAmount());
    }


    /**
     * Inflates the layout for this fragment by initializing the
     * various components required, and populates the appropriate
     * fields with data passed from BoxAdapter.
     * @param inflater             defines fragment layout
     * @param container            view to be inflated
     * @param savedInstanceState   data to populate view
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_receipt, container, false);
        TextView mDateView = (TextView) rootView.findViewById(R.id.text_receipt_date);
        TextView mStoreView = (TextView) rootView.findViewById(R.id.text_receipt_store);
        TextView mAmountView = (TextView) rootView.findViewById(R.id.text_receipt_amount);

        mDateView.setText(mDate);
        mStoreView.setText(mStore);
        mAmountView.setText(mAmount);

        return rootView;
    }

}
