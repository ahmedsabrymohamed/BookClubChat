package com.fromscratch.mine.bookclub.Classes;

import android.os.AsyncTask;

import com.fromscratch.mine.bookclub.Adapters.SearchAdapter;

import java.util.ArrayList;


public class SearchTask extends AsyncTask<ArrayList<BookClub>, Void, ArrayList<BookClub>> {

    private String query;

    private SearchAdapter adapter;

    public SearchTask(String query, SearchAdapter adapter) {
        this.query = query;
        this.adapter = adapter;
    }

    @Override
    protected ArrayList<BookClub> doInBackground(ArrayList<BookClub>[] arrayLists) {
        ArrayList<BookClub> clubs = new ArrayList<>();
        for (BookClub club : arrayLists[0]) {
            if (club.getBookName().toLowerCase().contains(query.toLowerCase())) {
                clubs.add(club);
            }
        }
        return clubs;
    }


    @Override
    protected void onPostExecute(ArrayList<BookClub> bookClubArrayList) {
        adapter.addClubs(bookClubArrayList);
        super.onPostExecute(bookClubArrayList);
    }
}
