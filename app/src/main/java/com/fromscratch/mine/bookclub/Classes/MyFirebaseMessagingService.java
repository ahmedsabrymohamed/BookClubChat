package com.fromscratch.mine.bookclub.Classes;

import android.content.Intent;
import android.util.Log;

import com.fromscratch.mine.bookclub.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String SENDER_UID="senderUid";
    private static final String TITLE="title";
    private static final String MESSAGE="message";
    private static final String CHAT_ID="chatID";
    private static final String CHAT_NAME="chatName";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        if (remoteMessage.getData().size() > 0) {
           // Log.d("ahmed123", "onMessageReceived: "+remoteMessage.getData().get(MESSAGE));
            Map<String, String> data = remoteMessage.getData();
            displayNotification(data);

        }

    }

    private void displayNotification(Map<String, String> data) {

        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        if(true||!mAuth.getUid().equals(data.get(SENDER_UID))||true) {
            MyNotificationManager notificationManager = new MyNotificationManager(getApplicationContext());

            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            notificationManager
                    .displayNotification(new ClubNotification(data.get(TITLE)
                            , data.get(MESSAGE)
                            , data.get(CHAT_ID)
                            , data.get(CHAT_NAME)
                            , data.get(SENDER_UID)), intent);
            notificationManager.playNotificationSound();

        }
    }
}
