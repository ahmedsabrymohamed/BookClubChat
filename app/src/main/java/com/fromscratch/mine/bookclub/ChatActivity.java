package com.fromscratch.mine.bookclub;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CheckableImageButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.fromscratch.mine.bookclub.Adapters.ChatDataAdapter;
import com.fromscratch.mine.bookclub.Classes.APIClient;
import com.fromscratch.mine.bookclub.Classes.ApiInterface;
import com.fromscratch.mine.bookclub.Classes.ChatMessage;
import com.fromscratch.mine.bookclub.Classes.LastMessage;
import com.fromscratch.mine.bookclub.Classes.Response;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import retrofit2.Call;

public class ChatActivity extends AppCompatActivity {

    private static final int RC_PHOTO_PICKER = 2;
    private static final String CHAT_ID = "chatID";
    private static final String CHAT_NAME = "chatName";
    private static final String LIST_POSITION = "List Positon";
    private static final String LAST_MESSAGE_BRANCH = "LastMessage";
    private static final String CLUBS_EXTRA_DATA_BRANCH = "clubsExtraData";
    private static final String CLUBS_CHAT_BRANCH = "clubsChat";
    private static final String CHAT_PHOTO_BRANCH = "chat_photos";
    private static final String USER_ID = "Uid";
    String bookClubId;
    String bookClubName;
    ChatDataAdapter listAdapter;
    EditText body;
    LinearLayoutManager layoutManager;
    CheckableImageButton send;
    int position;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private RecyclerView chatList;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        body = findViewById(R.id.body);
        send = findViewById(R.id.send);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child(CHAT_PHOTO_BRANCH);
        body.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                send.setEnabled(!(body.getText().toString().trim().isEmpty()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        listAdapter = new ChatDataAdapter(this);
        position = 0;
        if (getIntent() != null) {
            bookClubId = getIntent().getStringExtra(CHAT_ID);
            bookClubName = getIntent().getStringExtra(CHAT_NAME);
        }
        if (savedInstanceState != null) {
            bookClubId = savedInstanceState.getString(CHAT_ID);
            bookClubName = savedInstanceState.getString(CHAT_NAME);
            position = savedInstanceState.getInt(LIST_POSITION);

        }
        getSupportActionBar().setTitle(bookClubName);
        mDatabase = FirebaseDatabase.getInstance().getReference().child(CLUBS_CHAT_BRANCH)
                .child(bookClubId);

        chatList = findViewById(R.id.chatList);

        chatList.setAdapter(listAdapter);
        layoutManager = new LinearLayoutManager(this);
        chatList.setLayoutManager(layoutManager);
        chatList.setHasFixedSize(true);
        getData();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putString(CHAT_ID, bookClubId);
        outState.putString(CHAT_NAME, bookClubName);
        outState.putInt(LIST_POSITION, layoutManager.findFirstVisibleItemPosition());
        //Log.d("ahmed123", "onChildAdded: "+position);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedPhoto = data.getData();
            Toast.makeText(getApplicationContext(), getResources()
                    .getText(R.string.uploading_image_message), Toast.LENGTH_LONG)
                    .show();
            UploadTask uploadTask = storageReference.child(selectedPhoto.getLastPathSegment()).putFile(selectedPhoto);

// Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Toast.makeText(getApplicationContext(), getResources().getText(R.string.finish_uploading_image_message)
                            , Toast.LENGTH_LONG)
                            .show();
                    sendPhotoData(downloadUrl);
                }
            });
        }
    }

    public void send(View view) {
        String key = mDatabase.push().getKey();
        mDatabase.push()
                .setValue(new ChatMessage(key, body.getText().toString(), mAuth.getUid(),
                        ServerValue.TIMESTAMP, false));
        mDatabase.getRoot().child(CLUBS_EXTRA_DATA_BRANCH).child(bookClubId).child(LAST_MESSAGE_BRANCH)
                .setValue(new LastMessage(bookClubName, body.getText().toString()
                        , mAuth.getCurrentUser().getDisplayName()));
        sendNotification(mAuth.getCurrentUser().getDisplayName()
                , body.getText().toString()
                , bookClubId
                , mAuth.getUid()
                , bookClubName);
        body.setText("");
    }

    private void sendPhotoData(Uri url) {
        String key = mDatabase.push().getKey();
        mDatabase.push()
                .setValue(new ChatMessage(key, url.toString(), mAuth.getUid(),
                        ServerValue.TIMESTAMP, true));

        mDatabase.getRoot().child(CLUBS_EXTRA_DATA_BRANCH).child(bookClubId).child(LAST_MESSAGE_BRANCH)
                .setValue(new LastMessage(bookClubName, getResources().getString(R.string.notification_message_image_sent)
                        , mAuth.getCurrentUser().getDisplayName()));

        sendNotification(mAuth.getCurrentUser().getDisplayName()
                , getResources().getString(R.string.notification_message_image_sent)
                , bookClubId
                , mAuth.getUid()
                , bookClubName);
        body.setText("");
    }

    public void getData() {
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                listAdapter.insertItem(dataSnapshot.getValue(ChatMessage.class));
                if (listAdapter.getItemCount() > position) {
                    chatList.scrollToPosition(position);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    public void pickPhoto(View view) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using")
                , RC_PHOTO_PICKER);
    }

    private void sendNotification(String senderName
            , String message
            , String chatID
            , String senderUid
            , String chatName) {

        ApiInterface apiService =
                APIClient.getClient().create(ApiInterface.class);
        Call<Response> call = apiService.send(senderName
                , message
                , chatID
                , senderUid
                , chatName);
        call.enqueue(new retrofit2.Callback<Response>() {
            @Override
            public void onResponse(@NonNull Call<Response> call
                    , @NonNull retrofit2.Response<Response> response) {
                Toast.makeText(getApplicationContext(), getResources().getText(R.string.message_sent)
                        , Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Call<Response> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


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
                        ChatActivity.this.finish();
                    }
                });
                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

        }


        return true;
    }

}
