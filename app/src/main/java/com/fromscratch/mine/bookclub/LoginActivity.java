package com.fromscratch.mine.bookclub;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.auth.AuthUI;

import com.fromscratch.mine.bookclub.Classes.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//import com.twitter.sdk.android.core.Twitter;


import java.util.Arrays;

public class LoginActivity extends AppCompatActivity  {


    static final int GOOGLE_SIGN_IN = 100;
    private FirebaseAuth mAuth;
    String status;
    private DatabaseReference mDatabase;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        //TextView status =findViewById(R.id.status);
        //findViewById(R.id.logout).setOnClickListener(this);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("UsersData");
            if (mAuth.getCurrentUser() != null) {

                startActivity(new Intent(LoginActivity.this
                        , StatusActivity.class));
                finish();
            }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GOOGLE_SIGN_IN&&resultCode == RESULT_OK)
        {

            mDatabase.child(mAuth.getCurrentUser().getUid())
                    .setValue(getUserInfo()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        startActivity(new Intent(LoginActivity.this
                                , StatusActivity.class));
                        finish();
                    }
                    else{
                        Toast.makeText(getApplicationContext()
                                ,task.getException().getMessage(),Toast.LENGTH_LONG)
                                .show();
                    }
                }
            });
        }
        else
        {
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.main_layout),status, Snackbar.LENGTH_LONG);
            snackbar.show();
            //Log.d("ahmed123", "onActivityResult: "+requestCode);
        }
    }


    public void setSignIn(View view){
        updateStatus();
        if( isNetworkAvailable()) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                                    new AuthUI.IdpConfig.FacebookBuilder().build(),
                                    new AuthUI.IdpConfig.TwitterBuilder().build()
                            ))
                            .setIsSmartLockEnabled(false)
                            .build(),
                    GOOGLE_SIGN_IN);
        }
        else{
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.main_layout),status, Snackbar.LENGTH_LONG);
            snackbar.show();
        }


    }

    private void signOut() {


        AuthUI.getInstance()
                .signOut(this).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

               // Log.d("ahmed", "onFail: good");

            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()) {



                  //  Log.d("ahmed", "onComplete: success ");


                }
                else
                {


                   // Log.d("ahmed", "onComplete: good");
                }
            }
        });


    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void updateStatus(){

        if( isNetworkAvailable()) {

            status = String.valueOf(getResources().getText(R.string.sign_in_request_connected));
        }
        else {
            status=String.valueOf(getResources().getText(R.string.sign_in_request_not_connected));
        }
    }

 private UserData getUserInfo(){

        return new UserData(mAuth.getCurrentUser().getUid()
                ,mAuth.getCurrentUser().getDisplayName()
                ,mAuth.getCurrentUser().getPhotoUrl().toString());
 }
}

