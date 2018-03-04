package com.fromscratch.mine.bookclub.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.fromscratch.mine.bookclub.Classes.BookClub;
import com.fromscratch.mine.bookclub.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;



public class SearchAdapter extends BaseAdapter {

    // override other abstract methods here
    private static final String CLUBS_DATA_BRANCH="clubsData";
    private static final String CLUBS_EXTRA_DATA_BRANCH="clubsExtraData";
    private static final String USERS_DATA_BRANCH="Users";

    ArrayList<BookClub> clubs;
    Context context;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    public SearchAdapter(ArrayList<BookClub> clubs, Context context) {
        this.clubs = clubs;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public SearchAdapter() {
    }

    @Override
    public int getCount() {
        return clubs.size();
    }

    @Override
    public BookClub getItem(int i) {
        return clubs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            convertView = getLayoutInflater().inflate(R.layout.search_result_item, container
                    , false);
        }
        final BookClub club =  getItem(position);

        ((TextView) convertView.findViewById(R.id.book_name_result))
                .setText(club.getBookName());
        ((TextView) convertView.findViewById(R.id.book_type_result))
                .setText(club.getBookType());
        Button button = ( convertView.findViewById(R.id.action_button_search));
        if (club.isSelected()) {
            button.setText(context.getResources().getText(R.string.leave_button));
            button.setBackgroundColor(context.getResources().getColor(R.color.red));
        } else {
            button.setText(context.getResources().getText(R.string.join_button));
            button.setBackgroundColor(context.getResources().getColor(R.color.Green));
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!club.isSelected()) {
                    joinClub(club);

                } else {
                    leaveClub(club);
                }
                clubs.clear();
            }
        });


        return convertView;
    }

    private LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(context);
    }

    private void joinClub(BookClub club) {
        mDatabase.getRoot().child(CLUBS_DATA_BRANCH)
                .child(club.getClubId())
                .child(USERS_DATA_BRANCH)
                .child(mAuth.getCurrentUser().getUid())
                .setValue(true);
        mDatabase.getRoot().child(CLUBS_DATA_BRANCH)
                .child(club.getClubId())
                .child(USERS_DATA_BRANCH)
                .child(mAuth.getCurrentUser().getUid())
                .setValue(true);
        FirebaseMessaging.getInstance().subscribeToTopic(club.getClubId());
        //Log.d("ahmed123", "addFabClicked: ");
    }

    private void leaveClub(BookClub club) {
        mDatabase.getRoot().child(CLUBS_DATA_BRANCH)
                .child(club.getClubId())
                .child(USERS_DATA_BRANCH)
                .child(mAuth.getCurrentUser().getUid())
                .removeValue();
        mDatabase.getRoot().child(CLUBS_EXTRA_DATA_BRANCH)
                .child(club.getClubId())
                .child(USERS_DATA_BRANCH)
                .child(mAuth.getCurrentUser().getUid())
                .removeValue();
        FirebaseMessaging.getInstance().unsubscribeFromTopic(club.getClubId());

    }

    public void addClubs(ArrayList<BookClub> clubs) {
        this.clubs.addAll(clubs);
        notifyDataSetChanged();
    }
}
