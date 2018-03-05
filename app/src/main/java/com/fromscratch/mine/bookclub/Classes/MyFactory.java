package com.fromscratch.mine.bookclub.Classes;


import android.content.Context;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.fromscratch.mine.bookclub.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

class MyFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String BOOK_CLUBS = "BookClubs";
    private final Context mContext;
    public ArrayList<LastMessage> listItemList;
    private DatabaseReference ref;

    MyFactory(Context mContext) {

        this.mContext = mContext;
        Gson gson = new Gson();


        String jsonClubs = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(BOOK_CLUBS, null);

        Type listType = new TypeToken<ArrayList<LastMessage>>() {
        }.getType();

        ArrayList<LastMessage> lastMessages = gson.fromJson(jsonClubs, listType);

        if (jsonClubs == null)
            listItemList = new ArrayList<>();
        else {
            listItemList = lastMessages;
        }
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        Gson gson = new Gson();

        String jsonClubs = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(BOOK_CLUBS, null);

        Type listType = new TypeToken<ArrayList<LastMessage>>() {
        }.getType();

        ArrayList<LastMessage> lastMessages = gson.fromJson(jsonClubs, listType);

        if (jsonClubs == null)
            listItemList = new ArrayList<>();
        else {
            listItemList = lastMessages;
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (listItemList == null)
            return 0;
        return listItemList.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        final RemoteViews remoteView = new RemoteViews(
                mContext.getPackageName(), R.layout.widget_item);
        if (listItemList != null && FirebaseAuth.getInstance().getCurrentUser() != null) {

            remoteView.setTextViewText(R.id.widget_club_name, listItemList.get(i).getClubName());
            remoteView.setTextViewText(R.id.widget_club_message, listItemList.get(i).getMessagebody());
            remoteView.setTextViewText(R.id.widget_sender_name, listItemList.get(i).getUserName());

        }
        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}