package com.google.firebase.udacity.receiptapp;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by helen on 4/13/17.
 * Adapter handles displaying each receipt in a CardView.
 * Each CardView is clickable for a detailed view of the receipt.
 */

class BoxAdapter extends RecyclerView.Adapter<BoxAdapter.ViewHolder> {
    private ArrayList<Receipt> mReceiptList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        //        private CardView mCardView;
        private TextView mStoreView;
        private TextView mAmountView;
        private TextView mDateView;

        ViewHolder(View v) {
            super(v);
//            mCardView = (CardView) v.findViewById(R.id.view_card_receipt);
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

    // constructor
    BoxAdapter(ArrayList<Receipt> myReceiptList) {
        mReceiptList = myReceiptList;
    }

    // Create new views (invoked by the layout manager)
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

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mStoreView.setText(mReceiptList.get(position).mStore);
        holder.mAmountView.setText(Double.toString(mReceiptList.get(position).mAmount));
        holder.mDateView.setText(mReceiptList.get(position).mDate);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mReceiptList.size();
    }
}