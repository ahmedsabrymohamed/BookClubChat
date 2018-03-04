package com.fromscratch.mine.bookclub;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.fromscratch.mine.bookclub.Adapters.BooksListAdapter;
import com.fromscratch.mine.bookclub.Adapters.ProfleListAdapter;
import com.fromscratch.mine.bookclub.Classes.BookClub;
import com.fromscratch.mine.bookclub.Classes.UserData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity  {

    private MyBooksFragment.OnSelectedListenerMyBooks mListener;
    private ArrayList<BookClub> bookClubArrayList;
    private String uid;
    private  ProfleListAdapter listAdapter;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    ImageView profileImage;
    TextView name;
    DatabaseReference ref ;
    private static final String USER_ID="Uid";
    private static final String CLUBS_DATA_BRANCH="clubsData";
    private static final String USERS_DATA_BRANCH="UsersData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        bookClubArrayList=new ArrayList<>();
        ref = database.getReference(CLUBS_DATA_BRANCH);
        listAdapter=new  ProfleListAdapter(bookClubArrayList) ;
        RecyclerView recyclerView=findViewById(R.id.myBooks_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);
        profileImage=findViewById(R.id.profile_image);
        name=findViewById(R.id.user_name);
        if(getIntent()!=null)
        {
            uid =getIntent().getStringExtra(USER_ID);
        }
        if(savedInstanceState!=null){
            uid =savedInstanceState.getString(USER_ID);
        }
        getImage();
    }

    public void onStart() {
        super.onStart();


        ref.orderByChild("Users/"+uid).equalTo(true).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                bookClubArrayList.clear();
                for (final DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    BookClub bookClub=postSnapshot.getValue(BookClub.class);
                    bookClub.setClubId(postSnapshot.getKey());
                    bookClubArrayList.add(bookClub);
                    listAdapter.notifyDataSetChanged();


                    //bookClubArrayList.add(new BookClub(postSnapshot.getKey().toString()));
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

               // Log.d("ahmed", "onCancelled: "+databaseError.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {



            case R.id.logout_item:
                AuthUI.getInstance().signOut(this).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        ProfileActivity.this.finish();
                    }
                });
                break;
            case android.R.id.home:
                finish();
                return true;

        }



        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(USER_ID, uid);
    }
   private void  getImage(){
       ref.getRoot().child(USERS_DATA_BRANCH).child(uid)
               .addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       UserData userData = dataSnapshot.getValue(UserData.class);
                       name.setText(userData.getUserName());
                       Picasso.with(ProfileActivity.this).load(userData.getProfileImage()).noFade()
                               .into(profileImage);
                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {
                       Toast.makeText(getApplicationContext(),
                               getResources().getText(R.string.chat_data_error), Toast.LENGTH_LONG)
                               .show();
                   }
               });
   }


}
