package com.fromscratch.mine.bookclub.Classes;

import android.os.Parcel;
import android.os.Parcelable;


public class UserData implements Parcelable {
    public static final Creator<UserData> CREATOR = new Creator<UserData>() {
        @Override
        public UserData createFromParcel(Parcel in) {
            return new UserData(in);
        }

        @Override
        public UserData[] newArray(int size) {
            return new UserData[size];
        }
    };
    public String uid;
    public String userName;
    public String profileImage;

    public UserData(String uid, String userName, String profileImage) {
        this.userName = userName;
        this.profileImage = profileImage;
    }

    public UserData() {
    }

    protected UserData(Parcel in) {
        uid = in.readString();
        userName = in.readString();
        profileImage = in.readString();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(userName);
        parcel.writeString(profileImage);
    }
}
