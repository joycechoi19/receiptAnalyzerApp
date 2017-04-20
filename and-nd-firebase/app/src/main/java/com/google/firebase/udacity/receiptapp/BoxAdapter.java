package com.google.firebase.udacity.receiptapp;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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
    private ArrayList<Receipt> mReceiptList;
    private NumberFormat mCurFormat;


    /**
     * The ViewHolder provides a refernce to the views for each
     * data item (in our case, a CardView).
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        //        private CardView mCardView;
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
    BoxAdapter(ArrayList<Receipt> myReceiptList) {
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
     * Replaces the contents of a view (invoked by the layout manager)
     * @param holder     ViewHolder created from above method
     * @param position   position in the list of views
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String currency = mCurFormat.format(mReceiptList.get(position).mAmount);
        holder.mStoreView.setText(mReceiptList.get(position).mStore);
        holder.mAmountView.setText(currency);
        holder.mDateView.setText(mReceiptList.get(position).mDate);
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