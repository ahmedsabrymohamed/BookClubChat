package com.fromscratch.mine.bookclub;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.fromscratch.mine.bookclub.Classes.BookClub;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class CreateBookActivity extends AppCompatActivity {

    private static final String CLUBS_DATA_BRANCH = "clubsData";
    private static final String USER_ID = "Uid";
    private static final String USERS_BRANCH = "Users";
    private static final String CLUBS_EXTRA_DATA_BRANCH = "clubsExtraData";
    EditText bookName, bookType;
    Toast toast;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_book);
        mDatabase = FirebaseDatabase.getInstance().getReference().child(CLUBS_DATA_BRANCH);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        bookName = findViewById(R.id.book_name_editText);
        bookType = findViewById(R.id.book_type_editText);

    }

    public void createBook(View view) {
        if (!bookName.getText().toString().trim().isEmpty() &&
                !bookType.getText().toString().trim().isEmpty()) {
            String key = mDatabase.push().getKey();
            mDatabase.child(key).setValue(new BookClub(bookName.getText().toString()
                    , bookType.getText().toString(), key));
            mDatabase.child(key)
                    .child(USERS_BRANCH)
                    .child(mAuth.getCurrentUser().getUid())
                    .setValue(true);
            mDatabase.getRoot().child(CLUBS_EXTRA_DATA_BRANCH)
                    .child(key)
                    .child(USERS_BRANCH)
                    .child(mAuth.getCurrentUser().getUid())
                    .setValue(true);
            FirebaseMessaging.getInstance().subscribeToTopic(key);
            bookName.setText("");
            bookType.setText("");
            showToast(getResources().getString(R.string.club_created_message));
        } else {
            showToast(getResources().getString(R.string.empty_fields_message));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.menu, menu);
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
                        CreateBookActivity.this.finish();
                    }
                });
                break;
            case android.R.id.home:
                finish();
                return true;

        }


        return true;
    }

    private void showToast(String message) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }
}
