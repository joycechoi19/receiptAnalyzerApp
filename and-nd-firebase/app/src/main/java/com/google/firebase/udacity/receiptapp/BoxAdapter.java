package com.google.firebase.udacity.receiptapp;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by helen on 4/13/17.
 * Adapter handles displaying each receipt in a CardView.
 * Each CardView is clickable for a detailed view of the receipt.
 */

class BoxAdapter extends RecyclerView.Adapter<BoxAdapter.ViewHolder> {
    private static final String TAG = "BoxAdapter";

    private ArrayList<Receipt> mReceiptList;
    private NumberFormat mCurFormat;

    private OnChoiceSelectedListener mCallBack;

    interface OnChoiceSelectedListener {
        void onChoiceSelected(int index);
    }

    /**
     * The ViewHolder provides a refernce to the views for each
     * data item (in our case, a CardView).
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView mCardView;
        private TextView mStoreView;
        private TextView mAmountView;
        private TextView mDateView;

        /**
         * Constructor for ViewHolder provides reference to
         * all the component views
         * @param v  Overarching view for item (cardview)
         */
        ViewHolder(View v) {
            super(v);
            mCardView = (CardView) v.findViewById(R.id.view_card_receipt);
            mStoreView = (TextView) v.findViewById(R.id.text_receipt_store);
            mAmountView = (TextView) v.findViewById(R.id.text_receipt_amount);
            mDateView = (TextView) v.findViewById(R.id.text_receipt_date);
        }
    }

    // Not sure why we need this method but keeping it in
    // just in case it's useful for debugging later
    public void add(int position, Receipt r) {
        mReceiptList.add(position, r);
        notifyItemInserted(position);
    }

    /**
     * Constructor for adapter instantiates a currency formatter
     * and a reference to the list of items used to create the
     * list of cardviews.
     * @param myReceiptList  ArrayList of items to be presented in the view
     */
    BoxAdapter(OnChoiceSelectedListener callback, ArrayList<Receipt> myReceiptList) {
        mCallBack = callback;
        mCurFormat = NumberFormat.getCurrencyInstance();
        mReceiptList = myReceiptList;
    }


    /**
     * Creates new Views (invoked by the layout manager)
     * @param parent     The parent view container
     * @param viewType
     * @return ViewHolder for use by the adapter
     */
    @Override
    public BoxAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                    int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_receipt, parent, false);
        // set the view's size, margins, etc
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    /**
     * Replaces the contents of a view (invoked by the layout manager) by
     * getting element from dataset at given position and replacing contents
     * of the viewholder with that element.
     * @param holder     ViewHolder created from above method
     * @param position   position in the list of views
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int idx = position;
        // usage of textformatter ensures that money is displayed
        // in the phone's desired locale
        String currency = mCurFormat.format(mReceiptList.get(position).getAmount());
        holder.mStoreView.setText(mReceiptList.get(position).getStore());
        holder.mAmountView.setText(currency);
        holder.mDateView.setText(mReceiptList.get(position).getDate());
        // onClickListener calls detailed receipt view (fragment)
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "position of item selected is: " + idx);
                mCallBack.onChoiceSelected(idx);
            }
        });
    }

    /**
     * Returns the size of the dataset (invoked by layout manager)
     * @return integer size
     */
    @Override
    public int getItemCount() {
        return mReceiptList.size();
    }
}