package com.fromscratch.mine.bookclub.Classes;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;


public class AppStart extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }


}