package com.fromscratch.mine.bookclub.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.fromscratch.mine.bookclub.Classes.BookClub;
import com.fromscratch.mine.bookclub.MyBooksFragment;
import com.fromscratch.mine.bookclub.NewBooksFragment;
import com.fromscratch.mine.bookclub.R;
import com.google.firebase.auth.FirebaseAuth;
import com.twitter.sdk.android.core.models.TwitterCollection;

import java.util.ArrayList;


public class PagerAdapter extends FragmentStatePagerAdapter {
    private static final int FRAGMENTS_NUM = 2;
    private String titles[];
    private FirebaseAuth mAuth;


    public PagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mAuth = FirebaseAuth.getInstance();
        titles = context.getResources().getStringArray(R.array.fragmentsTitle);
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return NewBooksFragment.newInstance(mAuth.getCurrentUser().getUid());
            case 1:
                return MyBooksFragment.newInstance(mAuth.getCurrentUser().getUid());
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return FRAGMENTS_NUM;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }


}
