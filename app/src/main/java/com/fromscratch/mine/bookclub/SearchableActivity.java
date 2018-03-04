package com.fromscratch.mine.bookclub;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.fromscratch.mine.bookclub.Adapters.SearchAdapter;
import com.fromscratch.mine.bookclub.Classes.BookClub;
import com.fromscratch.mine.bookclub.Classes.SearchTask;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchableActivity extends AppCompatActivity {

    SearchAdapter searchAdapter;
    private DatabaseReference mDatabase;
    ListView listView;
    String uid;
    Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        listView=findViewById(R.id.search_result_list);
        searchAdapter=new SearchAdapter(new ArrayList<BookClub>(),this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        listView.setAdapter(searchAdapter);
        uid=FirebaseAuth.getInstance().getUid();
        Intent intent = getIntent();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BookClub club=(BookClub)adapterView.getItemAtPosition(i);
                if(club.isSelected())
                    startActivity((new Intent(SearchableActivity.this, ChatActivity.class))
                            .putExtra("club", club.getClubId())
                            .putExtra("clubName", club.getBookName()));
                else
                    showToast();

            }
        });
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }

    }
    private void doMySearch(String query){

       // Log.d("ahmed123", "doMySearch: "+query);
        getMyBookClubs(query);
        getNewBookClubs(query);

    }
    public void getMyBookClubs(final String query){


        mDatabase.getRoot().child("clubsData").orderByChild("Users/"+ uid)
                .equalTo(true).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<BookClub> myBookClubArrayList=new ArrayList<>();
                for (final DataSnapshot postSnapshot: dataSnapshot.getChildren()) {


                    BookClub bookClub=postSnapshot.getValue(BookClub.class);
                    bookClub.setClubId(postSnapshot.getKey());
                    bookClub.setSelected(true);
                    myBookClubArrayList.add(bookClub);

                }
                (new SearchTask(query,searchAdapter)).execute(myBookClubArrayList);
                // Log.d("ahmed123", "onDataChange: "+position);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                //   Log.d("ahmed", "onCancelled: "+databaseError.getMessage());
            }
        });
    }
    private void getNewBookClubs(final String query){
        mDatabase.getRoot().child("clubsData").orderByChild("Users/"+uid)
                .equalTo(null).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<BookClub> newBookClubArrayList=new ArrayList<>();
                for (final DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    BookClub bookClub=postSnapshot.getValue(BookClub.class);
                    bookClub.setClubId(postSnapshot.getKey());
                    bookClub.setSelected(false);
                    newBookClubArrayList.add(bookClub);


                }
                (new SearchTask(query,searchAdapter)).execute(newBookClubArrayList);
                //  Log.d("ahmed123", "onDataChange: "+position);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                // Log.d("ahmed", "onCancelled: "+databaseError.getMessage());
            }
        });
    }
    private void showToast() {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(this, "Your are not in this Club", Toast.LENGTH_LONG);
        toast.show();
    }
}
