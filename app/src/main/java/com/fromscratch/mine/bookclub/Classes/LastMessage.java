package com.fromscratch.mine.bookclub.Classes;

import android.os.Parcel;
import android.os.Parcelable;



public class LastMessage  implements Parcelable{

    public String clubName;
    public String messagebody;
    public String userName;


    protected LastMessage(Parcel in) {
        clubName = in.readString();
        messagebody = in.readString();
        userName = in.readString();
    }

    public LastMessage() {
    }

    public LastMessage(String clubName, String messagebody, String userName) {
        this.clubName = clubName;
        this.messagebody = messagebody;
        this.userName = userName;
    }

    public static final Creator<LastMessage> CREATOR = new Creator<LastMessage>() {
        @Override
        public LastMessage createFromParcel(Parcel in) {
            return new LastMessage(in);
        }

        @Override
        public LastMessage[] newArray(int size) {
            return new LastMessage[size];
        }
    };

    public String getClubName() {
        return clubName;
    }

    public String getMessagebody() {
        return messagebody;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(clubName);
        parcel.writeString(messagebody);
        parcel.writeString(userName);
    }
}
