package com.google.firebase.udacity.receiptapp.app;

import android.app.Application;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.MaterialModule;

/**
 * Created by helen on 4/20/17.
 * Extension of the Application class allows us to set up
 * objects that last during the full lifecycle of the Application.
 * Here we add an icon library that lets us add drawable icons
 * to our menu that scale with resolution.
 */

public class ReceiptAnalyze extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // set up icon library
        Iconify.with(new MaterialModule());

    }
}
