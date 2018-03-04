package com.fromscratch.mine.bookclub.Classes;

import android.os.Parcel;
import android.os.Parcelable;



public class BookClub  implements Parcelable{

    public String bookName;
    public String bookType;
    public String clubId;
    private boolean selected;

    public BookClub(){

    }
    public BookClub(String bookName, String bookType , String clubId) {
        this.bookName = bookName;
        this.bookType = bookType;
        this.clubId = clubId;
        this.selected=false;
    }


    protected BookClub(Parcel in) {
        bookName = in.readString();
        bookType = in.readString();
        clubId = in.readString();
        selected = in.readByte() != 0;
    }

    public static final Creator<BookClub> CREATOR = new Creator<BookClub>() {
        @Override
        public BookClub createFromParcel(Parcel in) {
            return new BookClub(in);
        }

        @Override
        public BookClub[] newArray(int size) {
            return new BookClub[size];
        }
    };

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(bookName);
        parcel.writeString(bookType);
        parcel.writeString(clubId);
        parcel.writeByte((byte) (selected ? 1 : 0));
    }
}
