package com.fromscratch.mine.bookclub.Classes;

import android.os.Parcel;
import android.os.Parcelable;


public class ChatMessage implements Parcelable {

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel in) {
            return new ChatMessage(in);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };
    public String messageID;
    public String messageBody;
    public String messageAuthorUID;
    public Object timeStamp;
    public boolean image;

    public ChatMessage() {
    }


    public ChatMessage(String messageID, String messageBody, String uid, Object time, boolean image) {
        this.messageID = messageID;
        this.messageBody = messageBody;
        this.messageAuthorUID = uid;
        this.timeStamp = time;
        this.image = image;

    }

    protected ChatMessage(Parcel in) {
        messageID = in.readString();
        messageBody = in.readString();
        messageAuthorUID = in.readString();
        image = in.readByte() != 0;
    }

    /*public Long getTimeStamp(){
            return (Long)timeStamp;
        }
    */

    public boolean isImage() {
        return image;
    }

    public void setImage(boolean image) {
        this.image = image;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getMessageAuthorUID() {
        return messageAuthorUID;
    }

    public void setMessageAuthorUID(String messageAuthorUID) {
        this.messageAuthorUID = messageAuthorUID;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(messageID);
        parcel.writeString(messageBody);
        parcel.writeString(messageAuthorUID);
        parcel.writeByte((byte) (image ? 1 : 0));
    }
}
