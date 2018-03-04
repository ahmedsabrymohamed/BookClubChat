package com.fromscratch.mine.bookclub.Classes;

import android.os.Parcel;
import android.os.Parcelable;



public class ClubNotification implements Parcelable {

    private String title;
    private String notificationMessage;
    private String chatID;
    private String chatName;
    private String senderUid;

    protected ClubNotification(Parcel in) {
        title = in.readString();
        notificationMessage = in.readString();
        chatID = in.readString();
        chatName = in.readString();
        senderUid=in.readString();
    }

    public ClubNotification(String title, String notificationMessage, String chatID,String chatName
    ,String senderUid) {
        this.title = title;
        this.notificationMessage = notificationMessage;
        this.chatID = chatID;
        this.chatName=chatName;
        this.senderUid=senderUid;
    }

    public String getChatName() {
        return chatName;
    }



    public String getTitle() {
        return title;
    }


    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public static final Creator<ClubNotification> CREATOR = new Creator<ClubNotification>() {
        @Override
        public ClubNotification createFromParcel(Parcel in) {
            return new ClubNotification(in);
        }

        @Override
        public ClubNotification[] newArray(int size) {
            return new ClubNotification[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(notificationMessage);
        parcel.writeString(chatID);
        parcel.writeString(chatName);
        parcel.writeString(senderUid);
    }
}
