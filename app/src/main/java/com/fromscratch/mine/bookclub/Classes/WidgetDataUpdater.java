package com.fromscratch.mine.bookclub.Classes;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.fromscratch.mine.bookclub.AppWidget;
import com.fromscratch.mine.bookclub.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;


public class WidgetDataUpdater {

    private static final String LAST_MESSAGE_BRANCH = "LastMessage";
    private static final String BOOK_CLUBS = "BookClubs";
    private static final String CLUBS_EXTRA_DATA_BRANCH = "clubsExtraData";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public WidgetDataUpdater() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    public void updateWidget(final Context context) {
        String uid = mAuth.getUid();
        mDatabase.getRoot().child(CLUBS_EXTRA_DATA_BRANCH)
                .orderByChild("Users/" + uid)
                .equalTo(true).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<LastMessage> lastMessages = new ArrayList<>();
                for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (postSnapshot.child(LAST_MESSAGE_BRANCH).getValue() != null) {
                        lastMessages.add(postSnapshot.child(LAST_MESSAGE_BRANCH)
                                .getValue(LastMessage.class));
                    }
                }
                setWidget(context.getApplicationContext(), lastMessages);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setWidget(Context context, ArrayList<LastMessage> lastMessages) {


        Gson gson = new Gson();
        String messages = gson.toJson(lastMessages);
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putString(BOOK_CLUBS, messages).apply();

        AppWidgetManager appWidgetManager = AppWidgetManager
                .getInstance(context);

        Intent intent = new Intent(context, AppWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int[] ids = appWidgetManager
                .getAppWidgetIds(new ComponentName(context.getApplicationContext(), AppWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.widget_chat_list);
        context.sendBroadcast(intent);


    }
}
