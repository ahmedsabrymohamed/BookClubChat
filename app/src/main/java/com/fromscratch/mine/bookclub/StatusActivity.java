package com.fromscratch.mine.bookclub;

import android.app.SearchManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.fromscratch.mine.bookclub.Adapters.PagerAdapter;
import com.fromscratch.mine.bookclub.Classes.BookClub;
import com.fromscratch.mine.bookclub.Classes.LastMessage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

public class StatusActivity extends AppCompatActivity
        implements NewBooksFragment.OnSelectedListenerNewBooks, MyBooksFragment.OnSelectedListenerMyBooks {

    private ViewPager viewPager;
    private static final String BOOK_CLUBS = "BookClubs";
    private PagerAdapter pagerAdapter;
    private static final String MY_BOOKS_KEY = "NewBooksFragment";
    private static final String NEW_BOOKS_KEY = "MyBooksFragment";
    private static final String LAST_MESSAGE_BRANCH="LastMessage";
    private static final String CLUBS_EXTRA_DATA_BRANCH="clubsExtraData";
    private static final String USERS_BRANCH="Users";
    private static final String CHAT_ID="chatID";
    private static final String CHAT_NAME="chatName";
    private static final String CLUBS_DATA_BRANCH="clubsData";
    private static final String SHOW_BUTTON="show";
    private static final String PAGER_POSITION="pagerPosition";
    private static final String USER_ID="Uid";
    private FloatingActionButton addFab, deleteFab;
    private int pagerPosition;
    private DatabaseReference mDatabase;
    private ArrayList<BookClub> newBooksList;
    private ArrayList<BookClub> myBooksList;
    private FirebaseAuth mAuth;
    private boolean show;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pagerPosition = 0;
        newBooksList = new ArrayList<>();
        myBooksList = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("clubsData");
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_status);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewPager = findViewById(R.id.pager);
        show = true;

        if (savedInstanceState != null) {
            show = savedInstanceState.getBoolean(SHOW_BUTTON);
            pagerPosition = savedInstanceState.getInt(PAGER_POSITION);
            newBooksList=savedInstanceState.getParcelableArrayList(NEW_BOOKS_KEY);
            myBooksList=savedInstanceState.getParcelableArrayList(MY_BOOKS_KEY);

        }
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);



        addFab = findViewById(R.id.add_floatingActionButton);
        deleteFab = findViewById(R.id.delete_floatingActionButton);
        deleteFab.hide();

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                pagerPosition = position;
                if (position == 0) {
                    addFab.show();
                    deleteFab.hide();
                } else {
                    if (show) {
                        addFab.show();
                        deleteFab.hide();
                    } else {
                        addFab.hide();
                        deleteFab.show();
                    }


                }
            }
        });
        updateWidget();

    }


    @Override
    public void onSelectedChangeMyBooks(HashMap<String, BookClub> bookClubsSelected) {
        if (bookClubsSelected.isEmpty()) {
            show = true;
            addFab.show();
            deleteFab.hide();

        } else {
            show = false;
            addFab.hide();
            deleteFab.show();

        }
        myBooksList = new ArrayList<>(bookClubsSelected.values());
    }

    @Override
    public void onBookClubClicked(BookClub bookClub) {
        startActivity((new Intent(this, ChatActivity.class))
                .putExtra(CHAT_ID, bookClub.getClubId())
                .putExtra(CHAT_NAME, bookClub.getBookName()));
    }

    @Override
    public void onSelectedChangeNewBooks(HashMap<String, BookClub> bookClubsSelected) {
        newBooksList = new ArrayList<>(bookClubsSelected.values());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putBoolean(SHOW_BUTTON, show);
        outState.putInt(PAGER_POSITION, pagerPosition);
        outState.putParcelableArrayList(NEW_BOOKS_KEY,newBooksList);
        outState.putParcelableArrayList(MY_BOOKS_KEY,myBooksList);
        super.onSaveInstanceState(outState);


    }

    public void addFabClicked(View view) {
        if (pagerPosition == 0 && !newBooksList.isEmpty()) {
            for (BookClub club : newBooksList) {
                mDatabase.getRoot().child(CLUBS_DATA_BRANCH)
                        .child(club.getClubId())
                        .child(USERS_BRANCH)
                        .child(mAuth.getCurrentUser().getUid())
                        .setValue(true);
                mDatabase.getRoot().child(CLUBS_EXTRA_DATA_BRANCH)
                        .child(club.getClubId())
                        .child(USERS_BRANCH)
                        .child(mAuth.getCurrentUser().getUid())
                        .setValue(true);
                FirebaseMessaging.getInstance().subscribeToTopic(club.getClubId());
                //Log.d("ahmed123", "addFabClicked: ");
            }
            newBooksList.clear();
        } else if (pagerPosition == 1) {
            startActivity(new Intent(this, CreateBookActivity.class));
        } else {
            showToast();
        }

    }

    public void deleteFabClicked(View view) {
        if (!myBooksList.isEmpty()) {
            for (BookClub club : myBooksList) {
                mDatabase.getRoot().child(CLUBS_DATA_BRANCH)
                        .child(club.getClubId())
                        .child(USERS_BRANCH)
                        .child(mAuth.getCurrentUser().getUid())
                        .removeValue();
                mDatabase.getRoot().child(CLUBS_EXTRA_DATA_BRANCH)
                        .child(club.getClubId())
                        .child(USERS_BRANCH)
                        .child(mAuth.getCurrentUser().getUid())
                        .removeValue();
                FirebaseMessaging.getInstance().unsubscribeFromTopic(club.getClubId());

            }
            myBooksList.clear();
            show=true;
            addFab.show();
            deleteFab.hide();
        } else {
            showToast();
        }
    }

    private void showToast() {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(this, getResources()
                .getText(R.string.no_books_selected_message), Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.status_menu, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);// Do not iconify the widget; expand it by default
        searchView.setSubmitButtonEnabled(true);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {


            case R.id.profile_item:
                startActivity(new Intent(this, ProfileActivity.class)
                        .putExtra(USER_ID, mAuth.getUid()));
                break;

            case R.id.logout_item:
                AuthUI.getInstance().signOut(this).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        StatusActivity.this.finish();
                    }
                });
                break;

        }


        return true;
    }
    private void updateWidget(){
        mDatabase.getRoot().child(CLUBS_EXTRA_DATA_BRANCH)
                .orderByChild("Users/"+mAuth.getUid())
                .equalTo(true).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<LastMessage>lastMessages=new ArrayList<>();
                for (final DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    if (postSnapshot.child(LAST_MESSAGE_BRANCH).getValue() != null)
                    {
                        lastMessages.add( postSnapshot.child(LAST_MESSAGE_BRANCH)
                                .getValue(LastMessage.class));
                    }
                }
                setWidget(StatusActivity.this.getApplicationContext(),lastMessages);
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

        Intent intent = new Intent(this, AppWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int[] ids = appWidgetManager
                .getAppWidgetIds(new ComponentName(getApplicationContext(), AppWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.widget_chat_list);
        this.sendBroadcast(intent);



    }
}
